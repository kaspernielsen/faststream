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
package io.faststream.query.view.interpreter;

import java.util.Arrays;
import java.util.Random;

/**
 * Various array utils.
 * 
 * @author Kasper Nielsen
 */
class ArrayUtil {

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
