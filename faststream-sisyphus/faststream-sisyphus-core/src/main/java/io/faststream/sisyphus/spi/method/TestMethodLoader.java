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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Ignore;

import com.google.common.reflect.TypeToken;

import io.faststream.sisyphus.annotations.CustomWeight;
import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.spi.TestCase;
import io.faststream.sisyphus.util.ClassTestUtil;

/**
 * Responsible for loading all test methods from the classpath.
 * 
 * @author Kasper Nielsen
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class TestMethodLoader {

    /** A map of all random test methods on the classpath. */
    private static final Map<Class<?>, List<TestMethod>> ALL_METHODS;

    /** A cache of concrete type -> list of random test methods. */
    private static final ConcurrentHashMap<Class<?>, List<TestMethod>> CACHE = new ConcurrentHashMap<>();

    static {
        try {
            LinkedHashMap<Class<?>, List<TestMethod>> result = new LinkedHashMap<>();
            for (String str : readPackagesFromSystemResourceURLS("META-INF/servicepackages/" + TestCase.class.getName())) {
                List<Class<? extends TestCase<?, ?>>> classes = new ArrayList<>();
                Set<Class<?>> testTypes = new HashSet<>();
                for (Class<?> c : ClassTestUtil.findAllClassesInPackage(str)) {
                    if (TestCase.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers())) {
                        // Class<?> type = ClassTestUtil.getTypeOfArgument(TestCase.class, c, 1);
                        Class<?> type = TypeToken.of(c).resolveType(TestCase.class.getTypeParameters()[1]).getRawType();
                        testTypes.add(type);
                        classes.add((Class) c);
                    }
                }
                assertFalse("No test classes in package " + str, classes.isEmpty());
                if (testTypes.size() > 1) {
                    throw new AssertionError("A package must only contain tests for one type, was:" + testTypes);
                }
                Class<?> testType = testTypes.iterator().next();
                List<TestMethod> l = new ArrayList<>();
                for (Class<? extends TestCase<?, ?>> c : classes) {
                    l.addAll(findMethodsForClass(c));
                }
                if (result.containsKey(testType)) {
                    l.addAll(result.get(testType));
                }
                result.put(testType, Collections.unmodifiableList(l));
            }
            ALL_METHODS = Collections.unmodifiableMap(result);
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            throw new Error(e);
        }
    }

    /**
     * @param c
     * @return
     */
    private static List<TestMethod> findMethodsForClass(Class<? extends TestCase<?, ?>> c) {
        if (c.isAnnotationPresent(Ignore.class)) {
            return Collections.emptyList();
        }

        ArrayList<TestMethod> al = new ArrayList<>();
        double sum = 0;
        for (Method m : c.getMethods()) {
            if (isTestMethod(m) && !m.isAnnotationPresent(Ignore.class)) {
                sum += m.isAnnotationPresent(FailWith.class) ? 1 : 10;
            }
        }

        for (Method m : c.getMethods()) {
            if (isTestMethod(m) && !m.isAnnotationPresent(Ignore.class)) {
                CustomWeight wa = m.getAnnotation(CustomWeight.class);
                double w = wa == null ? 1 : wa.value();
                if (w <= 0) {
                    throw new Error(m + " must have a positive weight, was " + wa);
                }
                Class<? extends Throwable> expected = null;
                if (m.isAnnotationPresent(FailWith.class)) {
                    expected = m.getAnnotation(FailWith.class).value();
                }
                double rel = (expected == null ? 10 : 1) / sum; // <1
                al.add(new TestMethod(m, expected, rel * w));
            }
        }
        return al;
    }

    private static boolean isTestMethod(Method m) {
        return m.isAnnotationPresent(RndTest.class) || m.isAnnotationPresent(FailWith.class);
    }

    /**
     * 
     * @param t
     *            the type to test for
     * @return
     */
    static List<TestMethod> getTestsForType(Class<?> t) {
        List<TestMethod> d = CACHE.get(requireNonNull(t));
        if (d != null) {
            return d;
        }
        ArrayList<TestMethod> al = new ArrayList<>();
        for (Class<?> cc : ALL_METHODS.keySet()) {
            if (cc.isAssignableFrom(t)) {
                al.addAll(ALL_METHODS.get(cc));
            }
        }
        if (al.isEmpty()) {
            throw new AssertionError("No tests found for " + t.getCanonicalName());
        }
        Collections.sort(al); // We want to sort this as reflection can return methods in any order.

        CACHE.putIfAbsent(t, Collections.unmodifiableList(al));
        return CACHE.get(t);
    }

    private static List<String> readPackagesFromSystemResourceURLS(String agent) throws IOException {
        ArrayList<String> al = new ArrayList<>();
        ArrayList<URL> urls = Collections.list(TestMethodLoader.class.getClassLoader().getResources(agent));
        for (URL url : urls) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), Charset.defaultCharset()))) {
                reader.lines().map(e -> e.trim()).filter(e -> e.length() > 0 && !e.startsWith("#")).forEach(e -> al.add(e));
            }
        }
        return al;
    }
}
