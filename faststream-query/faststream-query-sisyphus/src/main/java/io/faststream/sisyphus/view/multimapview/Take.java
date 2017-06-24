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

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.faststream.query.util.Multimap;
import io.faststream.query.util.view.MultimapView;
import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link MultimapView#take(long)} operation.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class Take<K, V> extends AbstractMultimapViewRandomTestCase<K, V> {

    @RndTest
    public void take() {
        long count = randomTakeCount(expected().keySet().size());

        if (isKeysOrdered()) {
            @SuppressWarnings("unchecked")
            Map.Entry<K, Collection<V>>[] exp = expected().asMap().entrySet().toArray(new Map.Entry[0]);
            Multimap<K, V> m = newMultiMap();
            if (count > 0) {
                for (int i = 0; i < (count > exp.length ? exp.length : (int) count); i++) {
                    m.putAll(exp[i].getKey(), exp[i].getValue());
                }
            } else {
                int startIndex = (count + exp.length < 0) ? 0 : (int) (count + exp.length);
                for (int i = startIndex; i < exp.length; i++) {
                    m.putAll(exp[i].getKey(), exp[i].getValue());
                }
            }
            setNext(m, actual().take(count), true, isValuesOrdered());
            // nextRandomOperation());
        } else {
            Multimap<K, V> l = actual().take(count).toMultimap();
            if (count > 0) {
                if (count >= expected().keySet().size()) {
                    assertMultimapEquals(expected().asMultimap(), l, isKeysOrdered(), isValuesOrdered());
                } else {
                    assertEquals(count, l.keySet().size());
                    // TODO assertIsSubMapOfKeys(expected, l);
                }
            } else {
                if (count <= -expected().keySet().size()) {
                    assertMultimapEquals(expected().asMultimap(), l, isKeysOrdered(), isValuesOrdered());
                } else {
                    assertEquals(count, -l.keySet().size());
                    // TODO assertIsSubMapOfKeys(expected, l);
                }
            }
        }
    }

    @FailWith(IllegalArgumentException.class)
    public void takeZero() {
        actual().take(0);
    }

    @RndTest
    public void takeValues() {
        int maximumSize = 1;
        @SuppressWarnings("unchecked")
        Map.Entry<K, Collection<V>>[] exp = expected().asMap().entrySet().toArray(new Map.Entry[0]);
        for (Map.Entry<K, Collection<V>> e : exp) {
            maximumSize = Math.max(maximumSize, e.getValue().size());
        }

        long count = randomTakeCount(maximumSize);
        if (isValuesOrdered()) {
            Multimap<K, V> m = newMultiMap();
            for (Map.Entry<K, Collection<V>> e : exp) {
                K key = e.getKey();
                List<V> tmp = (List<V>) e.getValue();
                if (count > 0) {
                    for (int i = 0; i < (count > tmp.size() ? tmp.size() : (int) count); i++) {
                        m.put(key, tmp.get(i));
                    }
                } else {
                    int startIndex = (count + tmp.size() < 0) ? 0 : (int) (count + tmp.size());
                    for (int i = startIndex; i < tmp.size(); i++) {
                        m.put(key, tmp.get(i));
                    }
                }
            }
            setNext(m, actual().takeValues(count), isKeysOrdered(), isValuesOrdered());
        } else {
            // Todo unordered
        }
    }

    @FailWith(IllegalArgumentException.class)
    public void takeValuesZero() {
        actual().takeValues(0);
    }
}
