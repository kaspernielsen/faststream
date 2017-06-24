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
package org.cakeframework.util.view;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * A read-only (virtual) view of a group of objects, known as its elements. A collection view can contain duplicate
 * elements.
 *
 * @author Kasper Nielsen
 * @param <E>
 *            the type of elements
 */
public interface CollectionView<E> {

    /**
     * Returns any element from this view, or throws an {@link IllegalStateException} if the view contains no elements.
     * <p>
     * The element returned by this method is <tt>not</tt> guaranteed to be a random selection among all elements in the
     * view. For example, if the underlying data structure is an array most implementations will just return the first
     * element in the array.
     * <p>
     * If you need a random element from the view you can shuffle the view first and then take any element
     * <tt>view.shuffle().any()</tt>. Most implementations will not shuffle all elements but just select an element at
     * random. See also {@link #shuffle()}.
     *
     * @return any element from this view
     * @throws NoSuchElementException
     *             if the view contains no elements
     * @see #any(Object)
     **/
    E any();

    /**
     * Equivalent to {@link #any()} except that if the view contains no elements the specified base will be returned
     * instead of throwing a {@link NoSuchElementException}.
     *
     * @param base
     *            the result to return for a view containing no elements
     * @return an element from this view, or the specified base if this view contains no elements.
     * @see #any()
     */
    E any(E base);

    /**
     * Applies the specified procedure to all elements in this view.
     * <p>
     * Any implementation of this method is allow to process the elements in any order it chooses. Even if the elements
     * in the view has been ordered, for example, by calling {@link #sorted()}. Requiring an implementation to process
     * them in any specific order would prohibit an implementation where elements can be processed in parallel using
     * multiple threads.
     *
     * @param action
     *            the action to be performed for each element in the view
     * @throws NullPointerException
     *             if the specified procedure is {@code null}
     */
    void forEach(Consumer<? super E> action);

    /**
     * Returns a stream with the same properties as this view.
     *
     * @return a stream with the same properties as this view
     */
    Stream<E> asStream();

    default IntStream asIntStream() {
        throw new UnsupportedOperationException();
    }

    default DoubleStream asDoubleStream() {
        throw new UnsupportedOperationException();
    }

    default LongStream asLongStream() {
        throw new UnsupportedOperationException();
    }

    /**
     * Count the number of occurrences of each unique element in this view.
     * <p>
     * Usage example: Finding the 10 strings that occur most frequently in a view containing only Strings.
     *
     * <pre>
     * CollectionView&lt;String&gt; v =;
     * Map&lt;String, Long&gt; m = v.count().orderDescending().take(10).toMap();
     * </pre>
     * <p>
     * The equality property of two element is, unless otherwise specified, based on the behavior of the
     * {@link Object#equals(Object)} and {@link Object#hashCode()} operation for each element.
     * <p>
     * Implementations may, but are not required to preserve any ordering of elements in this view.
     *
     * @return a new map view where each unique element is mapped to the number of times it occurs in this view
     */
    MapView<E, Long> count();

    /**
     * Returns a new view retaining only those elements that are accepted by the specified filter.
     * <p>
     * Usage: Given a String view, the following example will create a new view that contains only those strings that
     * contains the sequence <tt>"pickme"</tt>
     *
     * <pre>
     * CollectionView&lt;String&gt; view = some view;
     * view.filterOn(StringOps.contains(&quot;pickme&quot;));
     * </pre>
     * <p>
     * Implementations may, but are not required to preserve the order of the elements in this view.
     *
     * @param filter
     *            the filter used to evaluate whether an element should be included in the new view
     * @return a new view where all elements are accepted by the specified filter
     * @throws NullPointerException
     *             if the specified filter is {@code null}
     */
    CollectionView<E> filter(Predicate<? super E> filter);

    /**
     * Returns a view retaining all <tt>non-null</tt> elements from this view.
     * <p>
     * If the underlying data structure is known to contain only <tt>non-null</tt> elements the view is allowed to
     * return <tt>this</tt>.
     * <p>
     * Implementations may, but are not required to preserve the order of the elements in this view.
     *
     * @return a view retaining all <tt>non-null</tt> elements from this view
     */
    CollectionView<E> filterNulls();

    /**
     * Returns a new view retaining only the elements of the specified type.
     * <p>
     * Usage: Given a {@link Number} view the following example will create a new view that contains only those numbers
     * that are floats:
     *
     * <pre>
     * CollectionView&lt;Number&gt; numbers = null;
     * CollectionView&lt;Float&gt; floats = numbers.filterOnType(Float.class);
     * </pre>
     * <p>
     * If the underlying data structure is known to contain only elements of the specified type the view is allowed to
     * return <tt>this</tt>.
     * <p>
     * Implementations may, but are not required to preserve the order of the elements in this view.
     *
     * @param <U>
     *            the type of elements in the new view
     * @param clazz
     *            the type of elements that should be retained in the new view
     * @return a new view retaining only those elements of the specified type
     * @throws NullPointerException
     *             if the specified class is {@code null}
     */
    <U> CollectionView<U> filterOnType(Class<U> clazz);

    /**
     * This method is primary used instead of {@link #reduce(BinaryOperator)} to avoid excessive object creation when
     * reducing views with many elements. Consider, for example, having to compute the sum from a view with millions of
     * Complex elements:
     *
     * <pre>
     * class Complex {
     *     double real, imaginary;
     * }
     * </pre>
     *
     * One way to do it would be to use a reducer ({@link #reduce(BinaryOperator)}) like this:
     *
     * <pre>
     * class ExpensiveComplexReducer implements Reducer&lt;Complex&gt; {
     *     public Complex op(Complex a, Complex b) {
     *         return new Complex(a.real + b.real, a.imaginary + b.imaginary); // creates a new Complex instance
     *     }
     * }
     * </pre>
     *
     * However, this would create as many new Complex instances as there are elements in the view. Which in many cases
     * is sub optimal even with the performance of modern garbage collectors.
     * <p>
     * A better approach is to first use this <tt>gather</tt> method for creating a small number of temporary state
     * objects. And then apply {@link #reduce(BinaryOperator)} afterwards on the temporary state objects to get the
     * final result. For the above example we will first create a class that process Complex objects and holds a
     * temporary sum:
     *
     * <pre>
     * class ComplexAddWithTemporaryState implements Procedure&lt;Complex&gt; {
     *     double real, imaginary; // no object creation
     *
     *     public void apply(Complex c) {
     *         real += c.real;
     *         imaginary += c.imaginary;
     *     }
     * }
     * </pre>
     *
     * To create a view of temporary view that holds all instances of ComplexAddTemporaryState we call <tt>gather</tt>
     * with a generator that creates the temporary state objects.
     *
     * <pre>
     * CollectionView&lt;ComplexAddWithTemporaryState&gt; tmp = view.gather(new Generator&lt;ComplexAddWithTemporaryState&gt;() {
     *     public ComplexAddWithTemporaryState next() {
     *         return new ComplexAddWithTemporaryState();
     *     }
     * });
     * </pre>
     *
     * Implementations of this view are free to use the specified generator to create as many temporary state objects as
     * needed. As long as each instance is used by one and only one thread at a time. Each temporary state object that
     * is created by the specified generator will then process a unique subset of all the elements in this view.
     * <p>
     * Finally we use {@link #reduce(BinaryOperator)} to reduce all the temporary state objects that have been created:
     *
     * <pre>
     * ComplexAdder result = tmp.reduce(new Reducer&lt;ComplexAddTemporaryState&gt;() {
     *     public ComplexAddTemporaryState reduce(ComplexAddTemporaryState a, ComplexAddTemporaryState b) {
     *         a.real += b.real;
     *         a.imaginary += b.imaginary;
     *         return a;
     *     }
     * });
     * Complex sum = new Complex(result.real, result.c.imaginary);
     * </pre>
     * <p>
     * Any implementation of this method is allow to process the elements in any order it chooses. Even if the elements
     * in the view has been ordered, for example, by calling {@link #sorted()}. Requiring an implementation to process
     * them in any specific order would prohibit an implementation where elements can be processed in parallel using
     * multiple threads.
     * <p>
     * An implementation may choose not to create any temporary state objects if applied on an empty view.
     *
     * @param <T>
     *            the type of temporary state objects
     * @param gatherer
     *            the generator that generates temporary state objects
     * @return a new view with all the temporary objects that have been generated
     * @throws NullPointerException
     *             if the specified generator is {@code null}
     */
    <T extends Consumer<E>> CollectionView<T> gather(Supplier<T> gatherer);

    /**
     * Groups all elements in the view according to the specified mapper.
     *
     * List of employees grouped by the year in which they were born TODO
     *
     * @param <K>
     *            The type of keys to map to
     * @param mapper
     *            the function that maps each element
     * @return a view where all elements in this view is grouped according to the specified mapper
     * @throws NullPointerException
     *             if the specified mapper is {@code null}
     */
    <K> MultimapView<K, E> groupBy(Function<? super E, ? extends K> mapper);

    /**
     * Returns the head of this view if it is ordered, otherwise {@link #any() any} element. If the view is empty throws
     * {@link NoSuchElementException}.
     * <p>
     * A view can be ordered, for example, by calls to {@link #sorted(Comparator)}.
     *
     * @return the first element if this view is ordered, otherwise any element
     * @throws NoSuchElementException
     *             if the view contains no elements
     * @see #first(Object)
     * @see #last()
     */
    E first();

    /**
     * Equivalent to {@link #first()} except that if the view contains no elements the specified base will be returned
     * instead of this method throwing {@link NoSuchElementException}.
     *
     * @param base
     *            the result to return for a view containing no elements
     * @return the first element from this view, or the specified base if this view contains no elements.
     * @see #first()
     * @see #last(Object)
     */
    E first(E base);

    /**
     * Returns <tt>true</tt> if this view contains no elements, otherwise <tt>false</tt>.
     *
     * @return <tt>true</tt> if this view contains no elements, otherwise <tt>false</tt>
     */
    boolean isEmpty();

    /**
     * Returns a new view where all elements of this view is mapped using the specified mapper.
     * <p>
     * Suppose <tt>v</tt> is a <tt>CollectionView</tt> known to contain only strings. The following code creates a new
     * view that contains the upper cased version of each string:
     *
     * <pre>
     * CollectionView&lt;String&gt; x = v.map(new Mapper&lt;String, String&gt;() {
     *     public String map(String a) {
     *         return a.toUpperCase();
     *     }
     * });
     * </pre>
     * <p>
     * If this view is ordered, for example by calls to {@link #sorted()}, the returned view will preserve the ordering.
     *
     * @param <T>
     *            the type of elements to map to
     * @param mapper
     *            the mapper
     * @return the new mapped view
     * @throws NullPointerException
     *             if the specified mapper is null
     */
    <T> CollectionView<T> map(Function<? super E, ? extends T> mapper);

    /**
     * Maps all elements in this view to a unique number in the range of <tt>0</tt> to <tt>size()-1</tt>. If this view
     * is ordered the returned map view will map all elements to their relative order. Mapping <tt>0</tt> to
     * {@link #first()} and <tt>size-1</tt> to {@link #last()}.
     * <p>
     * If this view is not ordered an implementation is free to choose any order.
     *
     * @return a new map view mapping all elements to a unique number
     */
    // Todo if unordered, is result view also onordered then?
    MapView<Long, E> mapToIndex();

    /**
     * Returns the single element contained in this view. Otherwise if there are zero or more than 1 element throws an
     * {@link NoSuchElementException} or {@link IllegalStateException} respectively.
     * <p>
     * This is equivalent to;
     *
     * <pre>
     * if (view.size() == 1) {
     *     throw new NoSuchElementException(&quot;view is empty&quot;);
     * } else if (view.size &gt; 1) {
     *     throw new IllegalArgumentException(&quot;view does not contain exactly 1 element&quot;);
     * }
     * return view.any();
     * </pre>
     *
     * only atomically.
     * <p>
     * In many situations this operation is more expensive then the {@link #any()} operation. Consider, for example, a
     * view that was created as the result of applying a filter on a list. The filter is constructed in such a way that
     * only the first element passed to the filter is accepted. The {@link #any()} will return almost immediately
     * because the filter accepts the first element. However, the <tt>one</tt> operation will have to evaluate all
     * elements using the filter to make absolutely sure that there is one and only one element accepted by the filter.
     *
     * @throws IllegalStateException
     *             if the view does not contain exactly one element
     * @return the only element in this view
     * @throws NoSuchElementException
     *             if the view contains no elements
     * @throws IllegalStateException
     *             if the view contains more than 1 element
     * @see #one(Object)
     */
    E one();

    /**
     * Equivalent to {@link #one()} except that if the view contains no elements the specified base will be returned
     * instead of this method throwing {@link NoSuchElementException}.
     *
     * @param base
     *            the result to return for a view containing no elements
     * @return the only element in this view, or the specified base if this view contains no elements.
     * @throws IllegalStateException
     *             if the view contains more than 1 element
     * @see #one()
     */
    E one(E base);

    /**
     * Analogous to {@link #reduce(Object,BinaryOperator)} except that it will throw an {@link IllegalStateException} if
     * this view contains no elements.
     *
     * @param reducer
     *            the reducer
     * @return reduction of elements in this view
     * @throws IllegalStateException
     *             if this view is empty
     * @throws NullPointerException
     *             if the specified reducer is {@code null}
     * @see #reduce(Object, BinaryOperator)
     */
    E reduce(BinaryOperator<E> reducer);

    /**
     * Returns reduction of elements in this view.
     * <p>
     * Usage: Considering a view of {@link BigDecimal}, the following example computes the sum of all numbers in the
     * view, or returns <tt>BigDecimal.ZERO</tt> if the view is empty:
     *
     * <pre>
     * CollectionView&lt;BigDecimal&gt; numbers = ...;
     * BigDecimal sum = numbers.reduce(new Reducer&lt;BigDecimal&gt;() {
     *     public BigDecimal op(BigDecimal a, BigDecimal b) {
     *         return a.add(b);
     *     }
     * }, BigDecimal.ZERO);
     * </pre>
     * <p>
     * Any implementation of this method is allow to process the elements in any order it chooses. Even if the elements
     * in the view has been ordered, for example, by calling {@link #sorted()}. Requiring an implementation to process
     * them in any specific order would prohibit an implementation where elements can be processed in parallel using
     * multiple threads.
     *
     * @param reducer
     *            the reducer
     * @param base
     *            the result for an empty view
     * @return reduction of elements in this view, or the specified base if the view contains no elements
     * @throws NullPointerException
     *             if the specified reducer is null
     * @see #reduce(BinaryOperator)
     * @see Stream#reduce(Object, BinaryOperator)
     */
    E reduce(E base, BinaryOperator<E> reducer);

    /**
     * Returns a new view where the order of all elements in this view are reversed.
     * <p>
     * If the elements in this view are not ordered, this view can be returned.
     *
     * @return a new collection view with the position of all elements reversed
     */
    CollectionView<E> reverse();

    /**
     * Returns a new view containing a random permutation of the elements in this view using a built-in source of
     * randomness. All permutations occur with approximately equal likelihood.
     * <p>
     * To select one random element from a {@link CollectionView} you would use:
     *
     * <pre>
     * CollectionView&lt;E&gt; view = ...
     * E randomElement = view.shuffle().any();
     * </pre>
     *
     * NOTE: Shuffling can often be an expensive operation. Even if only <tt>one</tt> random element is selected. For
     * example, it is not possible to select a single random element from a chained hash map with n elements in less
     * then O(n) time.
     * <p>
     * To select a random sampling with <tt>100</tt> elements you would use:
     *
     * <pre>
     * CollectionView&lt;E&gt; view = ...
     * List&lt;E&gt; randomSampling = view.shuffle().take(100).toList();
     * </pre>
     *
     * <p>
     * The hedge "approximately" is used in the foregoing description because built-in source of randomness is only
     * approximately an unbiased source of independently chosen bits. If it were a perfect source of randomly chosen
     * bits, then the algorithm would choose permutations with perfect uniformity.
     *
     * @return a new shuffled view
     * @throws UnsupportedOperationException
     *             if the shuffle operation is not supported.
     */
    CollectionView<E> shuffle();

    /**
     * Returns the number of elements in this view. If the view contains more than <tt>Long.MAX_VALUE</tt> elements,
     * returns <tt>Long.MAX_VALUE</tt>.
     *
     * @return the number of elements in this view
     */
    long size();

    /**
     * Assuming all elements are Comparable, returns a new view where all elements are ordered accordingly to their
     * natural order. Furthermore, all elements in the view must be <i>mutually comparable</i> (that is,
     * <tt>e1.compareTo(e2)</tt> must not throw a <tt>ClassCastException</tt> for any elements <tt>e1</tt> and
     * <tt>e2</tt> in this view).
     * <p>
     * This method does not check that all elements are Comparable. However, subsequent invocations on the returned view
     * may fail with {@link ClassCastException} if not all elements are Comparable.
     * <p>
     * Unless otherwise specified the ordering is not guaranteed to be <i>stable</i>: equal elements might be reordered
     * as a result of the sort. In other words the sort used by this method it not guaranteed to be stable. This is
     * especially true if the implementations uses multiple threads to sort the elements.
     *
     * @return the new ordered view
     * @see #sorted(Comparator)
     * @see #sortedDescending()
     * @see Stream#sorted()
     */
    CollectionView<E> sorted();

    /**
     * Returns a new view where all elements are ordered in accordance with the specified comparator.
     * <p>
     * Unlike {@link Arrays#sort(Object[])} this method does not guaranteed that equal elements maintain their order in
     * the returned view. In other words the sort used by this method it not guaranteed to be stable. This is especially
     * true if multiple threads are used internally.
     *
     * @param comparator
     *            the comparator used for ordering elements
     * @throws NullPointerException
     *             if the specified comparator is null
     * @return the new ordered view
     * @see #sorted()
     * @see #sortedDescending()
     */
    CollectionView<E> sorted(Comparator<? super E> comparator);

    /**
     * Assuming all elements are Comparable, returns a new view where all elements are ordered accordingly to their
     * reverse natural order. Furthermore, all elements in the view must be <i>mutually comparable</i> (that is,
     * <tt>e1.compareTo(e2)</tt> must not throw a <tt>ClassCastException</tt> for any elements <tt>e1</tt> and
     * <tt>e2</tt> in the view).This ordering does not guarantee that elements with equal keys maintain their relative
     * position.
     * <p>
     * Invoking this method is equivalent to invoking <tt>view.order().reverse()</tt>
     * <p>
     * This method does not check that all elements are Comparable. However, subsequent invocations on the returned view
     * may fail with {@link ClassCastException} if not all elements are Comparable.
     * <p>
     * Unless otherwise specified the ordering is not guaranteed to be <i>stable</i>: equal elements might be reordered
     * as a result of the sort. In other words the sort used by this method it not guaranteed to be stable. This is
     * especially true if the implementations uses multiple threads to sort the elements.
     *
     * @return the new view
     * @see #sorted()
     * @see #sorted(Comparator)
     */
    CollectionView<E> sortedDescending();

    /**
     * Returns the tail of this view if it is ordered, otherwise {@link #any() any} element. If the view is empty throws
     * {@link NoSuchElementException}.
     * <p>
     * A view can be ordered, for example, by calls to {@link #sorted(Comparator)}.
     *
     * @return the last element if this view is ordered, otherwise any element
     * @throws NoSuchElementException
     *             if the view contains no elements
     * @see #last(Object)
     * @see #first()
     */
    E last();

    /**
     * Equivalent to {@link #last()} except that if the view contains no elements the specified base will be returned
     * instead of this method throwing {@link NoSuchElementException}.
     *
     * @param base
     *            the result to return for a view containing no elements
     * @return the last element from this view, or the specified base if this view contains no elements.
     * @see #last()
     * @see #first(Object)
     */
    E last(E base);

    /**
     * Returns a new view retaining no more then the specified amount of elements.
     * <p>
     * If the elements in this view has been ordered, the elements retained in the new view will be the top
     * <tt>numberOfElementsToTake</tt> number of elements if <tt>numberOfElementsToTake</tt> is positive. Or the bottom
     * <tt>numberOfElementsToTake</tt> number of elements if <tt>numberOfElementsToTake</tt> is negative. The new view
     * will retain any order among the "taken" elements.
     * <p>
     * If this view has not been ordered the new view is free to select the elements in any way possible.
     * <p>
     * Usage: Returning a list of the 4 highest numbers in a view containing <tt>1, 2, 3, 4, 5, 6, 7, 8, 9, 10</tt>:
     *
     * <pre>
     * CollectionView&lt;Integer&gt; v = ...
     * List&lt;Integer&gt; list = v.orderDescending().setLimit(4).toList();//[10, 9, 8, 7]
     * </pre>
     *
     * Usage: Returning a list of the 4 lowest numbers in the view:
     *
     * <pre>
     * CollectionView&lt;Integer&gt; v = ...
     * List&lt;Integer&gt; list = v.orderDescending().setLimit(-4).toList();//[4, 3, 2, 1]
     * </pre>
     *
     * Notice that [4, 3, 2, 1] is returned and not [1, 2, 3, 4]. If you want the last list, call {@link #reverse()}
     * before creating the list.
     * <p>
     * NOTE: If the view is not ordered repeated calls to this method might return different results.
     *
     * @param numberOfElementsToTake
     *            the number of elements in the new view. If there are less then the specified
     *            <tt>numberOfElementsToTake</tt> of elements in the view. The new view will retain all elements in this
     *            view
     * @return the new view
     * @throws IllegalArgumentException
     *             if <tt>numberOfElementsToTake</tt> is 0
     */
    CollectionView<E> take(long numberOfElementsToTake);

    /**
     * Extracts all elements from this view to a data structure of the specified type. The following types are currently
     * supported: TODO
     *
     * All concrete types implementing {@link Collection} is supported as long as they obey the Collection contract by
     * having a constructor with a single argument of type <tt>Collection</tt>, which creates a new collection with the
     * same elements as its argument.
     * <p>
     * An array class can also be specified in which case, assuming all elements are of the array type, an array of the
     * specified type and <tt>length = view.size()</tt>. The array will be populated with all the elements in this view
     * <p>
     * Any interface from java.util or java.util.concurrent that extends {@link Collection} can also be specified. In
     * which case the view decides which particular implementation to use. Implementations should prefer returning
     * memory-compact and immutable versions of the interface.
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
     * @throws ArrayStoreException
     *             if the specified type is an array type that is not compatible with the elements in this view
     * @throws IllegalStateException
     *             if this view contains more then {@link Integer#MAX_VALUE} elements and the specified type does not
     *             support more then {@link Integer#MAX_VALUE} elements, for example, ArrayList
     * @throws NullPointerException
     *             if the specified type is null
     * @see #toList()
     */
    <T> T to(Class<T> type);

    /**
     * Creates a new {@link List} with all the elements in this view. If this view is ordered, the returned list will
     * maintain this ordering. Returning {@link #first()} as element with <tt>index 0</tt> in the list and
     * {@link #last()} as element with <tt>index size-1</tt> in the list.
     * <p>
     * The returned list will be "safe" in that no references to it or any of its elements are maintained by this view.
     * (In other words, this method must allocate a new list even if this view is somehow backed by a list).
     * <p>
     * Calling this method is equivalent to calling
     *
     * <pre>
     * List&lt;V&gt; all = (List&lt;V&gt;) to(List.class);
     * </pre>
     * <p>
     *
     * @return a list containing all the elements in this view
     * @throws IllegalStateException
     *             if this view contains more then {@link Integer#MAX_VALUE} elements
     * @see #to(Class)
     */
    List<E> toList();

    /**
     * Returns a new view retaining only unique elements from this view. This operation is roughly equal to that of
     * putting all elements in this view into a {@link Set} and then create a new view based on the remaining elements
     * in the set.
     * <p>
     * The uniqueness property of an element is, unless otherwise specified, based on the behavior of the
     * {@link Object#equals(Object)} and {@link Object#hashCode()} operation for each element.
     * <p>
     * Implementations may, but are not required to preserve the order of individual elements when invoking this method.
     *
     * @return a new view where all elements are unique
     */
    CollectionView<E> distinct();
}
