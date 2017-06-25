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
package io.faststream;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

/**
 *
 * @author Kasper Nielsen
 */

public class FastStreamTest {

    @Test
    public void testArray() {
        assertEquals(4, FastStreams.of(new Object[] { 1, 2, 3, 4 }).count());
    }

    @Test
    public void testArrayInt() {
        assertEquals(4, FastStreams.ofInt(new int[] { 1, 2, 3, 4 }).peek(e -> {}).count());
    }

    @Test
    public void testIt() {
        assertEquals(4, FastStreams.newArrayList(Arrays.asList(1, 2, 3, 4)).stream().count());
    }

}
