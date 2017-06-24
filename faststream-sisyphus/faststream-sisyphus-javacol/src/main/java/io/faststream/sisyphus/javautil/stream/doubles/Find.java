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
import static org.junit.Assert.fail;

import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test {@link DoubleStream#findAny()} and {@link DoubleStream#findFirst()}.
 * 
 * @author Kasper Nielsen
 */
public class Find extends AbstractRandomDoubleStreamTestCase {

    @RndTest
    public void findAny() {
        if (expected().size() == 0) {
            assertFalse(actual().findAny().isPresent());
        } else {
            OptionalDouble l = actual().findAny();
            assertTrue(l.isPresent());
            if (!expected().contains(l.getAsDouble())) {
                fail("DoubleStream.findAny() returned an unexpected element, expected one of " + expected().asList() + ", but was " + l.getAsDouble());
            }
        }
        consumed();
    }

    @RndTest
    public void findFirst() {
        if (expected().size() == 0) {
            assertFalse(actual().findFirst().isPresent());
        } else {
            OptionalDouble l = actual().findFirst();
            assertTrue(l.isPresent());
            if (!expected().contains(l.getAsDouble())) {
                fail("DoubleStream.findFirst() returned an unexpected element, expected one of " + expected().asList() + ", but was " + l.getAsDouble());
            }
            if (isOrdered()) {
                assertEquals(expected().asList().get(0), l.getAsDouble(), 0.000000001);
            }
        }
        consumed();
    }
}
