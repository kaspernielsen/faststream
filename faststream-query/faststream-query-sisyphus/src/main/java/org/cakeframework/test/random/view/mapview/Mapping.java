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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.cakeframework.internal.util.Multimaps;
import org.cakeframework.util.Multimap;
import org.cakeframework.util.view.MapView;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.DefaultBiFunction;
import io.faststream.sisyphus.stubs.DefaultFunction;
import io.faststream.sisyphus.stubs.LogHashcodeFunction;

/**
 * Test the {@link MapView#map(BiFunction)} operation.
 * <p>
 * This class is called MapEntries and not Map to avoid configurations with the Java's build in {@link Map}.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class Mapping<K, V> extends AbstractMapViewRandomTestCase<K, V> {

    @RndTest
    public void entries() {
        @SuppressWarnings("unchecked")
        List<Map.Entry<K, V>> l = Arrays.asList(expected().entrySet().toArray(
                (Map.Entry<K, V>[]) new Map.Entry[expected().size()]));
        setNext(l, actual().entries(), isOrdered());
        // if (isOrdered()) {
        // assertEquals(l, actual.entries().toList());
        // } else {
        // assertCollectionEqualInAnyOrder(l, actual.entries().toList());
        // }
    }

    @RndTest
    public void keys() {
        setNested(expected().keySet(), actual().keys());
    }

    @RndTest
    public void map() {
        BiFunction<K, V, Object> mapper = DefaultBiFunction.mapper();
        List<Object> l = new ArrayList<>(expected().size());
        for (Map.Entry<K, V> n : expected().entrySet()) {
            l.add(mapper.apply(n.getKey(), n.getValue()));
        }
        setNext(l, actual().map(mapper), isOrdered());
    }

    @RndTest
    public void mapKey() {
        Function<K, Number> mapper = LogHashcodeFunction.instance();
        Multimap<Number, V> multimap = Multimaps.newOrderedListMultimap();
        for (Map.Entry<K, V> n : expected().entrySet()) {
            multimap.put(mapper.apply(n.getKey()), n.getValue());
        }
        setNext(multimap, actual().mapKey(mapper), false, false);
    }

    @SuppressWarnings("unused")
    @RndTest
    public void mapValue() {
        Function<V, Object> mapper = DefaultFunction.newInstance(Math.round(random().nextGaussian(0, 5)));
        Map<K, Object> map = isOrdered() ? new LinkedHashMap<K, Object>() : new HashMap<K, Object>();
        for (Map.Entry<K, V> n : expected().entrySet()) {
            map.put(n.getKey(), mapper.apply(n.getValue()));
        }
        setNext(map, actual().mapValue(mapper), isOrdered());
    }

    @RndTest
    public void values() {
        setNested(expected().values(), actual().values());
    }
}
