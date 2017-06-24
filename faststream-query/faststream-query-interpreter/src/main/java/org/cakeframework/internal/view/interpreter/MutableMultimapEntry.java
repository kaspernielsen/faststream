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
package org.cakeframework.internal.view.interpreter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import org.cakeframework.internal.util.Multimaps;
import org.cakeframework.util.Multimap;

@SuppressWarnings({ "rawtypes", "unchecked" })
class MutableMultimapEntry implements Map.Entry {

    private static final int MAX_SIZE = Integer.MAX_VALUE - 8;

    public Object[] values;

    public Object key;
    public int length;
    public int offset;

    public MutableMultimapEntry(Object key, Object[] values) {
        this.key = key;
        this.values = values;
        this.length = values.length;
        this.offset = 0;
    }

    public void addValue(Object value) {
        makeRoom(length + 1);
        values[length++] = value;
    }

    private void makeRoom(int minCapacity) {
        if (minCapacity - values.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        // TODO check offsets, maybe we can just copy the array down
        Object[] values = this.values;
        int currentCapacity = values.length;
        int newCapacity = currentCapacity >> 1;
        if (minCapacity < 0 || minCapacity > MAX_SIZE) {
            throw new OutOfMemoryError("Overflow, minCapacity = " + minCapacity);
        } else if (newCapacity < 0 || newCapacity > MAX_SIZE) {
            newCapacity = MAX_SIZE;
        }
        newCapacity = Math.max(minCapacity, newCapacity);
        this.values = Arrays.copyOfRange(values, offset, newCapacity);
        length = size();
        offset = 0;
    }

    public int addValuesTo(Object[] copyTo, int offset) {
        Object[] data = this.values;
        for (int i = this.offset; i < length; i++) {
            copyTo[offset++] = data[i];
        }
        return offset;
    }

    public void filter(BiPredicate filter) {
        int low = offset;
        int high = length;
        Object key = this.key;
        Object[] a = this.values;
        while (low < high && !filter.test(key, a[low])) {
            low++;
        }
        if (low == high /* All false */) {
            this.values = null;// newArray(0);
            this.offset = 0;
            this.length = 0;
            return;
        }
        // [state.low;low[ = false, state[low]=true
        int i = low + 1;

        while (high > i && !filter.test(key, a[high - 1])) {
            high--;
        }
        int h = high - 1;
        if (h == low) {
            this.offset = low;
            this.length = high;
            return;
        }

        // Figure ]low;high[ out
        while (i < h && filter.test(key, a[i])) {
            i++;
        }
        if (i == h) {
            // Figure [low;high] = true
            this.offset = low;
            this.length = high;
            return;
        }
        // Okay here we have filtered empty and element from
        for (int j = i + 1; j < h; j++) {
            Object o = a[j];
            if (filter.test(key, o)) {
                a[i++] = o;
            }
        }
        a[i++] = a[h];
        this.offset = low;
        this.length = i;
    }

    public void filter(Predicate filter) {
        int low = offset;
        int high = length;
        Object[] a = this.values;
        while (low < high && !filter.test(a[low])) {
            low++;
        }
        if (low == high /* All false */) {
            this.values = null;// newArray(0);
            this.offset = 0;
            this.length = 0;
            return;
        }
        // [state.low;low[ = false, state[low]=true
        int i = low + 1;

        while (high > i && !filter.test(a[high - 1])) {
            high--;
        }
        int h = high - 1;
        if (h == low) {
            this.offset = low;
            this.length = high;
            return;
        }

        // Figure ]low;high[ out
        while (i < h && filter.test(a[i])) {
            i++;
        }
        if (i == h) {
            // Figure [low;high] = true
            this.offset = low;
            this.length = high;
            return;
        }
        // Okay here we have filtered empty and element from
        for (int j = i + 1; j < h; j++) {
            Object o = a[j];
            if (filter.test(o)) {
                a[i++] = o;
            }
        }
        a[i++] = a[h];
        this.offset = low;
        this.length = i;
    }

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public Object getValue() {
        return values;
    }

    public boolean isEmpty() {
        return length == offset;// maybe just test if data is null
    }

    public Object reduce(BinaryOperator reducer) {
        Object r = values[offset];
        for (int i = offset + 1; i < length; i++) {
            r = reducer.apply(r, values[i]);
        }
        return r;
    }

    @Override
    public Object setValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return length - offset;
    }

    public void sort() {
        Arrays.sort(values, offset, length);
    }

    public void sort(Comparator comparator) {
        Arrays.sort(values, offset, length, comparator);
    }

    public void sortDescending() {
        Arrays.sort(values, offset, length);
        ArrayUtil.reverse(values, offset, length);
    }

    public void take(long elementsToTake) {
        if (length != offset) {
            if (elementsToTake > 0) {
                if (elementsToTake < length - offset) {
                    length = offset + (int) elementsToTake;
                }
            } else if (elementsToTake > offset - length) {
                offset = length + (int) elementsToTake;
            }
        }
    }

    public void unique() {
        int size = size();
        switch (size) {
        case 1:
            return;
        case 2:
            Object a = values[offset];
            Object b = values[offset + 1];
            if ((a == b) || (a != null && b != null && a.hashCode() == b.hashCode() && a.equals(b))) {
                values[offset + 1] = null;
                length--;
            }
        default:
            HashSet hs = new HashSet();// awawaw
            for (int i = offset; i < length; i++) {
                hs.add(values[i]);
            }
            if (hs.size() != size) {
                values = hs.toArray();
                offset = 0;
                length = values.length;
            }
        }
    }

    public String valuesToString() {
        StringBuilder b = new StringBuilder();
        b.append('[').append(values[offset]);
        for (int i = offset + 1; i < length; i++) {
            b.append(", ").append(values[i]);
        }
        return b.append(']').toString();
    }

    public static Multimap from(MutableMultimapEntry[] entries, int start, int stop) {
        Multimap map = Multimaps.newOrderedListMultimap();
        for (int i = start; i < stop; i++) {
            MutableMultimapEntry e = entries[i];
            Object key = e.key;
            Object[] data = e.values;
            int elength = e.length;
            for (int j = e.offset; j < elength; j++) {
                map.put(key, data[j]);
            }
        }
        return map;
    }
}
