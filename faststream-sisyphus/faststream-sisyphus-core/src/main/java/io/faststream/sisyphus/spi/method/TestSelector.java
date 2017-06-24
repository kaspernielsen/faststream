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
import static org.junit.Assert.assertFalse;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import io.faststream.sisyphus.annotations.Remove;
import io.faststream.sisyphus.util.AliasedMethodTable;

/**
 * A test selector selects which
 * 
 * @author Kasper Nielsen
 */
public abstract class TestSelector {

    /** The default test selector. */
    public static TestSelector DEFAULT = new TestSelector() {};

    /** A test selector that selects remove tests with little probability. */
    public static final TestSelector RARELY_REMOVE = TestSelector.DEFAULT.multiplyIfAnnotatedWith(Remove.class, .05);

    /** A cache of all test methods for the test selector. */
    private final ConcurrentHashMap<Class<?>, AliasedMethodTable<TestMethod>> cache = new ConcurrentHashMap<>();

    /**
     * Calculates the weight of the specified method. The default implementation returns
     * {@link TestMethod#getDefaultWeight()}
     * 
     * @param method
     *            the method to calculate the weight for
     * @return the weight of the calculated method
     */
    public double calculateTestWeight(TestMethod method) {
        return method.getDefaultWeight();
    }

    /**
     * Returns a new
     * 
     * @param filter
     * @param factor
     * @return
     */
    public final TestSelector multiply(final Predicate<TestMethod> filter, final double factor) {
        requireNonNull(filter);
        return new TestSelector() {
            public double calculateTestWeight(TestMethod method) {
                double weight = TestSelector.this.calculateTestWeight(method);
                return filter.test(method) ? weight * factor : weight;
            }
        };
    }

    public final TestSelector multiplyIfAnnotatedWith(final Class<? extends Annotation> annotation, double factor) {
        requireNonNull(annotation);
        return multiply(m -> m.getMethod().isAnnotationPresent(annotation), factor);
    }

    public final void listMethods(Class<?> actualType) {
        nextMethod(actualType);
        AliasedMethodTable<TestMethod> table = cache.get(actualType);
        table.print(e -> e.toString2(), new PrintWriter(System.err));
    }

    public final Function<Random, TestMethod> nextMethod(Class<?> actualType) {
        AliasedMethodTable<TestMethod> d = cache.get(requireNonNull(actualType));
        return d != null ? d : cache.computeIfAbsent(actualType, new Function<Class<?>, AliasedMethodTable<TestMethod>>() {
            public AliasedMethodTable<TestMethod> apply(Class<?> at) {
                LinkedHashMap<TestMethod, Double> weights = new LinkedHashMap<>();
                for (TestMethod rtm : TestMethodLoader.getTestsForType(at)) {
                    double weight = calculateTestWeight(rtm);
                    assertFalse(Double.isNaN(weight) || Double.isInfinite(weight));
                    if (weight > 0) {
                        weights.put(rtm, weight);
                        // System.out.println(rtm.toString2() + " " + weight);
                    }
                }
                if (weights.isEmpty()) {
                    throw new IllegalArgumentException(
                            "All weights are 0 for targets of " + at.getCanonicalName() + " tm =" + TestMethodLoader.getTestsForType(at));
                }

                return new AliasedMethodTable<>(weights);
            }
        });
    }
}
