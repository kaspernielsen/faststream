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
package io.faststream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Factory methods for creating fast stream wrappers from arrays. Also creates array list implementations that return
 * fast streams.
 *
 * @author Kasper Nielsen
 */
@SuppressWarnings("unchecked")
// Let it implement interfaces for example interface MyList extends List
// To avoid virtualization.

public class FastStreams {

    /**
     * Returns a new empty array list with an initial capacity of ten.
     *
     * @return a new empty array list with an initial capacity of ten
     * @see ArrayList#ArrayList()
     * @see ListFactoryBuilder
     */
    public static <T> List<T> newArrayList() {
        return (List<T>) ArrayListHolder.INSTANCE.newArrayList();
    }

    /**
     * Returns a new array list populated with elements from the specified collection.
     *
     * @param c
     *            the collection whose elements wiil be placed into the returned list
     *
     * @return a new array list populated with elements from the specified collection
     * @throws NullPointerException
     *             if the specified collection is null
     * @see ArrayList#ArrayList(Collection)
     * @see ListFactoryBuilder
     */
    @SuppressWarnings("rawtypes")
    public static <T> List<T> newArrayList(Collection<? super T> c) {
        return ArrayListHolder.INSTANCE.newArrayList((Collection) c);
    }

    /**
     * Creates a new array list with the specified initial capacity.
     *
     * @param initialCapacity
     *            the initial capacity of the list
     *
     * @throws IllegalArgumentException
     *             if the specified initial capacity is negative
     * @return a new array list with the specified initial capacity
     * @see ArrayList#ArrayList(int)
     * @see ListFactoryBuilder
     */
    public static <T> List<T> newArrayList(int initialSize) {
        return (List<T>) ArrayListHolder.INSTANCE.newArrayList(initialSize);
    }

    /**
     * Returns a sequential ordered stream which consist of the specified values.
     *
     * @param values
     *            the elements of the new stream
     * @return the new stream
     * @throws NullPointerException
     *             if the specified array is null
     * @see DoubleStream#of(Object...)
     */
    public static DoubleStream ofDouble(double... values) {
        return ArrayHolderOfDouble.INSTANCE.of(values);
    }

    /**
     * Returns a sequential ordered stream which consist of the specified values.
     *
     * @param values
     *            the elements of the new stream
     * @return the new stream
     * @throws NullPointerException
     *             if the specified array is null
     * @see IntStream#of(Object...)
     */
    public static IntStream ofInt(int... values) {
        return ArrayHolderOfInt.INSTANCE.of(values);
    }

    /**
     * Returns a sequential ordered stream which consist of the specified values.
     *
     * @param values
     *            the elements of the new stream
     * @return the new stream
     * @throws NullPointerException
     *             if the specified array is null
     *
     * @see LongStream#of(Object...)
     */
    public static LongStream ofLong(long... values) {
        return ArrayHolderOfLong.INSTANCE.of(values);
    }

    /**
     * Returns a sequential ordered stream which consist of the specified values.
     *
     * @param <T>
     *            the type of elements in the stream
     * @param values
     *            the elements of the new stream
     * @return the new stream
     * @throws NullPointerException
     *             if the specified array is null
     * @see Stream#of(Object...)
     * @see ArrayFactory#of(Object...)
     */
    public static <T> Stream<T> of(T... values) {
        return ArrayHolder.INSTANCE.of(values);
    }

    /** Lazy construction of the default ArrayFactory implementation. */
    static class ArrayHolder {

        /** Default factory of arrays. */
        static final ArrayFactory INSTANCE = new ArrayFactoryBuilder().setClassLoaderParent(FastStreams.class.getClassLoader()).build();
    }

    /** Lazy construction of the default ArrayFactory.OfInt implementation. */
    static class ArrayHolderOfInt {

        /** Default factory of arrays. */
        static final ArrayFactory.OfInt INSTANCE = new ArrayFactoryBuilder().setClassLoaderParent(FastStreams.class.getClassLoader()).buildOfInt();
    }

    /** Lazy construction of the default ArrayFactory.OfInt implementation. */
    static class ArrayHolderOfDouble {

        /** Default factory of arrays. */
        static final ArrayFactory.OfDouble INSTANCE = new ArrayFactoryBuilder().setClassLoaderParent(FastStreams.class.getClassLoader()).buildOfDouble();
    }

    /** Lazy construction of the default ArrayFactory.OfInt implementation. */
    static class ArrayHolderOfLong {

        /** Default factory of arrays. */
        static final ArrayFactory.OfLong INSTANCE = new ArrayFactoryBuilder().setClassLoaderParent(FastStreams.class.getClassLoader()).buildOfLong();
    }

    /** Lazy construction of the default ArrayListFactory implementation. */
    static class ArrayListHolder {

        /** Default factory of array lists. */
        static final ListFactory<Object> INSTANCE = new ListFactoryBuilder().setClassLoaderParent(FastStreams.class.getClassLoader()).build();
    }
}
