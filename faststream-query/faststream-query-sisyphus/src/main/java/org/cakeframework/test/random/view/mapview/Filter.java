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
package org.cakeframework.test.random.view.mapview;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.cakeframework.util.view.MapView;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.HashcodeModPredicate;
import io.faststream.sisyphus.stubs.ModBinaryPredicate;

/**
 * Test the {@link MapView#filter()} operation.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class Filter<K, V> extends AbstractMapViewRandomTestCase<K, V> {

    @RndTest
    public void filter() {
        BiPredicate<K, V> p = new ModBinaryPredicate<>(random().nextInt(2, 14));
        Map<K, V> m = new HashMap<>();
        for (Entry<K, V> e : expected().entrySet()) {
            K key = e.getKey();
            V value = e.getValue();
            if (p.test(key, value)) {
                m.put(key, value);
            }
        }
        setNext(m, actual().filter(p), false);
    }

    @RndTest
    public void filterNullValues() {
        Map<K, V> m = new HashMap<>();
        for (Entry<K, V> e : expected().entrySet()) {
            V value = e.getValue();
            if (value != null) {
                m.put(e.getKey(), value);
            }
        }
        setNext(m, actual().filterNullValues(), false);
    }

    @RndTest
    public void filterOnKey() {
        Predicate<K> p = new HashcodeModPredicate<>(random().nextInt(2, 14));

        Map<K, V> m = new HashMap<>();
        for (Entry<K, V> e : expected().entrySet()) {
            K key = e.getKey();
            if (p.test(key)) {
                m.put(key, e.getValue());
            }
        }
        setNext(m, actual().filterOnKey(p), false);
    }

    @RndTest
    public void filterOnKeyType() {
        @SuppressWarnings("unchecked")
        Class<K> cl = (Class<K>) randomType(expected().keySet());
        Map<K, V> m = new HashMap<>();
        for (Entry<K, V> e : expected().entrySet()) {
            K key = e.getKey();
            if (key != null && cl.isInstance(key)) {
                m.put(key, e.getValue());
            }
        }
        setNext(m, actual().filterOnKeyType(cl), false);
    }

    @RndTest
    public void filterOnValue() {
        Predicate<V> p = new HashcodeModPredicate<>(random().nextInt(2, 14));
        Map<K, V> m = new HashMap<>();
        for (Entry<K, V> e : expected().entrySet()) {
            V value = e.getValue();
            if (p.test(value)) {
                m.put(e.getKey(), value);
            }
        }
        setNext(m, actual().filterOnValue(p), false);
    }

    @RndTest
    public void filterOnValueType() {
        @SuppressWarnings("unchecked")
        Class<V> cl = (Class<V>) randomType(expected().values());
        Map<K, V> m = new HashMap<>();
        for (Entry<K, V> e : expected().entrySet()) {
            V value = e.getValue();
            if (value != null && cl.isInstance(value)) {
                m.put(e.getKey(), value);
            }
        }
        setNext(m, actual().filterOnValueType(cl), false);
    }
}
