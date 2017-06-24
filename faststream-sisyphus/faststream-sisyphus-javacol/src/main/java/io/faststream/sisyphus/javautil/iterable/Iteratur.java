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
package io.faststream.sisyphus.javautil.iterable;

import static org.junit.Assert.fail;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import io.faststream.sisyphus.annotations.CustomWeight;
import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.spi.TestCase;

/**
 * @param <E>
 *            the elements in the iterable
 * @author Kasper Nielsen
 */
// intentional misspelling to avoid collisions with java.util.Iterator
public class Iteratur<E> extends TestCase<ExpectedIterable<E>, Iterable<E>> {

    @RndTest
    public void iterator() {

    }

    @RndTest
    @CustomWeight(0.1)
    public void iteratorNextFailsAtTheEnd() {
        Iterator<E> iter = actual().iterator();
        while (iter.hasNext()) {
            iter.next();
        }
        try {
            iter.next();
            fail("Iterator.next() should throw a NoSuchElementException when there are no elements to iterate over (Iterator.hasNext() returns false)");
        } catch (NoSuchElementException ok) {}
    }

    /** Tests that Iterator.remove() should throw an IllegalStateException if next() has not been called first. */
    @FailWith(IllegalStateException.class)
    public void removeFailsWithoutCallingNext() {
        assumeRemovable();
        Iterator<E> iter = actual().iterator();
        iter.remove();
    }

    private void assumeRemovable() {
        expected().assumeRemovable();
    }

    public void concurrentModificationOnNext() {
        assumeRemovable();
        long size = expected().size();
        if (size < 2) {
            return;
        }

        // TODO fix for collections that are not ordered
        Iterator<E> actualIterator1 = actual().iterator();
        Iterator<E> actualIterator2 = actual().iterator();
        Iterator<E> expectedIterator = expected().iterator();
        int elementIndex = 0;// nextInt(size);
        for (int i = 0; i < elementIndex; i++) {
            actualIterator1.next();
            expectedIterator.next();
            actualIterator2.next();
        }

        actualIterator1.remove();
        expectedIterator.remove();

        try {
            actualIterator2.next();
            fail("should fail, element has already been removed by first iterator");
        } catch (ConcurrentModificationException ok) {}
    }
}
