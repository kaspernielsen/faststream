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
package org.cakeframework.internal.view.interpreter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Test;

/**
 * Tests {@link ArrayUtil}.
 * 
 * @author Kasper Nielsen
 */
public class ArrayUtilTest {

    @Test
    public void sortDescending() {
        String[] str = new String[] { "B", "A", "D", "C", "E" };
        assertSame(str, ArrayUtil.sortDescending(str, 0, 5));
        assertArrayEquals(new String[] { "E", "D", "C", "B", "A" }, str);

        str = new String[] { "B", "A", "D", "C" };
        assertSame(str, ArrayUtil.sortDescending(str, 0, 4));
        assertArrayEquals(new String[] { "D", "C", "B", "A" }, str);

        str = new String[] { "B", "A", "D", "C", "Q" };
        assertSame(str, ArrayUtil.sortDescending(str, 1, 4));
        assertArrayEquals(new String[] { "B", "D", "C", "A", "Q" }, str);

        str = new String[] { "B", "A", "D", "C", "Q", "D" };
        assertSame(str, ArrayUtil.sortDescending(str, 1, 5));
        assertArrayEquals(new String[] { "B", "Q", "D", "C", "A", "D" }, str);

    }

    @Test
    public void reverse() {
        Integer[] i3 = new Integer[] { 1, 2, 3 };
        ArrayUtil.reverse(i3);
        assertTrue(i3[0] == 3 && i3[1] == 2 && i3[2] == 1);

        Integer[] i4 = new Integer[] { 1, 2, 3, 4 };
        ArrayUtil.reverse(i4);
        assertTrue(i4[0] == 4 && i4[1] == 3 && i4[2] == 2 && i4[3] == 1);

        Integer[] i4a = new Integer[] { 1, 2, 3, 4 };
        ArrayUtil.reverse(i4a, 1, 3);
        assertTrue(i4a[0] == 1 && i4a[1] == 3 && i4a[2] == 2 && i4a[3] == 4);
    }

    /**
     * Tests {@link ArrayUtil#shuffle(Object[], int, int, Random)} basically by making sure it behaves equivalent to
     * {@link Collections#shuffle(List, Random)} version.
     */
    @Test
    public void shuffle() {
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            l.add(i);
        }
        List<Integer> shuffled1 = new ArrayList<>(l);
        Collections.shuffle(shuffled1, new Random(1234));
        List<Integer> shuffled2 = new ArrayList<>(l);
        Collections.shuffle(shuffled2, new Random(1234));

        assertEquals(shuffled1, shuffled2);// just a dummy test
        Integer[] a = l.toArray(new Integer[0]);
        assertSame(a, ArrayUtil.shuffle(a, 0, a.length, new Random(1234)));
        assertEquals(shuffled1, Arrays.asList(a));// just a dummy test

        // subList
        shuffled2 = new ArrayList<>(l);
        List<Integer> sublist = shuffled2.subList(31, 77);
        Collections.shuffle(sublist, new Random(1234));
        a = l.toArray(new Integer[0]);
        ArrayUtil.shuffle(a, 31, 77, new Random(1234));
        assertEquals(shuffled2, Arrays.asList(a));// just a dummy test
    }

}
