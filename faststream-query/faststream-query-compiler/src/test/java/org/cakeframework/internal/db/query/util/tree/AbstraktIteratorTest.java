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
package org.cakeframework.internal.db.query.util.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

import io.faststream.sisyphus.util.IteratorTestUtil;

/**
 * Tests {@link AbstractIterator}.
 * 
 * @author Kasper Nielsen
 */
public class AbstraktIteratorTest {

    String currentVal;
    String nextVal;

    @Test
    public void test() {
        AbstractIteratorStub foo = new AbstractIteratorStub("abc");
        assertTrue(foo.hasNext());
        currentVal = "abc";
        assertEquals("abc", foo.next());
        assertFalse(foo.hasNext());
        IteratorTestUtil.nextFail(foo);
    }

    @Test
    public void test2() {
        nextVal = "cba";
        AbstractIteratorStub foo = new AbstractIteratorStub();
        assertTrue(foo.hasNext());
        currentVal = "cba";
        nextVal = "abc";
        assertEquals("cba", foo.next());
        assertTrue(foo.hasNext());
        currentVal = "abc";
        nextVal = null;
        assertEquals("abc", foo.next());
        assertFalse(foo.hasNext());
        IteratorTestUtil.nextFail(foo);
    }

    @Test
    public void asIterable() {
        AbstractIterator<Integer> ai = create(1, 2, 3, 4, 5);
        Iterable<Integer> ai2 = ai.asIterable();
        assertSame(ai, ai2.iterator());
        assertSame(ai, ai2.iterator());
    }

    @Test
    public void predicate() {
        IteratorTestUtil.testIterator(create(1, 2, 3, 4, 5), 1, 2, 3, 4, 5);
        IteratorTestUtil.testIterator(create(1, 2, 3).filtered(e -> true), 1, 2, 3);
        IteratorTestUtil.testIterator(create(1, 2, 3).filtered(e -> false));
        IteratorTestUtil.testIterator(create(1, 2, 3).filtered(e -> e.equals(1)), 1);
        IteratorTestUtil.testIterator(create(1, 2, 3).filtered(e -> e.equals(2)), 2);
        IteratorTestUtil.testIterator(create(1, 2, 3).filtered(e -> e.equals(3)), 3);
        IteratorTestUtil.testIterator(create(1, 2, 3).filtered(e -> Arrays.asList(1, 3).contains(e)), 1, 3);
        IteratorTestUtil.testIterator(create(1, 2, 3).filtered(e -> Arrays.asList(2, 3).contains(e)), 2, 3);
        IteratorTestUtil.testIterator(create(1, 2, 3).filtered(e -> Arrays.asList(1, 2).contains(e)), 1, 2);
    }

    @SafeVarargs
    static <T> AbstractIterator<T> create(final T... ts) {
        final Iterator<T> i = Arrays.asList(ts).iterator();
        return new AbstractIterator<T>() {
            protected T next0(T current) {
                return i.hasNext() ? i.next() : null;
            }
        };
    }

    @Test(expected = UnsupportedOperationException.class)
    public void remove() {
        new AbstractIteratorStub("AB").remove();
    }

    class AbstractIteratorStub extends AbstractIterator<String> {
        public AbstractIteratorStub() {
            super();
        }

        public AbstractIteratorStub(String first) {
            super(first);
        }

        /** {@inheritDoc} */
        @Override
        protected String next0(String current) {
            assertEquals(currentVal, current);
            return nextVal;
        }
    }
}
