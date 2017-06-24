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
package org.cakeframework.internal.util;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.cakeframework.util.Multimap;

/**
 * The default implementation of {@link Multimap}.
 * 
 * @param <K>
 *            the type of keys in the multimap
 * @param <V>
 *            the type of values in the multimap
 * @author Kasper Nielsen
 */
@SuppressWarnings("serial")
public class HashMultimap<K, V> implements Multimap<K, V>, Serializable {
    private transient Map<K, Collection<V>> asMap;

    private transient Collection<Map.Entry<K, V>> entries;

    private transient Set<K> keySet;

    transient Map<K, Collection<V>> map = new LinkedHashMap<>();

    transient long size;

    private transient Collection<V> values;

    @Override
    public Map<K, Collection<V>> asMap() {
        Map<K, Collection<V>> am = asMap;
        return (am != null) ? am : (asMap = newAsMap());
    }

    @Override
    public void clear() {
        // Clear each collection, to make previously returned collections empty.
        for (Collection<V> collection : map.values()) {
            collection.clear();
        }
        map.clear();
        size = 0;
    }

    @Override
    public boolean contains(Object key, Object value) {
        Collection<V> collection = map.get(key);
        return collection != null && collection.contains(value);
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (Collection<V> collection : map.values()) {
            if (collection.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public Collection<Map.Entry<K, V>> entries() {
        Collection<Map.Entry<K, V>> es = entries;
        return (es != null) ? es : (entries = newEntries());
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Multimap) {
            Multimap<?, ?> that = (Multimap<?, ?>) object;
            return this.map.equals(that.asMap());
        }
        return false;
    }

    public Collection<V> get(Object key) {
        Collection<V> col = map.get(key);
        if (col == null) {
            col = newValueContainer();
        }
        return unmodifiableCollection(col);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> ks = this.keySet;
        return (ks != null) ? ks : (keySet = newKeySet());
    }

    private Collection<V> lazyCreateValueContainer(K key) {
        Collection<V> col = map.get(key);
        if (col == null) {
            col = newValueContainer();
            map.put(key, col);
        }
        return col;
    }

    protected Map<K, Collection<V>> newAsMap() {
        return Collections.unmodifiableMap(map);
        // return new AsMap();
    }

    protected Collection<Map.Entry<K, V>> newEntries() {
        return new Entries();
    }

    protected Set<K> newKeySet() {
        return new KeySet();
    }

    protected Collection<V> newValueContainer() {
        return new ArrayList<>(1);
    }

    protected Collection<V> newValues() {
        return new Values();
    }

    @Override
    public boolean put(K key, V value) {
        Collection<V> col = lazyCreateValueContainer(key);
        if (col.add(value)) {
            size++;
            return true;
        } else {
            return false;
        }
    }

    public void putAll(K key, Iterable<? extends V> values) {
        Iterator<? extends V> i = values.iterator();
        if (i.hasNext()) {
            Collection<V> col = lazyCreateValueContainer(key);
            int previousSize = col.size();
            col.add(i.next());
            while (i.hasNext()) {
                col.add(i.next());
            }
            size = size + col.size() - previousSize;
        }
    }

    public void putAll(Multimap<? extends K, ? extends V> multimap) {
        for (Map.Entry<? extends K, ? extends V> entry : multimap.entries()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /** {@inheritDoc} */
    @Override
    public Collection<V> remove(Object key) {
        Collection<V> col = map.remove(key);
        if (col != null) {
            size -= col.size();
        }
        return col;
    }

    public boolean remove(Object key, Object value) {
        Collection<V> col = map.get(key);
        if (col != null) {
            boolean removed = col.remove(value);
            if (removed) {
                size--;
                if (col.isEmpty()) {
                    map.remove(key);
                }
                return true;
            }
        }
        return false;

    }

    public Collection<V> removeAll(Object key) {
        Collection<V> col = map.remove(key);
        Collection<V> result = newValueContainer();
        if (col != null) {
            result.addAll(col);
            size -= col.size();
            col.clear();
        }
        return unmodifiableCollection(result);
    }

    @Override
    public int size() {
        long size = this.size;
        return size <= Integer.MAX_VALUE ? (int) size : Integer.MAX_VALUE;
    }

    /**
     * Returns a string representation of the multimap, generated by calling {@code toString} on the map returned by
     * {@link Multimap#asMap}.
     * 
     * @return a string representation of the multimap
     */
    @Override
    public String toString() {
        return map.toString();
    }

    private Collection<V> unmodifiableCollection(Collection<V> collection) {
        if (collection instanceof List) {
            return Collections.unmodifiableList((List<V>) collection);
        } else {
            return Collections.unmodifiableCollection(collection);
        }
    }

    @Override
    public Collection<V> values() {
        Collection<V> val = values;
        return (val != null) ? val : (values = newValues());
    }

    class AsMap extends HashMap<K, Collection<V>> {

    }

    class Entries extends AbstractCollection<Map.Entry<K, V>> {
        @Override
        public void clear() {
            HashMultimap.this.clear();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Map.Entry<?, ?>) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                return HashMultimap.this.contains(entry.getKey(), entry.getValue());
            }
            return false;
        }

        @Override
        public boolean isEmpty() {
            return HashMultimap.this.isEmpty();
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof Map.Entry<?, ?>) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                return HashMultimap.this.remove(entry.getKey(), entry.getValue());
            }
            return false;
        }

        @Override
        public int size() {
            return HashMultimap.this.size();
        }
    }

    class EntryIterator implements Iterator<Map.Entry<K, V>> {
        Collection<V> currentCollection;
        K currentKey;
        final Iterator<Map.Entry<K, Collection<V>>> iter;
        Iterator<V> valueIterator;

        EntryIterator() {
            iter = map.entrySet().iterator();
            advance();
        }

        void advance() {
            if (iter.hasNext()) {
                Map.Entry<K, Collection<V>> entry = iter.next();
                currentKey = entry.getKey();
                currentCollection = entry.getValue();
                valueIterator = currentCollection.iterator();
            } else {
                valueIterator = Collections.emptyIterator();
            }
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext() || valueIterator.hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!valueIterator.hasNext()) {
                advance();
            }
            return new AbstractMap.SimpleEntry<>(currentKey, valueIterator.next());
        }

        @Override
        public void remove() {
            valueIterator.remove();
            if (currentCollection.isEmpty()) {
                iter.remove();
                advance();
            }
            size--;
        }
    }

    class KeySet extends HashSet<K> {
        @Override
        public void clear() {
            HashMultimap.this.clear();
        }

        @Override
        public boolean contains(Object o) {
            return HashMultimap.this.containsKey(o);
        }

        @Override
        public boolean isEmpty() {
            return HashMultimap.this.isEmpty();
        }

        public Stream<K> stream() {
            throw new UnsupportedOperationException();
        }

        public Stream<K> parallelStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<K> iterator() {
            final Iterator<Map.Entry<K, Collection<V>>> iter = map.entrySet().iterator();
            return new Iterator<K>() {
                Map.Entry<K, Collection<V>> entry;

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public K next() {
                    entry = iter.next();
                    return entry.getKey();
                }

                @Override
                public void remove() {
                    if (entry == null) {
                        iter.remove();// fails
                    }
                    Collection<V> collection = entry.getValue();
                    iter.remove();
                    size -= collection.size();
                    collection.clear();
                }
            };
        }

        @Override
        public int size() {
            return map.size();
        }
    }

    class Values extends AbstractCollection<V> {
        @Override
        public void clear() {
            HashMultimap.this.clear();
        }

        @Override
        public boolean contains(Object o) {
            return HashMultimap.this.containsValue(o);
        }

        @Override
        public boolean isEmpty() {
            return HashMultimap.this.isEmpty();
        }

        @Override
        public Iterator<V> iterator() {
            final Iterator<Map.Entry<K, V>> iter = entries().iterator();
            return new Iterator<V>() {
                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public V next() {
                    return iter.next().getValue();
                }

                @Override
                public void remove() {
                    iter.remove();
                }
            };
        }

        @Override
        public int size() {
            return HashMultimap.this.size();
        }
    }
}
