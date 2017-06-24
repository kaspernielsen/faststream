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
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import io.faststream.query.util.Multimap;
import io.faststream.query.util.view.MultimapView;
import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.TotalOrderComparator;
import io.faststream.sisyphus.util.ClassTestUtil;
import io.faststream.sisyphus.util.CollectionTestUtil;

/**
 * Test the {@link MultimapView#sortedKeys()} operation.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class OrderAndSorting<K, V> extends AbstractMultimapViewRandomTestCase<K, V> {

    @RndTest
    public void sortByKey() {
        // Check first if all keys are mutable-comparable or else abort
        if (ClassTestUtil.isInterComparable(expected().keySet())) {
            setNext(expected().sortByKey(Comparator.naturalOrder()), actual().sortedKeys());
        } else {
            setNext(expected(), actual());
        }
    }

    @RndTest
    public void orderByKeyComparator() {
        Comparator<K> comparator = TotalOrderComparator.instance();
        comparator = random().nextBoolean() ? comparator : comparator.reversed();
        setNext(expected().sortByKey(comparator), actual().sortedKeys(comparator));
    }

    @RndTest
    public void orderByKeyDescending() {
        // Check first if all keys are mutable-comparable or else abort
        if (ClassTestUtil.isInterComparable(expected().keySet())) {
            setNext(expected().sortByKey(Comparator.reverseOrder()), actual().sortedKeysDescending());
        } else {
            setNext(expected(), actual());
        }
    }

    @FailWith(NullPointerException.class)
    public void orderByKeyNullArgument() {
        actual().sortedKeys(null);
    }

    @FailWith(NullPointerException.class)
    public void orderByValueNullArgument() {
        actual().sortedKeys(null);
    }

    @RndTest
    public void orderValue() {
        Multimap<K, V> map = newMultiMap();
        for (Map.Entry<K, Collection<V>> me : expected().asMap().entrySet()) {
            if (!ClassTestUtil.isInterComparable(me.getValue())) {
                setNext(expected(), actual());
                return;
            }
            ArrayList<V> list = new ArrayList<>(me.getValue()); // ECLIPSE compiler
            map.putAll(me.getKey(), CollectionTestUtil.sort(list));
        }
        setNext(map, actual().sortedValues(), isKeysOrdered(), true);
    }

    @RndTest
    public void orderValueComparator() {
        Comparator<V> comparator = TotalOrderComparator.instance();
        comparator = random().nextBoolean() ? comparator : comparator.reversed();
        Multimap<K, V> map = newMultiMap();
        for (Map.Entry<K, Collection<V>> me : expected().asMap().entrySet()) {
            map.putAll(me.getKey(), CollectionTestUtil.sort(new ArrayList<>(me.getValue()), comparator));
        }
        setNext(map, actual().sortedValues(comparator), isKeysOrdered(), true);
    }

    @RndTest
    public void orderValueDescending() {
        Multimap<K, V> map = newMultiMap();
        for (Map.Entry<K, Collection<V>> me : expected().asMap().entrySet()) {
            if (!ClassTestUtil.isInterComparable(me.getValue())) {
                setNext(expected(), actual());
                return;
            }
            ArrayList<V> list = new ArrayList<>(me.getValue());
            map.putAll(me.getKey(), CollectionTestUtil.sortReverse(list));
        }
        setNext(map, actual().sortedValuesDescending(), isKeysOrdered(), true);
    }
}
