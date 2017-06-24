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

import static io.faststream.sisyphus.util.MoreAsserts.assertCollectionEqualInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import org.faststream.sisyphus.view.spi.ExpectedMultimapView;

import io.faststream.query.internal.util.Multimaps;
import io.faststream.query.util.Multimap;
import io.faststream.query.util.view.MultimapView;
import io.faststream.sisyphus.stubs.RememberZeroComparator;
import io.faststream.sisyphus.util.ClassTestUtil;
import io.faststream.sisyphus.view.AbstractViewRandomTestCase;

/**
 * An abstract test step for {@link MultimapView}.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
abstract class AbstractMultimapViewRandomTestCase<K, V> extends
AbstractViewRandomTestCase<ExpectedMultimapView<K, V>, MultimapView<K, V>> {

    static <K, V> Multimap<K, V> newMultiMap() {
        return Multimaps.newOrderedListMultimap();
    }

    static <K, V> void assertMultimapEquals(Multimap<K, V> expected, Multimap<K, V> actual, boolean isKeysOrdered,
            boolean isValuesOrdered) {
        if (expected.keySet().size() != actual.keySet().size()) {
            fail("Number of keys in Multimaps are not equivalent, expected " + expected + ", was " + actual);
        }

        if (expected.size() != actual.size()) {
            fail("Size of Multimaps are not equivalent, expected " + expected + ", was " + actual);
        }
        if (isKeysOrdered) {
            Iterator<Map.Entry<K, Collection<V>>> e = expected.asMap().entrySet().iterator();
            Iterator<Map.Entry<K, Collection<V>>> a = actual.asMap().entrySet().iterator();
            while (e.hasNext()) {
                assertTrue(a.hasNext());
                Map.Entry<K, Collection<V>> me = e.next();
                Map.Entry<K, Collection<V>> ma = a.next();
                if (!Objects.equals(me.getKey(), ma.getKey())) {
                    fail("Multimaps are not equivalent, expected " + expected + ", was " + actual);
                }
                try {
                    if (isValuesOrdered) {
                        assertEquals(me.getValue(), ma.getValue());
                    } else {
                        assertCollectionEqualInAnyOrder(me.getValue(), ma.getValue());
                    }
                } catch (AssertionError ee) {
                    System.err.println("Expected = " + expected);
                    System.err.println("Actual = " + actual);
                    throw ee;
                }
            }
            assertFalse(a.hasNext());
        } else {
            for (K key : expected.keySet()) {
                Collection<V> valueExpected = expected.get(key);
                Collection<V> valueActual = actual.get(key);
                if (isValuesOrdered) {
                    assertEquals(valueExpected, valueActual);
                } else {
                    assertCollectionEqualInAnyOrder(valueExpected, valueActual);
                }
            }
        }
    }

    void orderByValue(Multimap<K, V> expected, MultimapView<K, V> actual, MultimapView<K, V> nextActual,
            final Comparator<V> comparator, boolean usesComparable) {
        final RememberZeroComparator<V> izc = RememberZeroComparator.from(comparator);
        Multimap<K, V> map = newMultiMap();
        Map<K, Collection<V>> m = expected.asMap();
        for (Map.Entry<K, Collection<V>> me : m.entrySet()) {
            ArrayList<V> newValues = new ArrayList<>(me.getValue());

            // If any key maps to a collection of values that not inter-comparable
            // we select another operation than orderByValue by calling next
            if (usesComparable && !ClassTestUtil.isInterComparable(newValues)) {
                setNext(expected, nextActual, isKeysOrdered(), isValuesOrdered());
                return;
            }
            Collections.sort(newValues, izc);
            if (izc.hadZero()) {
                // System.err.println(ClassUtil.toStringObjects(newValues.toArray()));
                // throw new Error("Implement it");
            }
            map.putAll(me.getKey(), newValues);
        }
        setNext(map, nextActual, isKeysOrdered(), true);
    }

    public final boolean isValuesOrdered() {
        return expected().isValuesOrdered();
    }

    public final boolean isKeysOrdered() {
        return expected().isKeysOrdered();
    }
}
