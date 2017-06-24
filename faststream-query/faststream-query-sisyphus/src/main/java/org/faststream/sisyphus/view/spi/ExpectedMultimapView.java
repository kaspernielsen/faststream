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
package org.faststream.sisyphus.view.spi;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;

import io.faststream.query.internal.util.Collection2;
import io.faststream.query.internal.util.Multimaps;
import io.faststream.query.util.Multimap;
import io.faststream.sisyphus.expected.Expected;

/**
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class ExpectedMultimapView<K, V> extends Expected {
    private final Multimap<K, V> entries;

    final boolean isKeysOrdered;

    final boolean isValuesOrdered;

    public ExpectedMultimapView(Multimap<K, V> entries, boolean isKeysOrdered, boolean isValuesOrdered) {
        this.entries = Collection2.unmodifiableMultimap(entries);
        this.isKeysOrdered = isKeysOrdered;
        this.isValuesOrdered = isValuesOrdered;
    }

    public Map<K, Collection<V>> asMap() {
        return new LinkedHashMap<>(entries.asMap());
    }

    public Multimap<K, V> asMultimap() {
        return entries;
    }

    public List<Map.Entry<K, V>> entries() {
        ArrayList<Map.Entry<K, V>> l = new ArrayList<>();
        for (Entry<K, Collection<V>> n : entries.asMap().entrySet()) {
            for (V value : n.getValue()) {
                AbstractMap.SimpleEntry<K, V> e = new AbstractMap.SimpleEntry<>(n.getKey(), value);
                l.add(e);
            }
        }
        return Collections.unmodifiableList(l);
    }

    public ExpectedMultimapView<K, V> filter(BiPredicate<? super K, ? super V> filter) {
        Multimap<K, V> map = Multimaps.newOrderedListMultimap();
        for (Entry<K, Collection<V>> e : entries.asMap().entrySet()) {
            K key = e.getKey();
            for (V value : e.getValue()) {
                if (filter.test(key, value)) {
                    map.put(key, value);
                }
            }
        }
        return new ExpectedMultimapView<>(map, false, false);
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public ExpectedCollectionView<K> keySet() {
        return new ExpectedCollectionView<>(new ArrayList<>(entries.keySet()), isKeysOrdered);
    }

    public int size() {
        return entries.size();
    }

    @SuppressWarnings("rawtypes")
    public ExpectedMultimapView<K, V> sortByKey(final Comparator comparator) {
        Multimap<K, V> result = Multimaps.newOrderedListMultimap();
        ArrayList<Entry<K, Collection<V>>> l = new ArrayList<>(entries.asMap().entrySet());
        Collections.sort(l, new Comparator<Entry<K, Collection<V>>>() {
            @SuppressWarnings("unchecked")
            public int compare(Entry<K, Collection<V>> o1, Entry<K, Collection<V>> o2) {
                return comparator.compare(o1.getKey(), o2.getKey());
            }
        });
        for (Map.Entry<K, Collection<V>> entry : l) {
            result.putAll(entry.getKey(), entry.getValue());
        }
        return new ExpectedMultimapView<>(result, true, isValuesOrdered);
    }

    public ExpectedCollectionView<V> values() {
        return new ExpectedCollectionView<>(new ArrayList<>(entries.values()), false);
    }

    /**
     * @return
     */
    public boolean isKeysOrdered() {
        return isKeysOrdered;
    }

    /**
     * @return
     */
    public boolean isValuesOrdered() {
        return isValuesOrdered;
    }
}
