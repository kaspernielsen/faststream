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

import io.faststream.sisyphus.expected.Expected;
import io.faststream.sisyphus.util.ClassTestUtil;
import io.faststream.sisyphus.util.RandomSource;

/**
 * A random test case. All test cases must override this class.
 * 
 * @param <E>
 *            the expected value
 * @param <A>
 *            the actual value
 * 
 * @author Kasper Nielsen
 */
public abstract class TestCase<E extends Expected, A> {

    /** The actual value. Should only be read not written by tests */
    A actual;

    TestCaseContext context;

    /** The expected value. Should only be read not written by tests */
    E expected;

    boolean mark;

    Object nestedActual;

    Expected nestedExpected;

    /** The type of class this step tests. */
    final Class<?> testClass;

    protected TestCase() {
        this.testClass = ClassTestUtil.getTypeOfArgument(TestCase.class, getClass(), 1);
    }

    /**
     * Returns the actual value.
     * 
     * @return the actual value
     */
    public final A actual() {
        return actual;
    }

    /**
     * Returns the current test case context.
     * 
     * @return
     */
    public final TestCaseContext context() {
        return context;
    }

    /**
     * Returns the expected value.
     * 
     * @return the expected value
     */
    public final E expected() {
        return expected;
    }

    /**
     * 
     * @param nextExpected
     * @param nextActual
     */
    public final void nested(Expected nextExpected, Object nextActual) {
        this.nestedExpected = requireNonNull(nextExpected, "nextExpected is null");
        this.nestedActual = requireNonNull(nextActual, "nextActual is null");
    }

    /**
     * Returns a random source. Tests should not use other randomness source but the one returned from this method.
     * 
     * @return the random source
     */
    public final RandomSource random() {
        return RandomSource.current();
    }

    /** Called by client code to indicate that we are in terminal state where we need to start a new test batch. */
    public final void startNewBatch() {
        ControlErrors.throwNewBatch();
    }

    public final void streamStart() {
        mark = true;
    }

    // Vi skal have begge to, den ene er naar vi har lavet mutable aendringer.
    // den anden er naar vi har lavet en raekke af operationer
    /**
     * Resets to the nearest mark.
     */
    public final void streamTerminate() {
        ControlErrors.throwResetToMark();
    }
}
