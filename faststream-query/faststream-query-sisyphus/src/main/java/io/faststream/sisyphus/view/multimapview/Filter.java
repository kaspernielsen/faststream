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
package io.faststream.sisyphus.view.multimapview;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import io.faststream.query.util.Multimap;
import io.faststream.query.util.view.MultimapView;
import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.HashcodeModPredicate;
import io.faststream.sisyphus.stubs.ModBinaryPredicate;

/**
 * Test the {@link MultimapView#filterOn(BiPredicate)} operation.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class Filter<K, V> extends AbstractMultimapViewRandomTestCase<K, V> {

    @RndTest
    public void filterOn() {
        BiPredicate<K, V> p = new ModBinaryPredicate<>(random().nextInt(2, 14));
        setNext(expected().filter(p), actual().filter(p));
    }

   @FailWith(NullPointerException.class)
    public void filterNPE() {
        actual().filter(null);
    }

    @RndTest
    public void filterNullValues() {
        Multimap<K, V> map = newMultiMap();
        for (Entry<K, Collection<V>> n : expected().asMap().entrySet()) {
            for (V o : n.getValue()) {
                if (o != null) {
                    map.put(n.getKey(), o);
                }
            }
        }
        // strictly speaking we could maintain order on keys...
        // but than again some keys could disappear if they only pointed to null values
        setNext(map, actual().filterNullValues(), false, false);
    }

    @RndTest
    public void filterOnKey() {
        Predicate<K> p = new HashcodeModPredicate<>(random().nextInt(2, 14));
        Multimap<K, V> map = newMultiMap();
        for (Entry<K, Collection<V>> n : expected().asMap().entrySet()) {
            if (p.test(n.getKey())) {
                for (V value : n.getValue()) {
                    map.put(n.getKey(), value);
                }
            }
        }
        setNext(map, actual().filterOnKey(p), false, false);
    }

   @FailWith(NullPointerException.class)
    public void filterOnKeyNullArgument() {
        actual().filterOnKey(null);
    }

    @RndTest
    public void filterOnKeyType() {
        @SuppressWarnings("unchecked")
        Class<K> cl = (Class<K>) randomType(expected().keySet());
        Multimap<K, V> multimap = newMultiMap();
        for (Entry<K, Collection<V>> e : expected().asMap().entrySet()) {
            K key = e.getKey();
            if (key != null && cl.isInstance(key)) {
                for (V value : e.getValue()) {
                    multimap.put(e.getKey(), value);
                }
            }
        }
        setNext(multimap, actual().filterOnKeyType(cl), false, false);
    }

   @FailWith(NullPointerException.class)
    public void filterOnKeyTypeNullArgument() {
        actual().filterOnKeyType(null);
    }

    @RndTest
    public void filterOnValue() {
        Predicate<V> p = new HashcodeModPredicate<>(random().nextInt(2, 14));
        Multimap<K, V> map = newMultiMap();
        for (Entry<K, Collection<V>> n : expected().asMap().entrySet()) {
            for (V value : n.getValue()) {
                if (p.test(value)) {
                    map.put(n.getKey(), value);
                }
            }
        }
        setNext(map, actual().filterOnValue(p), false, false);
    }

   @FailWith(NullPointerException.class)
    public void filterOnValueNullArgument() {
        actual().filterOnValue(null);
    }

    @RndTest
    public void filterOnValueType() {
        @SuppressWarnings("unchecked")
        Class<V> cl = (Class<V>) randomType(expected().values());
        Multimap<K, V> multimap = newMultiMap();
        for (Entry<K, Collection<V>> e : expected().asMap().entrySet()) {
            for (V value : e.getValue()) {
                if (value != null && cl.isInstance(value)) {
                    multimap.put(e.getKey(), value);
                }
            }
        }
        setNext(multimap, actual().filterOnValueType(cl), false, false);
    }

   @FailWith(NullPointerException.class)
    public void filterOnValueTypeNullArgument() {
        actual().filterOnValueType(null);
    }
}
