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

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cakeframework.util.Multimap;

/**
 *
 * @author Kasper Nielsen
 */
public class Collection2 {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <K, V> UnmodifiableMultimap<K, V> unmodifiableMultimap(Multimap<? extends K, ? extends V> m) {
        return new UnmodifiableMultimap<>((Multimap) m);
    }

    static class UnmodifiableMultimap<K, V> implements Multimap<K, V> {
        Multimap<K, V> m;

        /**
         * @param m
         */
        public UnmodifiableMultimap(Multimap<K, V> m) {
            this.m = requireNonNull(m);
        }

        /** {@inheritDoc} */
        @Override
        public Map<K, Collection<V>> asMap() {
            return Collections.unmodifiableMap(m.asMap());
        }

        /**
         *
         * @see org.cakeframework.util.Multimap#clear()
         */
        public void clear() {
            throw new UnsupportedOperationException();
        }

        /**
         * @param key
         * @param value
         * @return
         * @see org.cakeframework.util.Multimap#contains(java.lang.Object, java.lang.Object)
         */
        public boolean contains(Object key, Object value) {
            return m.contains(key, value);
        }

        /**
         * @param key
         * @return
         * @see org.cakeframework.util.Multimap#containsKey(java.lang.Object)
         */
        public boolean containsKey(Object key) {
            return m.containsKey(key);
        }

        /**
         * @param value
         * @return
         * @see org.cakeframework.util.Multimap#containsValue(java.lang.Object)
         */
        public boolean containsValue(Object value) {
            return m.containsValue(value);
        }

        /**
         * @return
         * @see org.cakeframework.util.Multimap#entries()
         */
        public Collection<Entry<K, V>> entries() {
            return Collections.unmodifiableCollection(m.entries());
        }

        /**
         * @param o
         * @return
         * @see org.cakeframework.util.Multimap#equals(java.lang.Object)
         */
        public boolean equals(Object o) {
            return m.equals(o);
        }

        /**
         * @param key
         * @return
         * @see org.cakeframework.util.Multimap#get(java.lang.Object)
         */
        public Collection<V> get(Object key) {
            return m.get(key);
        }

        /**
         * @return
         * @see org.cakeframework.util.Multimap#hashCode()
         */
        public int hashCode() {
            return m.hashCode();
        }

        /**
         * @return
         * @see org.cakeframework.util.Multimap#isEmpty()
         */
        public boolean isEmpty() {
            return m.isEmpty();
        }

        /**
         * @return
         * @see org.cakeframework.util.Multimap#keySet()
         */
        public Set<K> keySet() {
            return Collections.unmodifiableSet(m.keySet());
        }

        /**
         * @param key
         * @param value
         * @return
         * @see org.cakeframework.util.Multimap#put(java.lang.Object, java.lang.Object)
         */
        public boolean put(K key, V value) {
            throw new UnsupportedOperationException();
        }

        /**
         * @param key
         * @param values
         * @see org.cakeframework.util.Multimap#putAll(java.lang.Object, java.lang.Iterable)
         */
        public void putAll(K key, Iterable<? extends V> values) {
            throw new UnsupportedOperationException();
        }

        /**
         * @param multimap
         * @see org.cakeframework.util.Multimap#putAll(org.cakeframework.util.Multimap)
         */
        public void putAll(Multimap<? extends K, ? extends V> multimap) {
            throw new UnsupportedOperationException();
        }

        /**
         * @param key
         * @return
         * @see org.cakeframework.util.Multimap#remove(java.lang.Object)
         */
        public Collection<V> remove(Object key) {
            throw new UnsupportedOperationException();
        }

        /**
         * @param key
         * @param value
         * @return
         * @see org.cakeframework.util.Multimap#remove(java.lang.Object, java.lang.Object)
         */
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        /**
         * @return
         * @see org.cakeframework.util.Multimap#size()
         */
        public int size() {
            return m.size();
        }

        /**
         * @return
         * @see org.cakeframework.util.Multimap#values()
         */
        public Collection<V> values() {
            return Collections.unmodifiableCollection(m.values());
        }

    }
}
