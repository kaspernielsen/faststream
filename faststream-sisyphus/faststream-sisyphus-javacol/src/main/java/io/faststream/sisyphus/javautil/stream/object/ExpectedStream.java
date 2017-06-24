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
package io.faststream.sisyphus.javautil.stream.object;

import static io.faststream.sisyphus.util.MoreAsserts.assertCollectionEqualInAnyOrder;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import io.faststream.sisyphus.expected.Expected;
import io.faststream.sisyphus.util.ArrayUtil;
import io.faststream.sisyphus.util.CollectionTestUtil;

/**
 * 
 * @author Kasper Nielsen
 */
/**
 * @param <E>
 *            the type of elements in the stream
 * @author Kasper Nielsen
 */
public class ExpectedStream<E> extends Expected implements Iterable<E> {

    final Context context;
    final List<E> list;

    final boolean isOrdered;

    final boolean isParallel;

    ExpectedStream(Context context, List<E> expected, boolean isParallel, boolean isOrdered) {
        this.context = requireNonNull(context);
        this.list = Collections.unmodifiableList(expected);
        this.isParallel = isParallel;
        this.isOrdered = isOrdered;
    }

    @SuppressWarnings("unchecked")
    public E reduce(BinaryOperator<? super E> reducer) {
        E expectedReduced = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            expectedReduced = (E) reducer.apply(expectedReduced, list.get(i));
        }
        return expectedReduced;
    }

    public void assertEqualsTo(Collection<E> other, boolean isOrdered) {
        if (isOrdered) {
            if (!(other instanceof List)) {
                other = new ArrayList<>(other);
            }
            assertEquals(list, other);
        } else {
            assertCollectionEqualInAnyOrder(list, other);
        }
    }

    public ExpectedStream<E> filter(Predicate<? super E> filter) {
        ArrayList<E> l = new ArrayList<>();
        for (E n : list) {
            if (filter.test(n)) {
                l.add(n);
            }
        }
        return withList(l);
    }

    public void forEach(Consumer<? super E> consumer) {
        list.forEach(consumer);
    }

    /**
     * @return the isOrdered
     */
    public boolean isOrdered() {
        return isOrdered;
    }

    /**
     * @return the isParallel
     */
    public boolean isParallel() {
        return isParallel;
    }

    public <T> ExpectedStream<T> map(Function<? super E, ? extends T> mapper) {
        ArrayList<T> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            result.add(i, mapper.apply(list.get(i)));
        }
        return withList(result);
    }

    public <T> ExpectedStream<Double> mapToDouble(ToDoubleFunction<? super E> mapper) {
        ArrayList<Double> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            result.add(i, mapper.applyAsDouble(list.get(i)));
        }
        return withList(result);
    }

    public <T> ExpectedStream<Integer> mapToInt(ToIntFunction<? super E> mapper) {
        ArrayList<Integer> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            result.add(i, mapper.applyAsInt(list.get(i)));
        }
        return withList(result);
    }

    public <T> ExpectedStream<Long> mapToLong(ToLongFunction<? super E> mapper) {
        ArrayList<Long> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            result.add(i, mapper.applyAsLong(list.get(i)));
        }
        return withList(result);
    }

    public int matchCount(Predicate<? super E> pred) {
        int count = 0;
        for (E t : list) {
            count += pred.test(t) ? 1 : 0;
        }
        return count;
    }

    public E reduce(E identity, BinaryOperator<E> ope) {
        return list.stream().reduce(identity, ope);
    }

    public ExpectedStream<E> sorted() {
        ArrayList<E> sorted = new ArrayList<>(list);
        return withList(CollectionTestUtil.sort(sorted)).withOrdered(true);
    }

    public List<E> asList() {
        return list;
    }

    public ExpectedStream<E> sorted(Comparator<E> comparator) {
        return withList(CollectionTestUtil.sort(new ArrayList<>(list), comparator)).withOrdered(true);
    }

    public ExpectedStream<E> sortedReverse() {
        ArrayList<E> sorted = new ArrayList<>(list);
        return withList(CollectionTestUtil.sortReverse(sorted)).withOrdered(true);
    }

    public ExpectedStream<E> subList(int start, int end) {
        return withList(list.subList(start, end));
    }

    public long[] toDoubleArray() {
        return ArrayUtil.toLongArray(toArray());
    }

    public long[] toLongArray() {
        return ArrayUtil.toLongArray(toArray());
    }

    public int[] toIntArray() {
        return ArrayUtil.toIntegerArray(toArray());
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public ExpectedStream<E> distinct() {
        return withList(new ArrayList<>(new HashSet<>(list))).withOrdered(false);
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return list.size();
    }

    public <T> ExpectedStream<T> withList(List<T> list) {
        return new ExpectedStream<>(context, list, isParallel, isOrdered);
    }

    public ExpectedStream<E> withParallel(boolean isParallel) {
        return new ExpectedStream<>(context, list, isParallel, isOrdered);
    }

    public ExpectedStream<E> withOrdered(boolean isOrdered) {
        return new ExpectedStream<>(context, list, isParallel, isOrdered);
    }

    public static <E> ExpectedStream<E> create(List<E> elements, boolean isParallel, boolean isOrdered) {
        return new ExpectedStream<>(new Context(), elements, isParallel, isOrdered);
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(list).iterator();
    }

    public boolean contains(Object element) {
        return list.contains(element);
    }

    public Context context() {
        return context;
    }

    public Collection<Object> registerForPeek() {
        OnPeek op = new OnPeek(this);
        context().peeks.add(op);
        return op.peekedElements;
    }

    public static class Context {
        final List<Runnable> closeHandlers = new ArrayList<>();

        final List<OnPeek> peeks = new ArrayList<>();

        /**
         * @param r
         */
        public void addRunnable(Runnable r) {
            closeHandlers.add(requireNonNull(r));
        }

        public List<Runnable> onClose() {
            List<Runnable> result = new ArrayList<>(closeHandlers);
            closeHandlers.clear();
            return result;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public void verifyOnConsume() {
            for (OnPeek op : peeks) {
                op.expected.assertEqualsTo((Collection) op.peekedElements, op.expected.isOrdered());
            }
        }
    }

    static class OnPeek {
        final ExpectedStream<?> expected;

        OnPeek(ExpectedStream<?> expected) {
            this.expected = expected;
        }

        Collection<Object> peekedElements = new ArrayList<>();
    }
}
