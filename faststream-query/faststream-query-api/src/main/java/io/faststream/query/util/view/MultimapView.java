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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import io.faststream.query.util.Multimap;

/**
 * A multimap view maps every key to one or more values.
 * <p>
 * A multimap view can be ordered in two independent ways. A order can be establish between all keys, for example, by
 * using {@link #sortedKeys(Comparator)}.
 * 
 * As well as each keys individual values can be ordered, for example, by calling {@link #sortedValues()}. These two can
 * easily be combined, given the following multimap
 * 
 * <pre>
 * {1 -&gt; 2, 4, 3}, {5 -&gt; 1}, {3 -&gt; 4, 5, 6, 1, 11, 6}, {2 -&gt; 5}
 * </pre>
 * 
 * calling <code>multimapView.orderByKeys().orderValues()</code> will return a new view with the following layout
 * 
 * <pre>
 * {1 -&gt; 2, 3, 4}, {2 -&gt; 5}, {3 -&gt; 1, 4, 5, 6, 6, 11}, {5 -&gt; 1}
 * </pre>
 * 
 * @param <K>
 *            the type of keys
 * @param <V>
 *            the type of values
 */
public interface MultimapView<K, V> {

    /**
     * Returns a new map view with any of the values that each key maps to.
     * <p>
     * For example given the following multimap view
     * 
     * <pre>
     * {1 -&gt; "B", "C", "D"}, {2 -&gt; "A"}, {3 -&gt; "D", "E", "F", "F", "F"}
     * </pre>
     * 
     * this method might return a map view that looks like this <code>{1 -&gt; "B"}, {2 -&gt; "A"}, {3 -&gt; "D"}</code>.
     * <p>
     * Analogies to {@link CollectionView#any()} the value selected for each key is <tt>not</tt> guaranteed to be a
     * random selection among all possible values.
     * <p>
     * Succint invocation of this method might select different values for each key, even if the underlying data has not
     * changed.
     * 
     * @return an map view with any value for each key
     **/
    MapView<K, V> any();

    /**
     * Counts the number of mappings for each individual key and returns the result as a new map view. For example,
     * invoking this method on the following mapping
     * 
     * <pre>
     * {1 -&gt; "A", "B", "C"}, {2 -&gt; "B"}, {3 -&gt; "B", "F", "G", "J", "P"}, {4 -&gt; "A"}
     * will return the new map view {1 -&gt; 3}, {2 -&gt; 1}, {3 -&gt; 5}, {4 -&gt; 1}
     * </pre>
     * <p>
     * Implementations may, but are not required to, preserve any previous ordering made to this view when invoking this
     * method.
     * 
     * @return a new map view with the number of values for each key
     */
    MapView<K, Long> count();

    /**
     * Returns a view retaining only those values that are <tt>non-null</tt>. For example, invoking this method on the
     * following mapping:
     * 
     * <pre>
     * {1 -&gt; null, "C", null, "D"}, {2 -&gt; "B", "C" }, {4 -&gt; null, null }
     * will return the new multimap view {1 -&gt; "C", "D"}, {2 -&gt; "B", "C"}
     * </pre>
     * <p>
     * If this view is known to contain only <tt>non-null</tt> values this method is allowed to return <tt>this</tt>.
     * 
     * @return a view retaining only those values that are <tt>non-null</tt>.
     */
    MultimapView<K, V> filterNullValues();

    /**
     * Returns a new view retaining only those entries that are accepted by the specified filter.
     * <p>
     * Implementations may, but are not required to, preserve any previous ordering made to this view when invoking this
     * method.
     * 
     * @param filter
     *            the filter used to evaluate whether an entry should be included in the new view
     * @return a view retaining only those entries accepted by the specified filter
     * @throws NullPointerException
     *             if the specified predicate is null
     */
    MultimapView<K, V> filter(BiPredicate<? super K, ? super V> filter);

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
     *             if the specified predicate is null
     */
    MultimapView<K, V> filterOnKey(Predicate<? super K> filter);

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
     *             if the specified class is null
     */
    <U> MultimapView<U, V> filterOnKeyType(Class<U> type);

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
     *             if the specified predicate is null
     */
    MultimapView<K, V> filterOnValue(Predicate<? super V> filter);

    /**
     * Returns a new view retaining only those entries where the value part of each entry is of the specified type.
     * 
     * @param <U>
     *            the type of values that should be retained
     * @param type
     *            the type of values that should be retained in the new view
     * @return a new view retaining only those entries where the value is of the specified type
     * @throws NullPointerException
     *             if the specified class is null
     */
    <U> MultimapView<K, U> filterOnValueType(Class<U> type);

    /**
     * Assuming the the values for each key has been ordered, for example, by a call to {@link #sortedValues(Comparator)}
     * . Returns a new map view with the head value for each key
     * <p>
     * For example, given the following ordered multimap view
     * 
     * <pre>
     * {1 -&gt; 2, 3, 4}, {2 -&gt; 1}, {3 -&gt; 4, 5, 6, 6, 6}
     * </pre>
     * 
     * this method will return a map view like this <code>{1 -&gt; 2}, {2 -&gt; 1}, {3 -&gt; 4}</code>
     * <p>
     * If the values in this view has not been ordered invoking this method is equivalent to invoking {@link #any()}.
     * 
     * @return an map view with the head value for each key
     **/
    MapView<K, V> head();

    /**
     * Returns <tt>true</tt> if this multimap contains no key-value mappings, otherwise <tt>false</tt>.
     * 
     * @return <tt>true</tt> if this multimap contains no key-value mappings, otherwise <tt>false</tt>
     */
    boolean isEmpty();

    /**
     * Returns a new view with all the keys in this view. The following example extract all keys in this view as a
     * {@link List}:
     * 
     * <pre>
     * List&lt;K&gt; list = multimapview.keys().toList();
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
     *            the mapper that combines keys and values into a new element
     * @return the new combined view
     * @throws NullPointerException
     *             if the specified mapper is null
     */
    <E> CollectionView<E> map(BiFunction<? super K, ? super V, ? extends E> mapper);

    /**
     * Creates a new view where are each key is mapped to another key according to the specified mapper.
     * <p>
     * Unlike most other map operations this method does not preserve any previous ordering of the view. Since different
     * keys can be mapped to the same key it does not make sense to attempt to preserve any ordering.
     * 
     * @param <U>
     *            the type of keys to map to
     * @param mapper
     *            the mapper
     * @throws NullPointerException
     *             if the specified mapper is null
     * @return a new view multimap view
     */
    <U> MultimapView<U, V> mapKey(Function<? super K, ? extends U> mapper);

    /**
     * Creates a new view where are all values are mapped accordingly to the specified mapper.
     * 
     * @param <U>
     *            the type of values to map to
     * @param mapper
     *            the mapper
     * @throws NullPointerException
     *             if the specified mapper is null
     * @return a new view
     */
    <U> MultimapView<K, U> mapValue(Function<? super V, ? extends U> mapper);

    /**
     * Assuming all keys map to exactly one value, returns a {@link MapView} with each mapping. If there exists a key
     * that maps to more than 1 value an {@link IllegalStateException} will be thrown at evaluation time.
     * 
     * @return an map view with the one unique mapping for each key
     * @see CollectionView#one()
     **/
    MapView<K, V> one();

    /**
     * Assuming all keys are Comparable, returns a new view where all entries are ordered accordingly to the keys
     * natural order. Furthermore, all keys in the view must be <i>mutually comparable</i> (that is,
     * <tt>k1.compareTo(k2)</tt> must not throw a <tt>ClassCastException</tt> for any elements <tt>k1</tt> and
     * <tt>k2</tt> in this view).
     * <p>
     * Unless otherwise specified the ordering is not guaranteed to be <i>stable</i>: equal keys might be reordered as a
     * result of the sort. In other words the sort used by this method it not guaranteed to be stable. This is
     * especially true if the implementations uses multiple threads to sort the elements.
     * <p>
     * Any previous ordering of values by, for example, {@link #sortedValues(Comparator)} will be retained.
     * 
     * @return the new ordered view
     */
    MultimapView<K, V> sortedKeys();

    /**
     * Equivalent to {@link #sortedKeys()} except that the keys will be ordered accordingly to specified comparator.
     * 
     * @param comparator
     *            the comparator used for ordering keys
     * @return the new ordered view
     * @throws NullPointerException
     *             if the specified comparator is null
     */
    MultimapView<K, V> sortedKeys(Comparator<? super K> comparator);

    /**
     * Equivalent to {@link #sortedKeys()} except that the keys will be ordered accordingly to the keys <b>reverse</b>
     * natural order.
     * 
     * @return the new ordered view
     */
    MultimapView<K, V> sortedKeysDescending();

    /**
     * Assuming all values are Comparable, returns a new view where the values for each key is ordered in accordance
     * with the specified comparator. Furthermore, all values in the view with the same key must be <i>mutually
     * comparable</i> (that is, <tt>v1.compareTo(v2)</tt> must not throw a <tt>ClassCastException</tt> for any elements
     * <tt>v1</tt> and <tt>ev2</tt> in this view).
     * <p>
     * This method does not check that all values are Comparable. However, subsequent invocations on the returned view
     * may fail with {@link ClassCastException} if not all values are Comparable.
     * <p>
     * Unless otherwise specified the ordering is not guaranteed to be <i>stable</i>: equal values might be reordered as
     * a result of the sort. In other words the sort used by this method it not guaranteed to be stable. This is
     * especially true if the implementations uses multiple threads to sort the elements.
     * 
     * @return the new view
     */
    MultimapView<K, V> sortedValues();

    /**
     * Returns a new view where the values for each key is ordered in accordance with the specified comparator. There is
     * no effect on the ordering of keys if {@link #sortedKeys()} or a similar method has invoked previously.
     * <p>
     * Unlike {@link Arrays#sort(Object[])} this method does not guaranteed that equal values maintain their order in
     * the returned view. In other words the sort used by this method it not guaranteed to be stable. This is especially
     * true if multiple threads are used internally.
     * 
     * @param comparator
     *            the comparator used for ordering values
     * @return the new ordered view
     * @throws NullPointerException
     *             if the specified comparator is null
     */
    MultimapView<K, V> sortedValues(Comparator<? super V> comparator);

    /**
     * Equivalent to {@link #sortedValues()} except that the values will be ordered accordingly to their reverse natural
     * order.
     * 
     * @return the new view
     */
    MultimapView<K, V> sortedValuesDescending();

    /**
     * Returns reduction of the values for each key.
     * <p>
     * For example given the following multimap view
     * 
     * <pre>
     * {1 -&gt; 2, 3, 4}, {2 -&gt; 1}, {3 -&gt; 4, 5, 6, 6, 6}
     * </pre>
     * 
     * and a {@link BinaryOperator} that returns the of its two parameters. This method will return a map view
     * containing the following entries: <code>{1 -&gt; 9}, {2 -&gt; 1}, {3 -&gt; 27}</code>
     * 
     * @param reducer
     *            the reducer to apply to all values
     * @return the new map view
     */
    MapView<K, V> reduce(BinaryOperator<V> reducer);

    /**
     * Returns the number of key-value mappings in this view. If the view contains more than <tt>Long.MAX_VALUE</tt>
     * mappings, returns <tt>Long.MAX_VALUE</tt>.
     * 
     * @return the number of key-value mappings in this view
     */
    long size();

    /**
     * Assuming the the values for each key has been ordered, for example, by a call to {@link #sortedValues(Comparator)}
     * . Returns a new map view with the tail value for each key
     * <p>
     * For example given the following ordered multimap view
     * 
     * <pre>
     * {1 -&gt; 2, 3, 4}, {2 -&gt; 1}, {3 -&gt; 4, 5, 6, 6, 6}
     * </pre>
     * 
     * this method will return a map view like this <code>{1 -&gt; 4}, {2 -&gt; 1}, {3 -&gt; 6}</code>
     * <p>
     * If the values in this view has not been ordered invoking this method is equivalent to invoking {@link #any()}.
     * 
     * @return an map view with the tail value for each key
     **/
    MapView<K, V> tail();

    /**
     * Creates a new view which limits the number of keys. For example, invoking this method on the following multimap
     * view where all keys has been ordered:
     * 
     * <pre>
     * {1 -&gt; 2, 3, 4}, {2 -&gt; 1}, {3 -&gt; 4, 5, 6, 6, 6}, {4 -&gt; 1, 7}
     * takeValues(2);
     * will return a view with these mappings {1 -&gt; 2,3, 4}, {2 -&gt; 1}
     * </pre>
     * 
     * @param numberOfKeysToTake
     *            the maximum number of keys in the new view
     * @return the new view
     * @throws IllegalArgumentException
     *             if <tt>numberOfKeysToTake</tt> is 0
     */
    // Rename to takeKeys
    MultimapView<K, V> take(long numberOfKeysToTake);

    /**
     * Creates a new view which limits the number of values for each key. For example, invoking this method on the
     * following multimap view where all values has been ordered:
     * 
     * <pre>
     * {1 -&gt; 2, 3, 4}, {2 -&gt; 1}, {3 -&gt; 4, 5, 6, 6, 6}
     * takeValues(2);
     * will return a view with these mappings {1 -&gt; 2,3}, {2 -&gt; 1}, {3 -&gt; 4, 5}
     * </pre>
     * 
     * @param numberOfValuesToTake
     *            the maximum number of values for each key
     * @return the new view
     * @throws IllegalArgumentException
     *             if <tt>numberOfValuesToTake</tt> is 0
     */
    MultimapView<K, V> takeValues(long numberOfValuesToTake);

    /**
     * Extracts all elements from this view to a data structure of the specified type. All concrete types implementing
     * {@link Multimap} is supported as long as they obey the Multimap contract by having a constructor with a single
     * argument of type <tt>Multimap</tt>, which creates a new map with the same elements as its argument.
     * <p>
     * This method can also take a {@link Map} type in which case the value part will be a {@link Collection},
     * {@link List} or {@link Set} depending on whether or not the individual entries are ordered or unique.
     * <p>
     * The returned data structure will be "safe" in that no references to it or any of its elements are maintained by
     * this view.
     * 
     * @param <T>
     *            the type to convert this view to
     * @param type
     *            the type to convert this view to
     * 
     * @return the contents of this view as the specified type
     * @throws IllegalArgumentException
     *             if the contents of this view cannot be converted to the specified type
     * @throws NullPointerException
     *             if the specified type is null
     */
    <T> T to(Class<T> type);

    /**
     * Creates a new {@link Multimap} with all the mappings in this view.
     * <p>
     * If this view is ordered, the returned map will maintain this ordering when invoking any of its iterators.
     * <p>
     * The returned multimap will be "safe" in that no references to it or any of its elements are maintained by this
     * view. (In other words, this method must allocate a new multimap even if this view is somehow backed by a
     * multimap).
     * <p>
     * Invoking this method id equivalent to invoking <code>to(MultiMap.class)</code>.
     * 
     * @return the new multimap
     */
    Multimap<K, V> toMultimap();

    /**
     * Returns a new view retaining only the unique values for each key in this view. This operation is roughly equal to
     * that of for each key in the view taking all values for a single key and putting them into a {@link HashSet}. And
     * then create a new view based on the remaining elements in the hash set. For example, invoking this method on the
     * following mapping:
     * 
     * <pre>
     * {1 -&gt; 2, 3, null, 4, 4, }, {2 -&gt; 1, 1, null, null}
     * will return this new multimap view {1 -&gt; 2, 3, null, 4}, {2 -&gt; 1, null}
     * </pre>
     * <p>
     * The uniqueness property of an element is, unless otherwise specified, based on the behavior of the
     * {@link Object#equals(Object)} and {@link Object#hashCode()} operation for each element.
     * <p>
     * Implementations may, but are not required to, preserve any previous ordering made to this view when invoking this
     * method.
     * 
     * @return a new view where all values for each key is unique
     * @throws UnsupportedOperationException
     *             if the unique operation is not supported.
     */
    // rename to distinctValues
    MultimapView<K, V> unique();

    /**
     * Creates a new {@link CollectionView} with all the values for each mapping in this view. The elements in the
     * returned collection view will ignore any ordering of this view. For example, invoking this method on the
     * following mapping:
     * 
     * <pre>
     * {1 -&gt; 2, 3, null, 4}, {2 -&gt; 1}, {3 -&gt; 4, 5, 6, null}
     * will return a new collection view with the following elements {2, 3, null, 4, 1, 4, 5, 6, null}
     * </pre>
     * 
     * @return the new collection view
     */
    CollectionView<V> values();
}
