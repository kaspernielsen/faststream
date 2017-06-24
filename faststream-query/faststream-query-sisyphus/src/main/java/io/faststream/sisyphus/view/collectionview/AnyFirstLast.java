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

import java.util.NoSuchElementException;
import java.util.Objects;

import io.faststream.query.util.view.CollectionView;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link CollectionView#any()} operation.
 *
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class AnyFirstLast<E> extends AbstractCollectionViewRandomTestCase<E> {

    @RndTest
    public void any() {
        if (expected().size() == 0) {
            try {
                E any = actual().any();
                fail("collectionView.any() should throw NoSuchElementException when view contains no elements, but returned "
                        + any);
            } catch (NoSuchElementException ok) {}
        } else {
            E any = actual().any();
            if (!expected().contains(any)) {
                fail("collectionView.any() returned an unexpected element, expected one of " + expected()
                        + ", but was " + any);
            }
        }
    }

    @RndTest
    public void anyBase() {
        E base = getBase();
        E any = actual().any(base);
        if (expected().size() == 0) {
            if (base != any) {
                fail("collectionView.any(base) should return the specified base for an empty view [ returned=" + any
                        + ", base=" + base + "]");
            }
        } else if (!expected().contains(any)) {
            fail("collectionView.any() returned an unexpected element, expected one of " + expected());
        }
    }

    @RndTest
    public void first() {
        if (expected().size() == 0) {
            try {
                actual().first();
                fail("collectionView.head() should throw NoSuchElementException when view contains no elements");
            } catch (NoSuchElementException ok) {}
        } else {
            E head = actual().first();
            if (isOrdered()) {
                E expectedHead = expected().iterator().next();
                if (!Objects.equals(expectedHead, head)) {
                    fail("collectionView.head() returned an unexpected elements, expected " + expectedHead + ", was = "
                            + head + ", elements=" + expected());
                }
            } else if (!expected().contains(head)) { // collectionView.head() unordered returns any element
                fail("(unordered) collectionView.head() returned an unexpected element, expected one of " + expected());
            }
        }
    }

    @RndTest
    public void firstBase() {
        E base = getBase();
        E head = actual().first(base);

        if (expected().size() == 0) {
            if (base != head) {
                fail("collectionView.head(base) should return the specified base for an empty view [ returned=" + head
                        + ", base=" + base + "]");
            }
        } else if (isOrdered()) {
            E expectedHead = expected().iterator().next();
            if (!Objects.equals(expectedHead, head)) {
                fail("collectionView.head(base) returned an unexpected elements, expected " + expectedHead + ", was "
                        + head + ", elements=" + expected());
            }
        } else if (!expected().contains(head)) { // collectionView.head(base) unordered returns any element
            fail("(unordered) collectionView.head(base) returned an unexpected element, expected one of " + expected()
                    + ", but was " + head);
        }
    }

    @RndTest
    public void last() {
        if (expected().size() == 0) {
            try {
                actual().last();
                fail("collectionView.tail() should throw NoSuchElementException when view contains no elements");
            } catch (NoSuchElementException ok) {}
        } else {
            E tail = actual().last();
            if (isOrdered()) {
                E expectedTail = expected().last();
                if (!Objects.equals(expectedTail, tail)) {
                    fail("collectionView.tail() returned an unexpected elements, expected " + expectedTail + ", was "
                            + tail + ", elements=" + expected());
                }
            } else if (!expected().contains(tail)) { // collectionView.tail() unordered returns any element
                fail("(unordered) collectionView.tail() returned an unexpected element, expected one of " + expected()
                        + " but got " + tail);
            }
        }
    }

    @RndTest
    public void lastBase() {
        E base = getBase();
        E tail = actual().last(base);

        if (expected().size() == 0) {
            if (base != tail) {
                fail("collectionView.tail(base) should return the specified base for an empty view [ returned=" + tail
                        + ", base=" + base + "]");
            }
        } else if (isOrdered()) {
            E expectedTail = expected().last();
            if (!Objects.equals(expectedTail, tail)) {
                fail("collectionView.tail(base) returned an unexpected elements, expected " + expectedTail
                        + " but was  " + tail + ", expected elements=" + expected());
            }
        } else if (!expected().contains(tail)) { // collectionView.tail(base) unordered returns any element
            fail("(unordered) collectionView.tail(base) returned an unexpected element, expected one of " + expected()
                    + ", but was " + tail);
        }
    }
}
