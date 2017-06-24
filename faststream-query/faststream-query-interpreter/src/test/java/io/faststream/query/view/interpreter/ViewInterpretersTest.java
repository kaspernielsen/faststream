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
package io.faststream.query.view.interpreter;

import static org.junit.Assert.assertArrayEquals;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Test;

import io.faststream.query.view.interpreter.ViewInterpreters;

/**
 * 
 * @author Kasper Nielsen
 */
public class ViewInterpretersTest {
    @Test
    public void convertIterableToArray() {
        assertArrayEquals(new Object[] { 1, null, 3 },
                ViewInterpreters.convertIterableToArray(Arrays.asList(1, null, 3)));
        assertArrayEquals(new Object[] { Paths.get("a"), Paths.get("b"), Paths.get("C") },
                ViewInterpreters.convertIterableToArray(Paths.get("a", "b", "C")));
        assertArrayEquals(new Object[] {}, ViewInterpreters.convertIterableToArray(new Iterable<Object>() {
            public Iterator<Object> iterator() {
                return Collections.emptyIterator();
            }
        }));
    }
}
