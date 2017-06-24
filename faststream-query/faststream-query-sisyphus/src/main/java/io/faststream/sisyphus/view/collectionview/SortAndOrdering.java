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
package io.faststream.sisyphus.view.collectionview;

import static org.junit.Assert.fail;

import java.util.Comparator;

import io.faststream.query.util.view.CollectionView;
import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.TotalOrderComparator;
import io.faststream.sisyphus.util.ClassTestUtil;

/**
 * Test the {@link CollectionView#sorted()}, {@link CollectionView#sorted(Comparator)} and
 * {@link CollectionView#sortedDescending()} operation.
 *
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class SortAndOrdering<E> extends AbstractCollectionViewRandomTestCase<E> {
    @RndTest
    public void reverse() {
        setNested(expected().reverse(), actual().reverse());
    }

    @RndTest
    public void shuffle() {
        // no statistical tests
        setNested(expected().withOrder(false), actual().shuffle());
    }

    @RndTest
    public void sort() {
        if (ClassTestUtil.isInterComparable(expected())) {
            setNested(expected().sorted(), actual().sorted());
        } else {
            // Different types that are not mutable-comparable lets just do something else
            if (expected().size() > 1 && random().nextInt(10) == 0) {
                try {
                    actual().sorted().toList();
                    fail("Should throw ClassCastException");
                } catch (ClassCastException ok) {}
            } else {
                setNested(expected(), actual());
            }
        }
    }

    @RndTest
    public void sortComparator() {
        Comparator<E> comp = TotalOrderComparator.instance();
        comp = random().nextBoolean() ? comp : comp.reversed();
        setNested(expected().sorted(comp), actual().sorted(comp));
    }

    @RndTest
    public void sortDescending() {
        if (ClassTestUtil.isInterComparable(expected())) {
            setNested(expected().sortedReverse(), actual().sortedDescending());
        } else {
            // Different types that are not mutable-comparable lets just do something else
            if (expected().size() > 1 && random().nextInt(10) == 0) {
                try {
                    actual().sorted().toList();
                    fail("Should throw ClassCastException");
                } catch (ClassCastException ok) {}
            } else {
                setNested(expected(), actual());
            }
        }
    }

    @RndTest
    public void distinct() {
        setNested(expected().unique(), actual().distinct());
    }
}
