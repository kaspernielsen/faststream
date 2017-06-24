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
package org.cakeframework.test.random.view.collectionview;

import static org.junit.Assert.fail;

import java.util.Objects;
import java.util.function.BinaryOperator;

import org.cakeframework.util.view.CollectionView;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.DefaultBinaryOperator;

/**
 * Test the {@link CollectionView#reduce(BinaryOperator)} operation.
 *
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class Reduce<E> extends AbstractCollectionViewRandomTestCase<E> {

    @SuppressWarnings("unchecked")
    private BinaryOperator<E> getReducer() {
        return (BinaryOperator<E>) DefaultBinaryOperator.REDUCER;
    }

    @RndTest
    public void reduce() {
        BinaryOperator<E> reducer = getReducer();
        if (expected().size() == 0) {
            try {
                actual().reduce(reducer);
                fail("collectionView.reduce(reducer) should throw IllegalStateException when view contains no elements");
            } catch (IllegalStateException ok) {}
        } else {
            E expectedReduced = expected().reduce(reducer);
            E reduced = actual().reduce(reducer);
            if (!Objects.equals(expectedReduced, reduced)) {
                System.out.println("Expected elements " + expected().expectedString());
                System.out.println(reducer);
                System.out.println(actual());
                System.out.println("Actual elements " + actual().toList().size());
                System.out.println("Actual size " + actual().size());
                // actual().forEach(e -> System.out.println(e));
                fail("collectionView.reduce(reduced) returned unexpected result [expected=" + expectedReduced
                        + ", actual=" + reduced);

            }
        }
    }

    @RndTest
    public void reduceBase() {
        BinaryOperator<E> reducer = getReducer();
        if (expected().size() == 0) {
            E base = getBase();
            E reduced = actual().reduce(base, reducer);
            if (base != reduced) {
                fail("collectionView.reduce(reducer, base) should return the specified base for an empty view [ returned ="
                        + reduced + ", base=" + base + "]");
            }
        } else {
            E expectedReduced = expected().reduce(reducer);
            E reduced = actual().reduce(getBase(), reducer);
            if (!Objects.equals(expectedReduced, reduced)) {
                fail("collectionView.reduce(reduced, base) returned unexpected result [expected=" + expectedReduced
                        + ", actual=" + reduced);
            }
        }
    }

}
