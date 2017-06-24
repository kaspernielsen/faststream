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
package io.faststream.query.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * An object that maps keys to values. Unlike {@link Map} a multimap allows more than one value to be associated with a
 * given key.
 * 
 * @param <K>
 *            the type of keys maintained by this multimap
 * @param <V>
 *            the type of mapped values
 * @author Kasper Nielsen
 */
public interface Multimap<K, V> {

    /**
     * Returns a {@link Map} view of the mappings contained in this multimap. The map is backed by the multimap, so
     * changes to the multimap are reflected in the map, and vice-versa. If the multimap is modified while an iteration
     * over any of the maps views is in progress (except through the iterator's own <tt>remove</tt> operation), the
     * results of the iteration are undefined.
     * 
     * @return a map view of the entries contained in this multimap
     */
    // TODO javadoc which methods on the map are supported??
    Map<K, Collection<V>> asMap();

    /**
     * Removes all of the mappings from this multimap (optional operation). The multimap will be empty after this call
     * returns.
     * 
     * @throws UnsupportedOperationException
     *             if the <tt>clear</tt> operation is not supported by this multimap
     */
    void clear();

    /**
     * Returns <tt>true</tt> if this multimap contains one or more mappings from the specified key to the specified
     * value.
     * 
     * @param key
     *            key whose presence in this multimap is to be tested
     * @param value
     *            value whose presence in this multimap is to be tested
     * @return true if the map contains the mapping, otherwise false
     * @throws ClassCastException
     *             if the key or value is of an inappropriate type for this multimap (<a
     *             href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException
     *             if the specified key or value is null and this multimap does not permit null keys or values (<a
     *             href="Collection.html#optional-restrictions">optional</a>)
     */
    boolean contains(Object key, Object value);

    /**
     * Returns <tt>true</tt> if this multimap contains a mapping for the specified key. More formally, returns
     * <tt>true</tt> if and only if this multimap contains a mapping for a key <tt>k</tt> such that
     * <tt>(key==null ? k==null : key.equals(k))</tt>. (There can be at most one such mapping.)
     * 
     * @param key
     *            key whose presence in this multimap is to be tested
     * @return <tt>true</tt> if this multimap contains a mapping for the specified key
     * @throws ClassCastException
     *             if the key is of an inappropriate type for this multimap (<a
     *             href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException
     *             if the specified key is null and this multimap does not permit null keys (<a
     *             href="Collection.html#optional-restrictions">optional</a>)
     */
    boolean containsKey(Object key);

    /**
     * Returns <tt>true</tt> if this multimap has one or more keys that maps to the specified value. More formally,
     * returns <tt>true</tt> if and only if this multimap contains at least one mapping from any key to to a value
     * <tt>v</tt> such that <tt>(value==null ? v==null : value.equals(v))</tt>. This operation will probably require
     * time linear in the size of the multimap for most implementations of this interface.
     * 
     * @param value
     *            value whose presence in this multimap is to be tested
     * @return <tt>true</tt> if this multimap contains at least one from any key to the specified value
     * @throws ClassCastException
     *             if the value is of an inappropriate type for this multimap (<a
     *             href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException
     *             if the specified value is null and this multimap does not permit null values (<a
     *             href="Collection.html#optional-restrictions">optional</a>)
     */
    boolean containsValue(Object value);

    /**
     * Returns a {@link Collection} view of the mappings contained in this multimap. The collection is backed by the
     * multimap, so changes to the multimap are reflected in the collection, and vice-versa. If the multimap is modified
     * while an iteration over the collection is in progress (except through the iterator's own <tt>remove</tt>
     * operation, or through the <tt>setValue</tt> operation on a map entry returned by the iterator) the results of the
     * iteration are undefined. The collection supports element removal, which removes the corresponding mapping from
     * the multimap, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations. It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     * 
     * @return a collection view of the mappings contained in this multimap
     */
    Collection<Map.Entry<K, V>> entries();

    /**
     * Compares the specified object with this multimap for equality. Returns <tt>true</tt> if the given object is also
     * a multimap and the two multimaps represent the same mappings with respect to any defined order on values. Two
     * multimaps are equal when their map views, as returned by {@link #asMap}, are also equal.
     * 
     * @param o
     *            object to be compared for equality with this multimap
     * @return <tt>true</tt> if the specified object is equal to this multimap
     * @see Object#equals(Object)
     * @see #hashCode()
     */
    boolean equals(Object o);

    // A view or an immutable copy???
    Collection<V> get(Object key);

    /**
     * Returns the hash code value for this multimap. The hash code of a multimap is defined as the hash code of the map
     * view, as returned by {@link Multimap#asMap}. This ensures that <tt>m1.equals(m2)</tt> implies that
     * <tt>m1.hashCode()==m2.hashCode()</tt> for any two multimaps <tt>m1</tt> and <tt>m2</tt>, as required by the
     * general contract of {@link Object#hashCode}.
     * 
     * @return the hash code value for this multimap
     * @see Map#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    int hashCode();

    /**
     * Returns <tt>true</tt> if this multimap contains no key-value mappings. Otherwise <tt>false</tt>.
     * 
     * @return <tt>true</tt> if this multimap contains no key-value mappings
     */
    boolean isEmpty();

    /**
     * Returns a {@link Set} view of the keys contained in this multimap. The set is backed by the multimap, so changes
     * to the multimap are reflected in the set, and vice-versa. If the multimap is modified while an iteration over the
     * set is in progress (except through the iterator's own <tt>remove</tt> operation), the results of the iteration
     * are undefined. The set supports element removal, which removes the corresponding mapping from the multimap, via
     * the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations. It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     * 
     * @return a set view of the keys contained in this multimap
     */
    Set<K> keySet();

    boolean put(K key, V value);

    void putAll(K key, Iterable<? extends V> values);

    void putAll(Multimap<? extends K, ? extends V> multimap);

    Collection<V> remove(Object key);

    boolean remove(Object key, Object value);

    /**
     * Returns the number of key-value mappings in this map. If the map contains more than <tt>Integer.MAX_VALUE</tt>
     * elements, returns <tt>Integer.MAX_VALUE</tt>.
     * <p>
     * The size returned by this method is equivalent to the value returned by calling <code>values().size()</code>.
     * <code>keySet().size()</code> can be used to obtain the number of unique keys.
     * 
     * @return the number of key-value mappings in this map
     */
    int size();

    /**
     * Returns a {@link Collection} view of the values contained in this multimap. The collection is backed by the
     * multimap, so changes to the multimap are reflected in the collection, and vice-versa. If the multimap is modified
     * while an iteration over the collection is in progress (except through the iterator's own <tt>remove</tt>
     * operation), the results of the iteration are undefined. The collection supports element removal, which removes
     * the corresponding mapping from the multimap, via the <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations. It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.
     * 
     * @return a collection view of the values contained in this multimap
     */
    Collection<V> values();
}
