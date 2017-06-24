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
import static org.junit.Assert.fail;

import java.util.OptionalLong;
import java.util.stream.LongStream;

import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test {@link LongStream#findAny()} and {@link LongStream#findFirst()}.
 * 
 * @author Kasper Nielsen
 */
public class Find extends AbstractRandomLongStreamTestCase {

    @RndTest
    public void findAny() {
        if (expected().size() == 0) {
            assertFalse(actual().findAny().isPresent());
        } else {
            OptionalLong l = actual().findAny();
            assertTrue(l.isPresent());
            if (!expected().contains(l.getAsLong())) {
                fail("LongStream.findAny() returned an unexpected element, expected one of " + expected().asList() + ", but was " + l.getAsLong());
            }
        }
        consumed();
    }

    @RndTest
    public void findFirst() {
        if (expected().size() == 0) {
            assertFalse(actual().findFirst().isPresent());
        } else {
            OptionalLong l = actual().findFirst();
            assertTrue(l.isPresent());
            if (!expected().contains(l.getAsLong())) {
                fail("LongStream.findFirst() returned an unexpected element, expected one of " + expected().asList() + ", but was " + l.getAsLong());
            }
            if (isOrdered()) {
                assertEquals(expected().asList().get(0).longValue(), l.getAsLong());
            }
        }
        consumed();
    }
}
