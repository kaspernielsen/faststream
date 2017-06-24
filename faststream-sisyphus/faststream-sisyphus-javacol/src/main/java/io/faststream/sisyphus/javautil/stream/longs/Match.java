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

import java.util.stream.LongStream;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.HashcodeModPredicate;

/**
 * Test {@link LongStream#allMatch(java.util.function.LongPredicate)},
 * {@link LongStream#anyMatch(java.util.function.LongPredicate)},
 * {@link LongStream#noneMatch(java.util.function.LongPredicate)}
 * 
 * @param <E>
 *            the type of elements in the stream
 * 
 * @author Kasper Nielsen
 */
public class Match extends AbstractRandomLongStreamTestCase {

    @RndTest
    public void allMatch() {
        HashcodeModPredicate<Long> p = new HashcodeModPredicate<>(random().nextInt(1, 4));
        if (expected().size() == 0) {
            assertTrue(actual().allMatch(p));
        } else {
            assertEquals(expected().matchCount(p) == expected().size(), actual().allMatch(p));
        }
        consumed();
    }

    @RndTest
    public void anyMatch() {
        HashcodeModPredicate<Long> p = new HashcodeModPredicate<>(random().nextInt(2, 14));
        if (expected().size() == 0) {
            assertFalse(actual().anyMatch(p));
        } else {
            assertEquals(expected().matchCount(p) > 0, actual().anyMatch(p));
        }
        consumed();
    }

    @RndTest
    public void noneMatch() {
        HashcodeModPredicate<Long> p = new HashcodeModPredicate<>(random().nextInt(2, 14));
        if (expected().size() == 0) {
            assertTrue(actual().noneMatch(p));
        } else {
            assertEquals(expected().matchCount(p) == 0, actual().noneMatch(p));
        }
        consumed();
    }
}
