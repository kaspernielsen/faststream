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
package io.faststream.sisyphus.builder;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.ForkJoinPool;

import io.faststream.sisyphus.TestResult;
import io.faststream.sisyphus.expected.Expected;
import io.faststream.sisyphus.spi.TestRunner;
import io.faststream.sisyphus.spi.method.TestSelector;
import io.faststream.sisyphus.spi.method.TestSelectorProvider;
import io.faststream.sisyphus.util.RandomSource;

/**
 * A abstract random test builder.
 * 
 * @param <E>
 *            the expected value
 * @param <A>
 *            the actual value
 * 
 * @author Kasper Nielsen
 */
public abstract class TestBuilder<E extends Expected, A> {

    /** The provider used for creating test selectors. */
    private TestSelectorProvider defaultTestProvider = TestSelectorProvider.DEFAULT;

    /** The fork join pool used to execute each test runner. */
    private ForkJoinPool executor = ForkJoinPool.commonPool();

    /** The random seed that is used to initialize each {@link TestStepThreadRunner}. */
    private long initialSeed = 123456789;

    private boolean reproduce;

    /**
     * Creates a new test set from the specified random source.
     * 
     * @param random
     *            the random source
     */
    protected abstract void createTestSet(Bootstrap<E, A> factory);

    public void createTestSet2(Bootstrap<E, A> factory) {
        createTestSet(factory);
    }

    /**
     * Returns the executor that should be used to run tests concurrently.
     * 
     * @return the executor that should be used to run tests concurrently
     */
    public final ForkJoinPool getExecutor() {
        return executor;
    }

    /**
     * @return the random seed, the default value is <tt>123456789</tt>
     */
    public final long getInitialSeed() {
        return initialSeed;
    }

    public final TestSelector getSelector(RandomSource random, E expected) {
        return defaultTestProvider.getSelector(random, expected);
    }

    /**
     * @return the reproduce
     */
    public final boolean isReproduce() {
        return reproduce;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final A recreateTest(long seed) {
        RandomSource r = RandomSource.current();
        r.setSeed(seed);
        Bootstrap tf = new Bootstrap<>(r);
        createTestSet(tf);
        return (A) tf.actual;
    }

    /**
     * Sets the executor that will be used to execute the tests. {@link ForkJoinPool#getParallelism()} number of
     * concurrent tests will be started.
     * 
     * @param pool
     *            the pool to use
     * @throws NullPointerException
     *             if the specified pool is null
     */
    public final void setExecutor(ForkJoinPool pool) {
        this.executor = requireNonNull(pool, "pool is null");
    }

    /**
     * Creates a new {@link ForkJoinPool} with the specified parallelism to use for executing the tests.
     * 
     * @param parallelism
     *            the amount of parallelism
     */
    public final void setExecutor(int parallelism) {
        this.executor = new ForkJoinPool(parallelism);
    }

    /**
     * Sets the random seed that should be used when running the tests.
     * 
     * @param seed
     *            the random seed
     */
    public final void setInitialSeed(long seed) {
        this.initialSeed = seed;
    }

    /**
     * @param reproduce
     *            the reproduce to set
     */
    public final void setReproduce(boolean reproduce) {
        this.reproduce = reproduce;
    }

    /**
     * Sets
     * 
     * @param testSelectorProvider
     */
    public final void setTestSelectorProvider(TestSelectorProvider testSelectorProvider) {
        this.defaultTestProvider = testSelectorProvider;
    }

    public final TestResult start(long iterations) {
        return TestRunner.start(this, iterations);
    }

    /** The configuration. */
    public static final class Bootstrap<E extends Expected, A> {

        A actual;

        E expected;

        private final RandomSource rnd;

        public Bootstrap(RandomSource rnd) {
            this.rnd = requireNonNull(rnd);
        }

        /**
         * @return the actual
         */
        public A getActual() {
            return actual;
        }

        /**
         * @return the expected
         */
        public E getExpected() {
            return expected;
        }

        public RandomSource getRandom() {
            return rnd;
        }

        public void setActual(A actual) {
            this.actual = requireNonNull(actual);
        }

        // check() <- checks expected, actual, configuration not null

        public void setExpected(E expected) {
            this.expected = requireNonNull(expected);
        }
    }
}
