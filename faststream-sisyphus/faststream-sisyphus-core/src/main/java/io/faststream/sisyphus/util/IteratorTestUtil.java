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
package io.faststream.sisyphus.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * @author Kasper Nielsen
 */
public class IteratorTestUtil {

    public static void testEmptyIterator(Iterator<?> iterator) {
        assertFalse(iterator.hasNext());

        try {
            iterator.next();
            fail("should throw NoSuchElementException");
        } catch (NoSuchElementException ok) {}

        iterator.forEachRemaining(e -> {
            throw new AssertionError();
        });
    }

    public static void testEmptyImmutableIterator(Iterator<?> iterator) {
        testEmptyIterator(iterator);

        try {
            iterator.remove();
            fail("should throw IllegalStateException");
        } catch (IllegalStateException ok) {}
    }

    public static void testIterable(Iterable<?> iterable, Object... nodes) {
        testIterator(iterable.iterator(), nodes);
    }

    public static void testIterableAnyOrder(Iterable<?> iterable, Object... elements) {
        testIteratorAnyOrder(iterable.iterator(), elements);
    }

    public static void testIteratorAnyOrder(Iterator<?> actual, Object... expected) {
        ArrayList<Object> set = new ArrayList<>(Arrays.asList(expected));
        // System.out.println(Arrays.asList(expected));
        for (int i = 0; i < expected.length; i++) {
            // System.out.println("A" + i + ":" + elements.length);
            assertTrue(actual.hasNext());
            Object next = actual.next();
            // System.out.println("x " + next);
            assertTrue(set.remove(next));
        }
        assertEquals(0, set.size());
        testEmptyIterator(actual);
    }

    public static void testIterator(Iterator<?> iterator, Object... elements) {
        for (int i = 0; i < elements.length; i++) {
            assertTrue(iterator.hasNext());
            assertEquals(elements[i], iterator.next());
        }
        assertFalse(iterator.hasNext());
        nextFail(iterator);
    }

    public static void nextFail(Iterator<?> iterator) {
        try {
            iterator.next();
            fail("should throw NoSuchElementException");
        } catch (NoSuchElementException ok) {}
    }

    public static void removeFail(Iterator<?> iterator) {
        try {
            fail("should throw NoSuchElementException");
        } catch (NoSuchElementException ok) {}
    }

}
