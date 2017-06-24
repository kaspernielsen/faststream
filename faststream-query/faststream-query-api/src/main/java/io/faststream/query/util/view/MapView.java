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
package io.faststream.query.util.view;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import io.faststream.query.util.BiComparator;

/**
 * A read-only (virtual) view of a collection of mappings from keys to values. A map view cannot contain duplicate keys;
 * each key can map to at most one value.
 *
 * @param <K>
 *            the type of keys
 * @param <V>
 *            the type of values
 */
public interface MapView<K, V> {

    /**
     * Returns a new collection view with all entries in this view as a {@link Entry}.
     * <p>
     * The entries in the returned view are immutable. That is, trying to modify their contents by using
     * {@link Entry#setValue(Object)} will result in an {@link UnsupportedOperationException} being thrown.
     * <p>
     * The following example extract all entries in this view as a {@link List}:
     *
     * <pre>
     * List&lt;Map.Entry&lt;K, V&gt;&gt; list = mapview.entries().toList();
     * </pre>
     *
     * @return a view of the entries in this view
     */
    CollectionView<Map.Entry<K, V>> entries();

    /**
     * Returns a new view retaining only those entries that are accepted by the specified filter.
     * <p>
     * Implementations may, but are not required to, preserve any previous ordering made to this view when invoking this
     * method.
     *
     * @param filter
     *            the filter used to evaluate whether an entry should be retained
     * @return a new view where all entries are accepted by the specified filter
     * @throws NullPointerException
     *             if the specified filter is {@code null}
     */
    MapView<K, V> filter(BiPredicate<? super K, ? super V> filter);

    /**
     * Returns a view retaining all entries where the value part is <tt>non-null</tt>.
     * <p>
     * If this view is known to contain only <tt>non-null</tt> values the view is allowed to return <tt>this</tt>.
     *
     * @return a view retaining all entries where the value part is <tt>non-null</tt>
     */
    MapView<K, V> filterNullValues();

    /**
     * Returns a new view retaining only those entries where the key part of each entry is accepted by the specified
     * filter.
     * <p>
     * Implementations may, but are not required to, preserve any previous ordering made to this view when invoking this
     * method.
     *
     * @param filter
     *            the filter used to evaluate whether an entry should be included in the new view
     * @return a new view where the the key part of all entries are accepted by the specified filter
     * @throws NullPointerException
     *             if the specified predicate is {@code null}
     */
    MapView<K, V> filterOnKey(Predicate<? super K> filter);

    /**
     * Returns a new view retaining only those entries where the key part of each entry is of the specified type.
     * <p>
     * Implementations may, but are not required to, preserve any previous ordering made to this view when invoking this
     * method.
     *
     * @param <U>
     *            the type of keys that should be retained
     * @param type
     *            the type of keys that should be retained in the new view
     * @return a new view retaining only those entries where the key is of the specified type
     * @throws NullPointerException
     *             if the specified class is {@code null}
     */
    <U extends K> MapView<U, V> filterOnKeyType(Class<U> type);

    /**
     * Returns a new view retaining only those entries where the value part of each entry is accepted by the specified
     * filter.
     * <p>
     * Implementations may, but are not required to, preserve any previous ordering made to this view when invoking this
     * method.
     *
     * @param filter
     *            the filter used to evaluate whether an entry should be included in the new view
     * @return a new view where the the value part of all entries are accepted by the specified filter
     * @throws NullPointerException
     *             if the specified predicate is {@code null}
     */
    MapView<K, V> filterOnValue(Predicate<? super V> filter);

    /**
     * Returns a new view retaining only those entries where the value part of each entry is of the specified type.
     * <p>
     * Implementations may, but are not required to, preserve any previous ordering made to this view when invoking this
     * method.
     *
     * @param <U>
     *            the type of values that should be retained
     * @param type
     *            the type of values that should be retained in the new view
     * @return a new view retaining only those entries where the value is of the specified type
     * @throws NullPointerException
     *             if the specified class is null
     */
    <U extends V> MapView<K, U> filterOnValueType(Class<U> type);

    /**
     * Applies the specified procedure to all entries in this view.
     *
     * @param procedure
     *            the procedure to apply
     * @throws NullPointerException
     *             if the specified procedure is {@code null}
     */
    void forEach(BiConsumer<? super K, ? super V> procedure);

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings, otherwise <tt>false</tt>.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings, otherwise <tt>false</tt>
     */
    boolean isEmpty();

    /**
     * Returns a new view with the {@link Entry#getKey() key part} of all key-value mappings in this view.
     * <p>
     * The following example extract all keys in this view as a {@link List}:
     *
     * <pre>
     * List&lt;K&gt; list = mapview.keys().toList();
     * </pre>
     *
     * @return the new view
     */
    CollectionView<K> keys();

    /**
     * Returns a new view where the key and value of each entry is mapped using the specified mapper.
     *
     * @param <E>
     *            the type of the new view
     * @param mapper
     *            the mapper that combine keys and values into a new element
     * @return the new combined view
     * @throws NullPointerException
     *             if the specified mapper is null
     */
    <E> CollectionView<E> map(BiFunction<? super K, ? super V, ? extends E> mapper);

    /**
     * Returns a new multimap view where the key part of each entry is mapped using the specified mapper.
     *
     * @param <U>
     *            the of keys to map to
     * @param mapper
     *            the mapper
     * @return the new view
     * @throws NullPointerException
     *             if the specified mapper is null
     */
    <U> MultimapView<U, V> mapKey(Function<? super K, ? extends U> mapper);

    /**
     * Returns a new view where the value part of each entry is mapped using the specified mapper.
     * <p>
     * If this view is ordered the returned view will preserved the ordering.
     *
     * @param <U>
     *            the of values to map to
     * @param mapper
     *            the mapper
     * @return the new view
     * @throws NullPointerException
     *             if the specified mapper is null
     */
    <U> MapView<K, U> mapValue(Function<? super V, ? extends U> mapper);

    /**
     * Returns the number of key-value mappings in this view. If the view contains more than <tt>Long.MAX_VALUE</tt>
     * mappings, returns <tt>Long.MAX_VALUE</tt>.
     *
     * @return the number of key-value mappings in this view
     */
    long size();

    /**
     * Returns a new view where all entries are ordered in accordance with the specified comparator.
     * <p>
     * Unlike {@link Arrays#sort(Object[])} this method does not guaranteed that equal entries maintain their order in
     * the returned view. In other words the sort used by this method it not guaranteed to be stable. This is especially
     * true if multiple threads are used internally.
     *
     * @param comparator
     *            the comparator used for ordering entries
     * @throws NullPointerException
     *             if the specified comparator is null
     * @return the new view
     */
    MapView<K, V> sorted(BiComparator<? super K, ? super V> comparator);

    /**
     * Assuming all keys are Comparable, returns a new view where all entries are ordered accordingly to the keys
     * natural order. Furthermore, all keys in the view must be <i>mutually comparable</i> (that is,
     * <tt>k1.compareTo(k2)</tt> must not throw a <tt>ClassCastException</tt> for any elements <tt>k1</tt> and
     * <tt>k2</tt> in this view).
     * <p>
     * Unless otherwise specified the ordering is not guaranteed to be <i>stable</i>: equal keys might be reordered as a
     * result of the sort. In other words the sort used by this method it not guaranteed to be stable. This is
     * especially true if the implementations uses multiple threads to sort the elements.
     *
     * @return the new view
     */
    MapView<K, V> sortedByKey();

    /**
     * Returns a new view where all entries are ordered by the key part of the entry in accordance with the specified
     * comparator.
     * <p>
     * Unlike {@link Arrays#sort(Object[])} this method does not guaranteed that equal entries maintain their order in
     * the returned view. In other words the sort used by this method it not guaranteed to be stable. This is especially
     * true if multiple threads are used internally.
     *
     * @param comparator
     *            the comparator used for ordering entries
     * @throws NullPointerException
     *             if the specified comparator is null
     * @return the new view
     */
    MapView<K, V> sortedByKey(Comparator<? super K> comparator);

    /**
     *
     * Assuming all keys are Comparable, returns a new view where all entries are ordered accordingly to the keys
     * natural order. Furthermore, all keys in the view must be <i>mutually comparable</i> (that is,
     * <tt>k1.compareTo(k2)</tt> must not throw a <tt>ClassCastException</tt> for any elements <tt>k1</tt> and
     * <tt>k2</tt> in this view).
     * <p>
     * Unless otherwise specified the ordering is not guaranteed to be <i>stable</i>: equal keys might be reordered as a
     * result of the sort. In other words the sort used by this method it not guaranteed to be stable. This is
     * especially true if the implementations uses multiple threads to sort the elements.
     *
     * @return the new view
     */
    MapView<K, V> sortedByKeyDescending();

    /**
     * Assuming all values are Comparable, returns a new view where all entries are ordered accordingly to the values
     * natural order. Furthermore, all values in the view must be <i>mutually comparable</i> (that is,
     * <tt>v1.compareTo(v2)</tt> must not throw a <tt>ClassCastException</tt> for any elements <tt>v1</tt> and
     * <tt>v2</tt> in this view).
     * <p>
     * Unless otherwise specified the ordering is not guaranteed to be <i>stable</i>: equal keys might be reordered as a
     * result of the sort. In other words the sort used by this method it not guaranteed to be stable. This is
     * especially true if the implementations uses multiple threads to sort the elements.
     *
     * @return the new view
     */
    MapView<K, V> sortedByValue();

    /**
     * Returns a new view where all entries are ordered by the value part of the entry in accordance with the specified
     * comparator.
     * <p>
     * Unlike {@link Arrays#sort(Object[])} this method does not guaranteed that equal entries maintain their order in
     * the returned view. In other words the sort used by this method it not guaranteed to be stable. This is especially
     * true if multiple threads are used internally.
     *
     * @param comparator
     *            the comparator used for ordering entries
     * @throws NullPointerException
     *             if the specified comparator is null
     * @return the new view
     */
    MapView<K, V> sortedByValue(Comparator<? super V> comparator);

    /**
     * Assuming all values are Comparable, returns a new view where all entries are ordered accordingly to the values
     * natural order. Furthermore, all values in the view must be <i>mutually comparable</i> (that is,
     * <tt>v1.compareTo(v2)</tt> must not throw a <tt>ClassCastException</tt> for any elements <tt>v1</tt> and
     * <tt>v2</tt> in this view).
     * <p>
     * Unless otherwise specified the ordering is not guaranteed to be <i>stable</i>: equal keys might be reordered as a
     * result of the sort. In other words the sort used by this method it not guaranteed to be stable. This is
     * especially true if the implementations uses multiple threads to sort the elements.
     *
     * @return the new view
     */
    MapView<K, V> sortedByValueDescending();

    /**
     * Selects the first or last n key-value mappings of this view. See also {@link CollectionView#take(long)}.
     * <p>
     * NOTE: If the view is not ordered repeated calls to this method might return different results.
     *
     * @param numberOfEntriesToTake
     *            if positive the number of first elements to take, if negative the number of last elements to take
     * @return a map view consisting only the first or last n elements of this view. If this view has less then abs(n)
     *         key-value mapping. The returned view will retain all elements
     * @throws IllegalArgumentException
     *             if <tt>numberOfEntriesToTake</tt> is 0
     */
    MapView<K, V> take(long numberOfEntriesToTake);

    /**
     * Extracts all elements from this view to a data structure of the specified type. All concrete types implementing
     * {@link Map} is supported as long as they obey the Map contract by having a constructor with a single argument of
     * type <tt>Map</tt>, which creates a new map with the same elements as its argument.
     * <p>
     * Any interface from java.util or java.util.concurrent that extends {@link Map} can also be specified. In which
     * case the view decides which particular implementation to use. Implementations should prefer returning
     * memory-compact and immutable versions of the interface.
     * <p>
     * The returned data structure will be "safe" in that no references to it or any of its elements are maintained by
     * this view.
     * <p>
     * NOTE: be wary when specifying {@link SortedMap} (or one of is descendens) to this method. SortedMap assumes that
     * entries are sorted in accordance to the key part or each entry. Hence this contract is broken when a map view has
     * been ordered by, for example, {@link #sortedByValue()}. Instead just use {@link #toMap()} which will return an
     * ordinary {@link Map} where the iterator will return each entry in the right order.
     *
     * @param <T>
     *            the type to convert this view to
     * @param type
     *            the type to convert this view to
     * @return the contents of this view as the specified type
     * @throws IllegalArgumentException
     *             if the contents of this view cannot be converted to the specified type
     * @throws NullPointerException
     *             if the specified type is null
     */
    <T> T to(Class<T> type);

    /**
     * Creates a new {@link Map} with all the mappings in this view.
     * <p>
     * If this view is ordered, the returned map will maintain this ordering when invoking any of its iterators.
     * Returning <tt>entries().head()</tt> as the first entry and <tt>entries().tail()</tt> as the last entry.
     * <p>
     * The returned map will be "safe" in that no references to it or any of its elements are maintained by this view.
     * (In other words, this method must allocate a new map even if this view is somehow backed by a map).
     *
     * @return the new map
     */
    Map<K, V> toMap();

    /**
     * Returns a new view with the {@link Entry#getValue() value part} of all key-value mappings in this view.
     * <p>
     * The following example extract all keys in this view as a {@link List}:
     *
     * <pre>
     * List&lt;K&gt; list = values().toList();
     * </pre>
     *
     * @return the new view
     */
    CollectionView<V> values();

    // Map.Entry<K,V>[] toArray();
    // MapView<K,V> peek(BiConsumer<? super K, ? super V> procedure);
    // <R> R collect(Supplier<R> supplier,
    // TriConsumer<R, ? super K, ? super V> accumulator,
    // BiConsumer<R, R> combiner);
}
