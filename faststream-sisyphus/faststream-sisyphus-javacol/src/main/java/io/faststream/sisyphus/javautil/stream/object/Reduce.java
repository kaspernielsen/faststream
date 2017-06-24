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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.DefaultBinaryOperator;

/**
 * Test the {@link CollectionView#reduce(BinaryOperator)} operation.
 *
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class Reduce<E> extends AbstractRandomStreamTestCase<E> {

    @SuppressWarnings("unchecked")
    private BinaryOperator<E> getReducer() {
        return (BinaryOperator<E>) DefaultBinaryOperator.REDUCER;
    }

    @RndTest
    public void reduce() {
        BinaryOperator<E> reducer = getReducer();
        Optional<E> reduce = actual().reduce(reducer);
        if (expected().size() == 0) {
            assertFalse(reduce.isPresent());
        } else {
            assertTrue(reduce.isPresent());
            E expectedReduced = expected().reduce(reducer);
            assertEquals(expectedReduced, reduce.get());
        }
        consumed();
    }

    public E getBase() {
        return null; // TODO Auto-generated method stub
    }

    @RndTest
    public void reduceBase() {
        BinaryOperator<E> reducer = getReducer();
        if (expected().size() == 0) {
            E base = getBase();
            E reduced = actual().reduce(base, reducer);
            if (!Objects.equals(base, reduced)) {
                fail("collectionView.reduce(reducer, base) should return the specified base for an empty view [ returned =" + reduced + ", base=" + base + "]");
            }
        } else {
            E expectedReduced = expected().reduce(reducer);
            E reduced = actual().reduce(getBase(), reducer);
            if (!Objects.equals(expectedReduced, reduced)) {
                fail("collectionView.reduce(reduced, base) returned unexpected result [expected=" + expectedReduced + ", actual=" + reduced);
            }
        }
        consumed();
    }

    // TODO uncomment
    // @RndTest
    public void reduceFused() {
        long result = actual().reduce(0L, (a, b) -> a + DefaultBinaryOperator.longValue(b), (a, b) -> a + b);
        long e = 0L;
        for (E element : expected()) {
            e += DefaultBinaryOperator.longValue(element);
        }
        assertEquals(e, result);
    }
}
