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
package io.faststream.sisyphus.javautil.stream.ints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.OptionalInt;

import io.faststream.sisyphus.annotations.RndTest;

/**
 *
 * @author Kasper Nielsen
 */
public class SortingDistinctMaxMin extends AbstractRandomIntStreamTestCase {

    @RndTest
    public void distinct() {
        nested(expected().distinct().withOrdered(false), actual().distinct());
    }

    @RndTest
    public void max() {
        OptionalInt max = actual().max();
        if (expected().size() == 0) {
            assertFalse(max.isPresent());
        } else {
            assertTrue(max.isPresent());
            assertEquals(max.getAsInt(), Collections.max(expected().asList()).intValue());
        }
        consumed();
    }

    @RndTest
    public void min() {
        OptionalInt min = actual().min();
        if (expected().size() == 0) {
            assertFalse(min.isPresent());
        } else {
            assertTrue(min.isPresent());
            assertEquals(min.getAsInt(), Collections.min(expected().asList()).intValue());
        }
        consumed();
    }

    @RndTest
    public void sorted() {
        nested(expected().sorted(), actual().sorted());
    }

    @RndTest
    public void unordered() {
        nested(expected().withOrdered(false), actual().unordered());
    }
}
