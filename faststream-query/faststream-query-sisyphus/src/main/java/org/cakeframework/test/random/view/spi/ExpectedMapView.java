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
package org.cakeframework.test.random.view.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.cakeframework.util.BiComparator;

import io.faststream.sisyphus.expected.Expected;

/**
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class ExpectedMapView<K, V> extends Expected {
    final boolean isOrdered;
    private final Map<K, V> map;

    public ExpectedMapView(Map<K, V> map, boolean isOrdered) {
        this.map = Collections.unmodifiableMap(map);
        this.isOrdered = isOrdered;
    }

    public Map<K, V> asMap() {
        return map;
    }

    public List<Map.Entry<K, V>> entrySet() {
        ArrayList<Map.Entry<K, V>> l = new ArrayList<>();
        for (Map.Entry<K, V> e : map.entrySet()) {
            Entry<K, V> ee = new Entry<>(e.getKey(), e.getValue());
            l.add(ee);
        }
        return l;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * @return
     */
    public boolean isOrdered() {
        return isOrdered;
    }

    public ExpectedCollectionView<K> keySet() {
        return new ExpectedCollectionView<>(new ArrayList<>(map.keySet()), isOrdered);
    }

    @SuppressWarnings("rawtypes")
    public ExpectedMapView<K, V> newSortedByKey(final Comparator comparator) {
        return sorted(new BiComparator<K, V>() {
            @SuppressWarnings("unchecked")
            @Override
            public int compare(K s1, V t1, K s2, V t2) {
                return comparator.compare(s1, s2);
            }
        });
    }

    public ExpectedMapView<K, V> newSortedByValue(final Comparator<V> comparator) {
        return sorted(new BiComparator<K, V>() {
            @Override
            public int compare(K s1, V t1, K s2, V t2) {
                return comparator.compare(t1, t2);
            }
        });
    }

    public int size() {
        return map.size();
    }

    public ExpectedMapView<K, V> sorted(final BiComparator<K, V> comparator) {
        List<Map.Entry<K, V>> l = entrySet();
        Collections.sort(l, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return comparator.compare(o1.getKey(), o1.getValue(), o2.getKey(), o2.getValue());
            }
        });
        // /Collections.reverse(l);//the last element
        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : l) {
            result.put(entry.getKey(), entry.getValue());
        }
        return new ExpectedMapView<>(result, true);
    }

    public ExpectedCollectionView<V> values() {
        return new ExpectedCollectionView<>(new ArrayList<>(map.values()), isOrdered);
    }

    static class Entry<K, V> implements Map.Entry<K, V> {
        private final K key;

        private final V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry) obj;
            return Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue());
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            return key + "=" + value;
        }
    }
}
