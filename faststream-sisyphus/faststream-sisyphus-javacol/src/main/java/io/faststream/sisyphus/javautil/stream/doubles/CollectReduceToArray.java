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

import static io.faststream.sisyphus.util.MoreAsserts.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.javautil.stream.object.CollectorHelper;
import io.faststream.sisyphus.javautil.stream.object.CollectorHelper.ResultContainer;

/**
 *
 * @author Kasper Nielsen
 */
public class CollectReduceToArray extends AbstractRandomDoubleStreamTestCase {

    @RndTest
    public void reduce() {
        if (expected().size() == 0) {
            OptionalDouble o = actual().reduce((a, b) -> a + b);
            assertFalse(o.isPresent());
        } else {
            OptionalDouble o = actual().reduce((a, b) -> a + b);
            assertTrue(o.isPresent());
            double e = expected().reduce(0d, (a, b) -> a + b);
            assertEqualsVar(e, o.getAsDouble());
        }
        consumed();
    }

    @RndTest
    public void reduceIdentity() {
        if (expected().size() == 0) {
            double l = actual().reduce(0, (a, b) -> a + b);
            assertEquals(0, l, 0);
        } else {
            double l = actual().reduce(0, (a, b) -> a + b);
            double e = expected().reduce(0d, (a, b) -> a + b);
            assertEqualsVar(e, l);
        }
        consumed();
    }

    @RndTest
    public void toArray() {
        List<Object> l = new ArrayList<>();
        for (double ll : actual().toArray()) {
            l.add(ll);
        }
        assertArrayEquals(expected().toArray(), l.toArray(), isOrdered());
        consumed();
    }

    @RndTest
    public void collect() {
        CollectorHelper<Double> h = new CollectorHelper<>();

        ResultContainer<Double> result = actual().collect(h.supplier(), h.accumulatorDouble(), h.combinerConsumer());

        expected().assertEqualsTo(result.result, expected().isOrdered());
        consumed();
    }
}
