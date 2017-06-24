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
package io.faststream.query.db.query.runtime;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Kasper Nielsen
 */
public class ArrayUtil {
    /** The largest possible (non-power of two) array size. Needed by toArray and related methods. */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private static final String OUT_OF_MEMORY = "Required array size too large, size=";

    public static double[] copy(double[] n) {
        return Arrays.copyOf(n, n.length);
    }

    public static double[] copy(double[] n, int from, int to) {
        return Arrays.copyOfRange(n, from, to);
    }

    public static long[] copy(long[] n) {
        return Arrays.copyOf(n, n.length);
    }

    public static long[] copy(long[] n, int from, int to) {
        return Arrays.copyOfRange(n, from, to);
    }

    public static int[] copy(int[] n) {
        return Arrays.copyOf(n, n.length);
    }

    public static int[] copy(int[] n, int from, int to) {
        return Arrays.copyOfRange(n, from, to);
    }

    public static Object[] copy(Object[] n) {
        return Arrays.copyOf(n, n.length);
    }

    public static Object[] copy(Object[] n, int from, int to) {
        return Arrays.copyOfRange(n, from, to);
    }

    public static double[] distinct(double[] n) {
        return distinct(n, 0, n.length);
    }

    public static double[] distinct(double[] n, int from, int to) {
        HashSet<Double> i = new HashSet<>();
        for (int j = from; j < to; j++) {
            i.add(n[j]);
        }
        return i.stream().mapToDouble(e -> e).toArray();
    }

    public static int[] distinct(int[] n) {
        return distinct(n, 0, n.length);
    }

    public static int[] distinct(int[] n, int from, int to) {
        HashSet<Integer> i = new HashSet<>();
        for (int j = from; j < to; j++) {
            i.add(n[j]);
        }
        return i.stream().mapToInt(e -> e).toArray();
    }

    public static long[] distinct(long[] n) {
        return distinct(n, 0, n.length);
    }

    public static long[] distinct(long[] n, int from, int to) {
        HashSet<Long> i = new HashSet<>();
        for (int j = from; j < to; j++) {
            i.add(n[j]);
        }
        return i.stream().mapToLong(e -> e).toArray();
    }

    public static Object[] distinct(Object[] n) {
        return distinct(n, 0, n.length);
    }

    public static Object[] distinct(Object[] n, int from, int to) {
        HashSet<Object> i = new HashSet<>();
        for (int j = from; j < to; j++) {
            i.add(n[j]);
        }
        return i.toArray();
    }

    public static int nextArraySize(int n) {
        if (n >= MAX_ARRAY_SIZE) {
            throw new OutOfMemoryError(OUT_OF_MEMORY + n);
        } else if (n >= MAX_ARRAY_SIZE - (MAX_ARRAY_SIZE >>> 1) - 1) {
            return MAX_ARRAY_SIZE;
        }
        return n + (n >>> 1) + 1;
    }

    public static <E> Object[] toArray(long size, Iterable<E> iter) {
        if (size > MAX_ARRAY_SIZE) {
            throw new OutOfMemoryError(OUT_OF_MEMORY + size);
        }
        int n = (int) size;
        Object[] r = new Object[n];

        int i = 0;
        for (E e : iter) {
            if (i == n) {
                r = Arrays.copyOf(r, n = nextArraySize(n));
            }
            r[i++] = e;
        }
        return (i == n) ? r : Arrays.copyOf(r, i);
    }

    public static List<Object> toList(Object[] array) {
        return Arrays.asList(array);
    }

    public static List<Object> toList(Object[] array, int from, int to) {
        if (from == 0 && to == array.length) {
            return Arrays.asList(array);
        }
        return toList(ArrayUtil.copy(array, 0, to));
    }

    public static List<Object> toList(long[] array, int from, int to) {
        Object[] o = new Object[to - from];
        for (int i = from; i < to; i++) {
            o[i] = Long.valueOf(array[i]);
        }
        return toList(o);
    }

    /**
     * @param a
     *            the array to be sorted
     * @param fromIndex
     *            the index of the first element (inclusive) to be sorted
     * @param toIndex
     *            the index of the last element (exclusive) to be sorted
     * @return the specified array
     */
    public static <T> T[] sortDescending(T[] a, int fromIndex, int toIndex) {
        Arrays.sort(a, fromIndex, toIndex);
        toIndex--;
        while (fromIndex < toIndex) {
            T t = a[fromIndex];
            a[fromIndex++] = a[toIndex];
            a[toIndex--] = t;
        }
        return a;
    }

    /**
     * Reverses the specified array in place.
     *
     * @param <T>
     *            the component type of the array
     * @param array
     *            the array to reverse
     * @return the specified array
     */
    public static <T> T[] reverse(T[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * Reverses the specified array in place.
     *
     * @param <T>
     *            the component type of the array
     * @param array
     *            the array to reverse
     * @param lo
     *            the low index (included)
     * @param hi
     *            the high index (excluded)
     * @return the specified array
     */
    public static <T> T[] reverse(T[] array, int lo, int hi) {
        for (int i = lo, j = hi - 1; i < j; i++, j--) {
            T temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return array;
    }

    /**
     * Randomly permute the specified array from using the specified source of randomness. All permutations occur with
     * equal likelihood assuming that the source of randomness is fair.
     * <p>
     * This implementation traverses the array backwards, from the to index up to the from + 1 index, repeatedly
     * swapping a randomly selected element into the "current position". Elements are randomly selected from the portion
     * of the array that runs from the from index to the current position, inclusive.
     * <p>
     * This method runs in linear time.
     *
     * @param a
     *            the array to be shuffled.
     * @param from
     *            the initial index of the range to be shuffled, inclusive
     * @param to
     *            the final index of the range to be shuffled, exclusive.
     * @param rnd
     *            the source of randomness to use to shuffle the list.
     */
    public static <T> T[] shuffle(T[] a, int from, int to, Random rnd) {
        for (int i = to; i > from + 1; i--) {
            swap(a, i - 1, rnd.nextInt(i - from) + from);
        }
        return a;
    }

    /** Swaps the two specified elements in the specified array. */
    static void swap(Object[] a, int i, int j) {
        Object o = a[i];
        a[i] = a[j];
        a[j] = o;
    }

}
