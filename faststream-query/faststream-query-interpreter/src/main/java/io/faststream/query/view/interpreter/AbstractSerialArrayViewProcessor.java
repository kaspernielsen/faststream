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
import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

import io.faststream.query.db.query.node.EmptyResult;

@SuppressWarnings({ "rawtypes", "unchecked" })
abstract class AbstractSerialArrayViewProcessor<T> extends AbstractSingleNodeViewQueryProcessor {

    T[] a;

    int hi;

    boolean isSafeArray;

    int lo;

    /** A runtime setting used in tests to make sure we generate the same random numbers every time. */
    public static final boolean IS_DETERMINISTIC;

    static {
        IS_DETERMINISTIC = Boolean.parseBoolean(System.getProperty("deterministic", "false"));
    }

    // Do we want to initialize if no t used???
    final Random r = IS_DETERMINISTIC ? new Random(123123) : new Random();

    AbstractSerialArrayViewProcessor(T[] a, boolean isSafeArray) {
        this.a = a;
        this.lo = 0;
        this.hi = a.length;
        this.isSafeArray = isSafeArray;
    }

    void map(Function<? super Object, ?> mapper) {
        if (hasResult()) {
            Object result = getResult();
            if (result != EmptyResult.EMPTY_RESULT) {
                setResult(mapper.apply(result));
            }
            return;
        }
        checkSafe();
        Object[] a = this.a;
        int lo = this.lo;
        int i = lo;
        int hi = this.hi;
        try {
            while (i < hi) {
                a[i] = mapper.apply(a[i++]);
            }
        } finally {
            // stats.readObjectArray(i - lo);
            // stats.op(mapper, i - lo);
        }
    }

    void any() {
        int lo = this.lo;
        if (hi == lo) {
            setResult(EmptyResult.EMPTY_RESULT);
        } else {
            setResult(a[lo]);
        }
    }

    protected final void checkSafe() {
        if (!isSafeArray) {
            a = Arrays.copyOfRange(a, lo, hi);
            lo = 0;
            hi = a.length;
            isSafeArray = true;
        }
    }

    void filter(Predicate filter) {
        int low = lo;
        int high = hi;
        Object[] a = this.a;
        while (low < high && !filter.test(a[low])) {
            low++;
        }
        if (low == high /* All false */) {
            this.a = newArray(0);
            this.lo = 0;
            this.hi = 0;
            return;
        }
        // [state.low;low[ = false, state[low]=true
        int i = low + 1;

        while (high > i && !filter.test(a[high - 1])) {
            high--;
        }
        int h = high - 1;
        if (h == low) {
            lo = low;
            hi = high;
            return;
        }

        // Figure ]low;high[ out
        while (i < h && filter.test(a[i])) {
            i++;
        }
        if (i == h) {
            // Figure [low;high] = true
            lo = low;
            hi = high;
            return;
        }
        // Okay here we have filtered empty and element from
        if (!isSafeArray) {
            Object[] newdata = newArray(h - low);
            int k = 0;
            for (int j = low; j < i; j++) {
                newdata[k++] = a[j];
            }
            for (int j = i + 1; j < h; j++) {
                Object o = a[j];
                if (filter.test(o)) {
                    newdata[k++] = o;
                }
            }

            newdata[k++] = a[h];
            lo = 0;
            hi = k;
            this.a = (T[]) newdata;
            isSafeArray = true;
        } else {
            for (int j = i + 1; j < h; j++) {
                Object o = a[j];
                if (filter.test(o)) {
                    a[i++] = o;
                }
            }
            a[i++] = a[h];
            lo = low;
            hi = i;
        }
    }

    protected void head() {
        any();
    }

    void isEmpty() {
        setResult(hi == lo);
    }

    T[] newArray(int size) {
        return (T[]) new Object[size];
    }

    void one() {
        int lo = this.lo;
        int hi = this.hi;
        if (hi == lo + 1) {
            setResult(a[lo]);
        } else if (hi == lo) {
            setResult(EmptyResult.EMPTY_RESULT);
        } else {
            // Todo list some elements
            throw new IllegalStateException(ErrorMessages.MORE_THAN_ONE_ELEMENT_VIEW_ERROR_MESSAGE);
        }
    }

    void sort(Comparator comparator) {
        checkSafe();
        if (comparator == Comparator.naturalOrder()) {
            Arrays.sort(a, lo, hi);
        } else if (comparator == Comparator.reverseOrder()) {
            ArrayUtil.sortDescending(a, lo, hi);
        } else {
            Arrays.sort(a, lo, hi, comparator);
        }
    }

    void reverse() {
        checkSafe();
        ArrayUtil.reverse(a, lo, hi);// int operationsCount = ((hi - lo) >> 1) << 1;
    }

    void shuffle() {
        checkSafe();
        ArrayUtil.shuffle(a, lo, hi, r); // int operationsCount = (hi - lo - 1) >> 1;
    }

    void size() {
        setResult((long) (hi - lo));
    }

    void tail() {
        int hi = this.hi;
        if (hi == lo) {
            setResult(EmptyResult.EMPTY_RESULT);
        } else {
            setResult(a[hi - 1]);
        }
    }

    void take(long numberOfElements) {
        if (hi != lo) {
            if (numberOfElements > 0) {
                if (numberOfElements < hi - lo) {
                    hi = lo + (int) numberOfElements;
                }
            } else if (numberOfElements > lo - hi) {
                lo = hi + (int) numberOfElements;
            }
        }
    }
}
