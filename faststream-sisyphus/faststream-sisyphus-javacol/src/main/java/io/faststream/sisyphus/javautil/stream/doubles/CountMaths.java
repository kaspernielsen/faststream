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

import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.stream.IntStream;

import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link IntStream#count()} method.
 * 
 * @author Kasper Nielsen
 */
public class CountMaths extends AbstractRandomDoubleStreamTestCase {

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
            double sum = 0;
            for (double i : expected()) {
                sum += i;
            }
            OptionalDouble d = actual().average();
            assertTrue(d.isPresent());
            assertEqualsVar(sum / expected().size(), d.getAsDouble());
        }
        consumed();
    }

    @RndTest
    public void sum() {
        double sum = expected().reduce(0d, (a, b) -> a + b);
        assertEqualsVar(sum, actual().sum());
        consumed();
    }

    @RndTest
    public void summaryStatistics() {
        DoubleSummaryStatistics expected = new DoubleSummaryStatistics();
        expected().forEach(e -> expected.accept(e));
        DoubleSummaryStatistics actual = actual().summaryStatistics();
        assertEqualsVar(expected.getSum(), actual.getSum());
        assertEquals(expected.getCount(), actual.getCount());
        assertEquals(expected.getMin(), actual.getMin(), 0);
        assertEquals(expected.getMax(), actual.getMax(), 0);
        consumed();
    }
    // average()
    // sum()
    // summaryStatistics()

}
