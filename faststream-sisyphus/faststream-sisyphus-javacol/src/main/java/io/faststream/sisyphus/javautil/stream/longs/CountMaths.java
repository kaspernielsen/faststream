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
package io.faststream.sisyphus.javautil.stream.longs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LongSummaryStatistics;
import java.util.OptionalDouble;
import java.util.stream.Stream;

import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link Stream#count()} method.
 * 
 * @author Kasper Nielsen
 */
public class CountMaths extends AbstractRandomLongStreamTestCase {

    @RndTest
    public void count() {
        assertEquals(expected().size(), actual().count());
        consumed();
    }

    @RndTest
    public void average() {
        if (expected().size() == 0) {
            assertFalse(actual().average().isPresent());
        } else {
            long sum = expected().reduce(0L, (a, b) -> a + b);
            OptionalDouble d = actual().average();
            assertTrue(d.isPresent());
            assertEquals((double) sum / expected().size(), d.getAsDouble(), 0d);
        }
        consumed();
    }

    @RndTest
    public void sum() {
        long sum = expected().reduce(0L, (a, b) -> a + b);
        assertEquals(sum, actual().sum());
        consumed();
    }

    @RndTest
    public void summaryStatistics() {
        LongSummaryStatistics expected = new LongSummaryStatistics();
        expected().forEach(e -> expected.accept(e));
        LongSummaryStatistics actual = actual().summaryStatistics();
        assertEquals(expected.getSum(), actual.getSum(), 0);
        assertEquals(expected.getCount(), actual.getCount());
        assertEquals(expected.getMin(), actual.getMin());
        assertEquals(expected.getMax(), actual.getMax());
        consumed();
    }
}
