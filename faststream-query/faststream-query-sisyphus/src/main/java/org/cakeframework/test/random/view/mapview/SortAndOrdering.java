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

import java.util.Comparator;

import org.cakeframework.test.random.view.spi.ExpectedMapView;
import org.cakeframework.util.BiComparator;
import org.cakeframework.util.view.MapView;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.RememberZeroComparator;
import io.faststream.sisyphus.stubs.TotalOrderComparator;
import io.faststream.sisyphus.util.ClassTestUtil;

/**
 * Test the {@link MapView#order()} operation.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class SortAndOrdering<K, V> extends AbstractMapViewRandomTestCase<K, V> {

    @SuppressWarnings("rawtypes")
    public static final BiComparator BY_KEY_VALUE = (a, b, aa, bb) -> TotalOrderComparator.compareTwo(a, aa, b, bb);
    @SuppressWarnings("rawtypes")
    public static final BiComparator BY_VALUE_KEY = (a, b, aa, bb) -> TotalOrderComparator.compareTwo(b, bb, a, aa);

    @RndTest
    public void sort() {
        @SuppressWarnings("unchecked")
        final BiComparator<K, V> c = random().nextBoolean() ? BY_KEY_VALUE : BY_VALUE_KEY;
        setNext(expected().sorted(c), actual().sorted(c));
    }

    @RndTest
    public void sortByKey() {
        // Check first if all keys are mutable-comparable or else abort
        if (ClassTestUtil.isInterComparable(expected().keySet())) {
            setNext(expected().newSortedByKey(Comparator.naturalOrder()), actual().sortedByKey());
        } else {
            setNext(expected(), actual());
        }
    }

    @RndTest
    public void sortByKeyComparator() {
        Comparator<K> comp = TotalOrderComparator.instance();
        comp = random().nextBoolean() ? comp : comp.reversed();
        setNext(expected().newSortedByKey(comp), actual().sortedByKey(comp));
    }

    @RndTest
    public void sortByKeyDescending() {
        // Check first if all keys are mutable-comparable or else abort
        if (ClassTestUtil.isInterComparable(expected().keySet())) {
            setNext(expected().newSortedByKey(Comparator.reverseOrder()), actual().sortedByKeyDescending());
        } else {
            setNext(expected(), actual());
        }
    }

    @RndTest
    public void sortByValue() {
        // Check first if all values are mutable-comparable or else abort
        if (ClassTestUtil.isInterComparable(expected().values())) {
            // The problem is here that values might be identical. In which case there is no
            // defined total order. Because we cannot take the key into consideration, so
            // we sort expected and if there where any values that where identical (hadZero)
            // In which case we abort
            RememberZeroComparator<V> z = new RememberZeroComparator<>(Comparator.naturalOrder());
            ExpectedMapView<K, V> map = expected().newSortedByValue(z);
            if (!z.hadZero()) {
                setNext(map, actual().sortedByValue());
                return;
            }
        }
        setNext(expected(), actual());
    }

    @RndTest
    public void sortByValueComparator() {
        Comparator<V> comparator = TotalOrderComparator.instance();
        comparator = random().nextBoolean() ? comparator : comparator.reversed();
        RememberZeroComparator<V> c = RememberZeroComparator.from(comparator);
        ExpectedMapView<K, V> map = expected().newSortedByValue(c);
        if (c.hadZero()) {
            // some values are identical, cannot guarantee order of keys
            setNext(expected(), actual());
        } else {
            setNext(map, actual().sortedByValue(comparator));
        }
    }

    @RndTest
    public void sortByValueDescending() {
        // Check first if all values are mutable-comparable or else abort
        if (ClassTestUtil.isInterComparable(expected().values())) {
            // The problem is here that values might be identical. In which case there is no
            // defined total order. Because we cannot take the key into consideration, so
            // we sort expected and if there where any values that where identical (hadZero)
            // In which case we abort
            RememberZeroComparator<V> z = new RememberZeroComparator<>(Comparator.reverseOrder());
            ExpectedMapView<K, V> map = expected().newSortedByValue(z);
            if (!z.hadZero()) {
                setNext(map, actual().sortedByValueDescending());
                return;
            }
        }
        setNext(expected(), actual());
    }
}
