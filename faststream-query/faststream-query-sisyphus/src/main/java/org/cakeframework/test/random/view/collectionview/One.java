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

import java.util.NoSuchElementException;
import java.util.Objects;

import org.cakeframework.util.view.CollectionView;

import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link CollectionView#one()} and {@link CollectionView#one(Object)} operation.
 * 
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class One<E> extends AbstractCollectionViewRandomTestCase<E> {

    @RndTest
    public void one() {
        if (expected().size() == 0) {
            try {
                E any = actual().one();
                fail("collectionView.one() should throw NoSuchElementException when view contains no elements, but returned "
                        + any);
            } catch (NoSuchElementException ok) {}
        } else if (expected().size() == 1) {
            E one = actual().one();
            Object expectedOne = expected().iterator().next();
            if (!Objects.equals(expectedOne, one)) {
                fail("collectionView.one() returned an unexpected element, expected " + expectedOne + ", but was "
                        + one);
            }
        } else {
            try {
                actual().one();
                fail("collectionView.one() should throw IllegalStateException when view does not contain exactly 1 element, elements in view= "
                        + actual().toList());
            } catch (IllegalStateException ok) {}
        }
    }

    @RndTest
    public void oneBase() {
        E base = getBase();
        if (expected().size() == 0) {
            E one = actual().one(base);
            if (base != one) {
                fail("collectionView.one(base) should return the specified base for an empty view [ returned=" + one
                        + ", base=" + base + "]");
            }
        } else if (expected().size() == 1) {
            E one = actual().one(base);
            if (!expected().contains(one)) {
                fail("collectionView.one() returned an unexpected element, expected " + expected().iterator().next());
            }
        } else {
            try {
                actual().one(base);
                fail("collectionView.one(base) should throw IllegalStateException when view does not contain exactly 1 element, elements in view = "
                        + actual().toList());
            } catch (IllegalStateException ok) {}
        }
    }
}
