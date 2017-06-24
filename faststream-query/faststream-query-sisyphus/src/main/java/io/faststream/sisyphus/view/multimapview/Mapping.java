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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.faststream.query.util.Multimap;
import io.faststream.query.util.view.MultimapView;
import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.DefaultBiFunction;
import io.faststream.sisyphus.stubs.DefaultFunction;
import io.faststream.sisyphus.stubs.LogHashcodeFunction;

/**
 * Test the {@link MultimapView#map(BiFunction)} operation.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class Mapping<K, V> extends AbstractMultimapViewRandomTestCase<K, V> {
    @RndTest
    public void keys() {
        ArrayList<K> result = new ArrayList<>();
        for (K key : expected().keySet()) {
            result.add(key);
        }
        setNext(result, actual().keys(), isKeysOrdered());
    }

    @RndTest
    public void values() {
        ArrayList<V> al = new ArrayList<>();
        for (V n : expected().values()) {
            al.add(n);
        }
        // Figure ordering out, right now both key and values needs to be ordered
        // But I dont know if it makes at all to have an ordered view
        // ordered=isKeysOrdered() && isValuesOrdered()
        setNext(al, actual().values(), false);
    }

    @RndTest
    public void map() {
        BiFunction<K, V, Object> mapper = DefaultBiFunction.mapper();
        List<Object> l = new ArrayList<>(expected().size());
        for (Entry<K, V> n : expected().entries()) {
            l.add(mapper.apply(n.getKey(), n.getValue()));
        }
        setNext(l, actual().map(mapper), false);
    }

    @RndTest
    public void mapKey() {
        // LogHashcodeMapper produces the same key for some keys.
        // DefaultMapper produces distinct keys for all keys
        Function<K, ? extends Object> mapper = random().nextBoolean() ? LogHashcodeFunction.<K> instance()
                : DefaultFunction.<K, Object> newInstance(Math.round(random().nextGaussian(0, 5)));

        Multimap<Object, V> map = newMultiMap();
        for (Entry<K, V> n : expected().entries()) {
            map.put(mapper.apply(n.getKey()), n.getValue());
        }
        setNext(map, actual().mapKey(mapper), false, false);
    }

   @FailWith(NullPointerException.class)
    public void mapKeyNullArgument() {
        actual().mapKey(null);
    }

    @RndTest
    public void mapValue() {
        Function<V, ? extends Object> mapper = DefaultFunction.<V, Object> newInstance(Math.round(random()
                .nextGaussian(0, 5)));
        Multimap<K, Object> map = newMultiMap();
        for (Entry<K, V> e : expected().entries()) {
            map.put(e.getKey(), mapper.apply(e.getValue()));
        }
        setNext(map, actual().mapValue(mapper), isKeysOrdered(), isValuesOrdered());
    }

   @FailWith(NullPointerException.class)
    public void mapValueNullArgument() {
        actual().mapValue(null);
    }
}
