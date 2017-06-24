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
package org.faststream.sisyphus.view.spi;

import static io.faststream.sisyphus.util.MoreAsserts.assertCollectionEqualInAnyOrder;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import io.faststream.sisyphus.expected.Expected;
import io.faststream.sisyphus.util.CollectionTestUtil;

/**
 *
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class ExpectedCollectionView<E> extends Expected implements Iterable<E> {

    private final List<E> elements;

    final boolean isOrdered;

    public boolean isOrdered() {
        return isOrdered;
    }

    public ExpectedCollectionView(List<E> elements, boolean isOrdered) {
        this.elements = Collections.unmodifiableList(requireNonNull(elements));
        this.isOrdered = isOrdered;
    }

    public void assertEqualsTo(Collection<E> other, boolean isOrdered) {
        if (isOrdered) {
            if (!(other instanceof List)) {
                other = new ArrayList<>(other);
            }
            assertEquals(elements, other);
        } else {
            assertCollectionEqualInAnyOrder(elements, other);
        }
    }

    public boolean contains(Object element) {
        return elements.contains(element);
    }

    /** {@inheritDoc} */
    public String expectedString() {
        return elements.toString();
    }

    public ExpectedCollectionView<E> filter(Predicate<? super E> filter) {
        ArrayList<E> l = new ArrayList<>();
        for (E n : elements) {
            if (filter.test(n)) {
                l.add(n);
            }
        }
        return new ExpectedCollectionView<>(l, false);
    }

    public E first() {
        return elements.get(0);
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public Iterator<E> iterator() {
        return elements.iterator();
    }

    public E last() {
        return elements.get(elements.size() - 1);
    }

    public <T> ExpectedCollectionView<T> map(Function<? super E, ? extends T> mapper) {
        ArrayList<T> result = new ArrayList<>(elements.size());
        for (int i = 0; i < elements.size(); i++) {
            result.add(i, mapper.apply(elements.get(i)));
        }
        return new ExpectedCollectionView<>(result, isOrdered);
    }

    public ExpectedMapView<Long, E> mapToIndex() {
        LinkedHashMap<Long, E> map = new LinkedHashMap<>();
        for (int i = 0; i < elements.size(); i++) {
            map.put(Long.valueOf(i), elements.get(i));
        }
        return new ExpectedMapView<>(map, isOrdered);
    }

    @SuppressWarnings("unchecked")
    public E reduce(BinaryOperator<? super E> reducer) {
        E expectedReduced = elements.get(0);
        for (int i = 1; i < elements.size(); i++) {
            expectedReduced = (E) reducer.apply(expectedReduced, elements.get(i));
        }
        return expectedReduced;
    }

    public ExpectedCollectionView<E> reverse() {
        ArrayList<E> al = new ArrayList<>(elements);
        Collections.reverse(al);
        return new ExpectedCollectionView<>(al, isOrdered);
    }

    public int size() {
        return elements.size();
    }

    public ExpectedCollectionView<E> sorted() {
        ArrayList<E> list = new ArrayList<>(elements);
        return new ExpectedCollectionView<>(CollectionTestUtil.sort(list), true);
    }

    public ExpectedCollectionView<E> sorted(Comparator<E> comparator) {
        return new ExpectedCollectionView<>(CollectionTestUtil.sort(new ArrayList<>(elements), comparator), true);
    }

    public ExpectedCollectionView<E> sortedReverse() {
        ArrayList<E> list = new ArrayList<>(elements);
        return new ExpectedCollectionView<>(CollectionTestUtil.sortReverse(list), true);
    }

    public ExpectedCollectionView<E> subList(int start, int end) {
        return new ExpectedCollectionView<>(new ArrayList<>(elements.subList(start, end)), isOrdered);
    }

    public Object[] toArray() {
        return elements.toArray();
    }

    public ExpectedCollectionView<E> unique() {
        return new ExpectedCollectionView<>(new ArrayList<>(new HashSet<>(elements)), false);
    }

    public ExpectedCollectionView<E> withOrder(boolean isOrdered) {
        return new ExpectedCollectionView<>(elements, isOrdered);
    }
}
