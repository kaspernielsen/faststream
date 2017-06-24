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
package io.faststream.sisyphus.view.mapview;

import static org.junit.Assert.assertEquals;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

import io.faststream.query.util.view.MapView;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link MapView#apply(BiConsumer)} operation.
 * <p>
 * Basic idea is to throw to all arguments to {@link BiConsumer#accept(Object, Object)} into a concurrent queue and
 * compare with expected contents.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class ForEach<K, V> extends AbstractMapViewRandomTestCase<K, V> {

    @RndTest
    public void forEach() {
        final ConcurrentLinkedQueue<SimpleImmutableEntry<K, V>> q = new ConcurrentLinkedQueue<>();
        actual().forEach(new BiConsumer<K, V>() {
            public void accept(K k, V v) {
                q.add(new SimpleImmutableEntry<>(k, v));
            }
        });

        LinkedHashMap<K, V> hm = new LinkedHashMap<>();
        for (SimpleImmutableEntry<K, V> sie : q) {
            hm.put(sie.getKey(), sie.getValue());
        }
        assertEquals(expected().size(), hm.size());
        assertEquals(expected().asMap(), hm);

    }

}
