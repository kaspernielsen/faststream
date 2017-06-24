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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import io.faststream.sisyphus.TestResult;
import io.faststream.sisyphus.builder.TestBuilder;
import io.faststream.sisyphus.expected.Expected;
import io.faststream.sisyphus.spi.method.TestMethod;
import io.faststream.sisyphus.util.RandomSource;

/**
 * 
 * @author Kasper Nielsen
 */
class InternalTestResult implements TestResult {

    final List<Failure> e = new CopyOnWriteArrayList<>();

    final AtomicLong iterations = new AtomicLong();

    final long maximumNumberOfiterations;

    final InternalTestProgressPrinter printer;

    BatchRunner[] runners;

    final long start = System.nanoTime();

    InternalTestResult(long maximumNumberOfiterations) {
        this.maximumNumberOfiterations = maximumNumberOfiterations;
        printer = new InternalTestProgressPrinter(this);
    }

    synchronized void addFailure(TestMethod method, long seed, Throwable cause, Object expected, Object actual, long initialSeed) {
        InternalReproducibleException e = new InternalReproducibleException(cause, expected, actual, initialSeed);
        e.add(seed, method);
        for (BatchRunner r : runners) {
            r.remaining.set(0);
        }
        this.e.add(new Failure(e));
        throw e;
    }

    /** {@inheritDoc} */
    public Failure getFirstFailure() {
        if (e.isEmpty()) {
            throw new IllegalStateException("no failures");
        }
        return e.iterator().next();
    }

    /** {@inheritDoc} */
    public long getNumberOfFinishedSteps() {
        return iterations.get();
    }

    /** {@inheritDoc} */
    public boolean hasFailures() {
        return !e.isEmpty();
    }

    void setRunnersToZero() {
        for (BatchRunner r : runners) {
            r.remaining.set(0);
        }
    }

    static Object createProxy(final Class<?> inter, final Object realActual, final StringBuilder sb) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { inter }, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
                if (sb.length() > 0) {
                    sb.append(".");
                }
                sb.append(method.getName()).append("(");
                if (args != null) {
                    for (Object a : args) {
                        if (a instanceof Class) {
                            sb.append(((Class<?>) a).getSimpleName()).append(".class");
                        } else {
                            sb.append(a);
                        }
                    }
                }
                sb.append(")");
                return method.invoke(realActual, args);
            }
        });
    }

    /** A failure. */
    class Failure {
        final InternalReproducibleException e;

        Failure(InternalReproducibleException e) {
            this.e = e;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        void reproduce(TestBuilder<?, ?> builder) {
            RandomSource r = RandomSource.current();
            r.setSeed(e.initialSeed);
            TestBuilder.Bootstrap f = new TestBuilder.Bootstrap(r);
            builder.createTestSet2(f);
            Object actual = f.getActual();
            Expected expected = f.getExpected();
            StringBuilder sb = new StringBuilder("builder.create(" + e.initialSeed + "L)");
            for (InternalReproducibleException.Entry ee : e.AE) {

                TestCase o = ee.method.newTestObject();
                actual = createProxy(o.testClass, actual, sb);
                o.expected = expected;
                o.actual = actual;
                try {
                    ee.method.invoke(o);
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
                actual = o.nestedActual;
                expected = o.nestedExpected;
            }
            System.out.println(sb + ";");
        }
    }
}
