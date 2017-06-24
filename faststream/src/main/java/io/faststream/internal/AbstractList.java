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
package io.faststream.internal;

import java.util.Arrays;

/**
 *
 * @author Kasper Nielsen
 */
public abstract class AbstractList<E> extends java.util.AbstractList<E> {

    /**
     * The maximum size of array to allocate. Some VMs reserve some header words in an array. Attempts to allocate
     * larger arrays may result in OutOfMemoryError: Requested array size exceeds VM limit
     */
    static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    static final String oomeMsg = "Required array size too large";

    protected int checkInitialCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Initial capacity must be a non-negative number, was: " + capacity);
        }
        return capacity;
    }

    /**
     * Increases the capacity to ensure that it can hold at least the number of elements specified by the minimum
     * capacity argument.
     *
     * @param minCapacity
     *            the desired minimum capacity
     */
    protected Object[] grow(Object[] elementData, int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = hugeCapacity(minCapacity);
        }
        return elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) {
            throw new OutOfMemoryError(oomeMsg);
        }
        return minCapacity > MAX_ARRAY_SIZE ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }
}

// public final Object[] toArray() {
// long sz = map.mappingCount();
// if (sz > MAX_ARRAY_SIZE)
// throw new OutOfMemoryError(oomeMsg);
// int n = (int)sz;
// Object[] r = new Object[n];
// int i = 0;
// for (E e : this) {
// if (i == n) {
// if (n >= MAX_ARRAY_SIZE)
// throw new OutOfMemoryError(oomeMsg);
// if (n >= MAX_ARRAY_SIZE - (MAX_ARRAY_SIZE >>> 1) - 1)
// n = MAX_ARRAY_SIZE;
// else
// n += (n >>> 1) + 1;
// r = Arrays.copyOf(r, n);
// }
// r[i++] = e;
// }
// return (i == n) ? r : Arrays.copyOf(r, i);
// }
