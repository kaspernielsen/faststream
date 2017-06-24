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
package io.faststream.sisyphus.spi.method;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import org.junit.internal.AssumptionViolatedException;

import io.faststream.sisyphus.spi.TestCase;

/**
 * A method of some object that is under test.
 *
 * @author Kasper Nielsen
 */
public final class TestMethod implements Comparable<TestMethod> {

    private volatile String cache;

    /** The default weight of this method. */
    private final double defaultWeight;

    /** An throwable that the method is expected to throw. */
    private final Class<? extends Throwable> expected;

    /** The method we are testing */
    private final Method method;

    TestMethod(Method method, Class<? extends Throwable> expected, double weight) {
        this.method = requireNonNull(method);
        this.expected = expected;
        this.defaultWeight = weight;
    }

    @Override
    public int compareTo(TestMethod o) {
        return method.getDeclaringClass() == o.method.getDeclaringClass() ? method.getName().compareTo(o.method.getName())
                : method.getDeclaringClass().getCanonicalName().compareTo(o.method.getDeclaringClass().getCanonicalName());
    }

    /**
     * Returns the default weight of the method
     *
     * @return the default weight of the method
     */
    public double getDefaultWeight() {
        return defaultWeight;
    }

    /**
     * Returns the actual method.
     *
     * @return the actual method
     */
    public Method getMethod() {
        return method;
    }

    public void invoke(TestCase<?, ?> testCase) throws Throwable {
        try {
            method.invoke(testCase);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AssumptionViolatedException) {
                return;
            }
            if (cause instanceof UndeclaredThrowableException) {
                cause = ((UndeclaredThrowableException) cause).getUndeclaredThrowable();
                if (cause instanceof InvocationTargetException) {
                    cause = cause.getCause();
                }
            }
            if (expected != null) {
                if (!expected.isInstance(cause)) {
                    throw new AssertionError("Expected " + expected.getCanonicalName() + ", but was " + cause.getClass().getSimpleName(), cause);
                }
                return;
            }
            throw cause;
        }
        if (expected != null) {
            String me = /* o.getClass().getCanonicalName() + "." */method.toString();
            throw new AssertionError("Expected " + expected.getCanonicalName() + " for method " + me);
        }
    }

    /**
     * Creates a new instanceof of the TestCase this method belongs to. The TestCase must have a public no argument
     * constructor.
     */
    @SuppressWarnings("rawtypes")
    public TestCase newTestObject() {
        try {
            return (TestCase) method.getDeclaringClass().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder().append(method.getDeclaringClass().getSimpleName() + "." + method.getName());
        if (expected != null) {
            sb.append(" [expected = " + expected.getSimpleName() + "]");
        }
        return sb.toString();
    }

    public String toString2() {
        if (cache != null) {
            return cache;
        }
        StringBuilder sb = new StringBuilder();
        String pname = method.getDeclaringClass().getPackage().getName();
        pname = pname.replace("io.faststream.test.random.", "");
        sb.append(pname).append(".").append(method.getDeclaringClass().getSimpleName() + "." + method.getName());
        return cache = sb.toString();
    }
}
