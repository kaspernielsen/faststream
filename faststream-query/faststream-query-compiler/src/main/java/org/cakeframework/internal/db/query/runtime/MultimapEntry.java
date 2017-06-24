package org.cakeframework.internal.db.query.runtime;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class MultimapEntry implements Map.Entry {

    public Object[] data;

    public Object key;
    public int length;
    public int offset;

    public MultimapEntry(Object key, Object[] data) {
        this.key = key;
        this.data = data;
        this.length = data.length;
        this.offset = 0;
    }

    public boolean isEmpty() {
        return length == offset;// maybe just test if data is null
    }

    public int size() {
        return length - offset;
    }

    public int addValuesTo(Object[] copyTo, int offset) {
        Object[] data = this.data;
        for (int i = this.offset; i < length; i++) {
            copyTo[offset++] = data[i];
        }
        return offset;
    }

    public Object reduce(BinaryOperator reducer) {
        Object r = data[offset];
        for (int i = offset + 1; i < length; i++) {
            r = reducer.apply(r, data[i]);
        }
        return r;
    }

    public void filter(BiPredicate filter) {
        int low = offset;
        int high = length;
        Object key = this.key;
        Object[] a = this.data;
        while (low < high && !filter.test(key, a[low])) {
            low++;
        }
        if (low == high /* All false */) {
            this.data = null;// newArray(0);
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
        Object[] a = this.data;
        while (low < high && !filter.test(a[low])) {
            low++;
        }
        if (low == high /* All false */) {
            this.data = null;// newArray(0);
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
        return data;
    }

    @Override
    public Object setValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public void unique() {
        int size = size();
        switch (size) {
        case 1:
            return;
        case 2:
            Object a = data[offset];
            Object b = data[offset + 1];
            if (eq(a, b)) {
                data[offset + 1] = null;
                length--;
            }
        case 3:
            a = data[offset];
            b = data[offset + 1];
            Object c = data[offset + 2];
            if (eq(a, c) || eq(b, c)) {
                data[offset + 2] = null;
                length--;
                if (eq(a, b)) {
                    data[offset + 1] = null;
                    length--;
                }
            } else if (eq(a, b)) {
                data[offset + 1] = c;
                data[offset + 2] = null;
                length--;
            }
        default:
            HashSet hs = new HashSet();// awawaw
            for (int i = offset; i < length; i++) {
                hs.add(data[i]);
            }
            if (hs.size() != size) {
                data = hs.toArray();
                offset = 0;
                length = data.length;
            }
        }
    }

    private boolean eq(Object a, Object b) {
        return a == b || (a != null && b != null && a.hashCode() == b.hashCode() && a.equals(b));
    }

    public void sort() {
        Arrays.sort(data, offset, length);
    }

    public void sortDescending() {
        Arrays.sort(data, offset, length);
        ArrayUtil.reverse(data, offset, length);
    }

    public void sort(Comparator comparator) {
        Arrays.sort(data, offset, length, comparator);
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

    public String valuesToString() {
        StringBuilder b = new StringBuilder();
        b.append('[').append(data[offset]);
        for (int i = offset + 1; i < length; i++) {
            b.append(", ").append(data[i]);
        }
        return b.append(']').toString();
    }
}
