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
package io.faststream.sisyphus.javautil.stream.doubles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.OptionalDouble;

import io.faststream.sisyphus.annotations.RndTest;

/**
 *
 * @author Kasper Nielsen
 */
public class SortingDistinctMaxMin extends AbstractRandomDoubleStreamTestCase {

    @RndTest
    public void distinct() {
        nested(expected().distinct().withOrdered(false), actual().distinct());
    }

    @RndTest
    public void max() {
        OptionalDouble max = actual().max();
        if (expected().size() == 0) {
            assertFalse(max.isPresent());
        } else {
            assertTrue(max.isPresent());
            assertEquals(max.getAsDouble(), Collections.max(expected().asList()).doubleValue(), 0.0000001);
        }
        consumed();
    }

    @RndTest
    public void min() {
        OptionalDouble min = actual().min();
        if (expected().size() == 0) {
            assertFalse(min.isPresent());
        } else {
            assertTrue(min.isPresent());
            assertEquals(min.getAsDouble(), Collections.min(expected().asList()).doubleValue(), 0.0000001);
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
