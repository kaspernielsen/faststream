/*
 * Copyright (c) 2008 Kasper Nielsen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.faststream.sisyphus.spi;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import io.faststream.sisyphus.builder.TestBuilder;
import io.faststream.sisyphus.expected.Expected;
import io.faststream.sisyphus.spi.ControlErrors.GenerateNewBatch;
import io.faststream.sisyphus.spi.method.TestMethod;
import io.faststream.sisyphus.spi.method.TestSelector;
import io.faststream.sisyphus.util.RandomSource;

/**
 * 
 * @author Kasper Nielsen
 */
class BatchRunner implements Runnable, TestCaseContext {

    /** The builder used for creating new tests sets. */
    @SuppressWarnings("rawtypes")
    private final TestBuilder builder;

    private final long initialSeed;

    final AtomicLong operationsFinished = new AtomicLong();

    final AtomicLong remaining;

    final InternalTestResult result;

    private Expected rootExpected;

    boolean isStream;

    int depth;

    int i;

    BatchRunner(AtomicLong remaining, InternalTestResult run, TestBuilder<?, ?> testConfiguration, long initialSeed) {
        this.remaining = remaining;
        this.builder = testConfiguration;
        this.result = run;
        this.initialSeed = initialSeed;
    }

    private boolean abortNestedStep(RandomSource rnd, int viewDepth, boolean isStream) {
        if (isStream) {
            return false;
        }
        if (viewDepth == 0) {
            if (rnd.nextInt(10000) == 0) {
                // return false;
                ControlErrors.throwNewBatch();
            } else {
                return false;
            }
        }
        double gaussian = Math.abs(rnd.nextGaussian() * StrictMath.log(viewDepth + 1));
        return gaussian > 1.5; // 8; // 1.8;
    }

    private boolean keepRunning() {
        long r = remaining.getAndDecrement();
        if (r > 0) {
            return true;
        }
        // try steal
        BatchRunner[] runners = result.runners;
        int j = ThreadLocalRandom.current().nextInt(runners.length);// steal from a random worker
        for (int i = 0; i < runners.length; i++) {
            BatchRunner run = runners[(j + i) % runners.length];
            for (;;) {
                long rem = run.remaining.get();
                if (rem < 100) {
                    break;
                }
                long take = rem / 2;
                if (run.remaining.compareAndSet(rem, take)) {
                    remaining.addAndGet(rem - take);
                    return true;
                }
            }
        }
        return false;// no more work
    }

    public static String spaces(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void nextRun(RandomSource rnd, int depth, Expected expected, Object actual) {
        this.depth = depth;

        Class<?> actualClass = actual.getClass();
        TestSelector dts = null;
        Function<Random, TestMethod> am = null;
        while (keepRunning()) {
            TestSelector ts = builder.getSelector(rnd, rootExpected);
            if (dts != ts) {
                am = (dts = ts).nextMethod(actualClass);
            }
            TestMethod m = am.apply(rnd);
            TestCase o = m.newTestObject();
            o.context = this;
            o.expected = expected;
            o.actual = actual;
            long seed = rnd.getSeed();
            try {
                this.i += 1;

                // System.out.println(spaces(2 * depth) + " -> " + m.getMethod());
                m.invoke(o);
            } catch (ControlErrors.GenerateNewBatch | ControlErrors.ResetToMark b) {
                operationsFinished.incrementAndGet();// we did finish this operation
                throw b;
            } catch (Throwable t) {
                System.out.println("Iteration = " + result.iterations);
                System.out.println("Seed = " + seed);
                System.out.println("InitialSeed = " + initialSeed);
                result.addFailure(m, seed, t, o.expected(), o.actual(), initialSeed);
            }
            operationsFinished.incrementAndGet();
            if (o.mark) {
                isStream = true;
                if (abortNestedStep(rnd, depth, false)) {
                    isStream = false;
                    return;
                }
            } else {
                if (abortNestedStep(rnd, depth, isStream)) {
                    return;
                }
            }
            if (o.nestedActual != null) {// A nested test has been set
                try {
                    nextRun(rnd, depth + 1, o.nestedExpected, o.nestedActual);
                } catch (InternalReproducibleException e) {
                    e.add(seed, m);
                    throw e;
                } catch (ControlErrors.ResetToMark e) {
                    if (!o.mark) {
                        throw e;
                    } else {
                        isStream = false;
                    }
                }
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void run() {
        RandomSource rnd = RandomSource.current();
        rnd.setSeed(initialSeed);
        for (;;) {
            TestBuilder.Bootstrap f = new TestBuilder.Bootstrap<>(rnd);
            builder.createTestSet2(f);
            try {
                nextRun(rnd, 0, rootExpected = f.getExpected(), f.getActual());
                return;
            } catch (InternalReproducibleException ignore) {
                return;
            } catch (GenerateNewBatch ignore) {} catch (ControlErrors.ResetToMark e) {
                throw new AssertionError("Attempted to reset a non-existing mark", e);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public long getInitialSeed() {
        return initialSeed;
    }

    /** {@inheritDoc} */
    @Override
    public int getDepth() {
        return depth;
    }

    /** {@inheritDoc} */
    @Override
    public int getInstruction() {
        return i;
    }
}
