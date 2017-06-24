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

import static java.util.Objects.requireNonNull;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

import io.faststream.sisyphus.builder.TestBuilder;
import io.faststream.sisyphus.expected.Expected;
import io.faststream.sisyphus.spi.method.TestMethod;
import io.faststream.sisyphus.spi.method.TestSelector;
import io.faststream.sisyphus.util.RandomSource;

/**
 *
 * @author Kasper Nielsen
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
class BatchRun implements TestCaseContext {

    private final TestBuilder builder;

    /** The current depth */
    private int depth;

    private final Predicate<TestCaseContext> keepRunning;

    /** The current number of operation performed. Including the current */
    private int operationCount;

    private final RandomSource rnd;

    private Expected rootExpected;

    private final long seed;

    BatchRun(long seed, TestBuilder builder, Predicate<TestCaseContext> keepRunning) {
        this.seed = seed;
        this.rnd = RandomSource.current();
        this.rnd.setSeed(seed);
        this.builder = builder;
        this.keepRunning = requireNonNull(keepRunning);
    }

    /** {@inheritDoc} */
    @Override
    public int getDepth() {
        return depth;
    }

    /** {@inheritDoc} */
    @Override
    public int getInstruction() {
        return operationCount;
    }

    /** {@inheritDoc} */
    @Override
    public long getInitialSeed() {
        return seed;
    }

    private void run(Expected expected, Object actual) {
        Class<?> clazzToTest = actual.getClass();

        TestSelector dts = null;
        Function<Random, TestMethod> am = null;
        while (keepRunning.test(this)) {
            TestSelector ts = builder.getSelector(rnd, rootExpected);
            if (dts != ts) {
                am = (dts = ts).nextMethod(clazzToTest);
            }
            TestMethod m = am.apply(rnd);
            TestCase o = m.newTestObject();
            o.context = this;
            o.expected = expected;
            o.actual = actual;
        }
    }

    void start() {
        TestBuilder.Bootstrap f = new TestBuilder.Bootstrap(rnd);
        builder.createTestSet2(f);
        run(rootExpected = f.getExpected(), f.getActual());
    }
}
