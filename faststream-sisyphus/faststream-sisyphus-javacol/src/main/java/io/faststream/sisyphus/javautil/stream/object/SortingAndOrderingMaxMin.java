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
package io.faststream.sisyphus.javautil.stream.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Ignore;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.TotalOrderComparator;
import io.faststream.sisyphus.util.ClassTestUtil;

/**
 *
 * @author Kasper Nielsen
 */
public class SortingAndOrderingMaxMin<E> extends AbstractRandomStreamTestCase<E> {

    @RndTest
    public void distinct() {
        nested(expected().distinct(), actual().distinct());
    }

    @RndTest
    public void max() {
        Comparator<E> comp = TotalOrderComparator.instance();
        comp = random().nextBoolean() ? comp : comp.reversed();
        Optional<E> max = actual().max(comp);
        if (expected().size() == 0) {
            assertFalse(max.isPresent());
        } else {
            assertTrue(max.isPresent());
            assertEquals(max.get(), Collections.max(expected().asList(), comp));
        }
        consumed();
    }

    @RndTest
    public void min() {
        Comparator<E> comp = TotalOrderComparator.instance();
        comp = random().nextBoolean() ? comp : comp.reversed();
        Optional<E> min = actual().min(comp);
        if (expected().size() == 0) {
            assertFalse(min.isPresent());
        } else {
            assertTrue(min.isPresent());
            assertEquals(min.get(), Collections.min(expected().asList(), comp));
        }
        consumed();
    }

    @RndTest
    @Ignore
    // Some issues probably with stable sorting
    public void sorted() {
        if (ClassTestUtil.isInterComparable(expected())) {
            nested(expected().sorted(), actual().sorted());
        } else {
            // Different types that are not mutable-comparable lets just do something else
            if (expected().size() > 1 && random().nextInt(10) == 0) {
                try {
                    actual().sorted().collect(Collectors.toList());
                    fail("Should throw ClassCastException");
                } catch (ClassCastException ok) {}
            } else {
                nested(expected(), actual());
            }
        }
    }

    @RndTest
    public void unordered() {
        nested(expected().withOrdered(false), actual().unordered());
    }
}
