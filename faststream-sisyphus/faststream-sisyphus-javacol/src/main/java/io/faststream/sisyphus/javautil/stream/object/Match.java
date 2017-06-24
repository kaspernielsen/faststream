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

import java.util.function.Predicate;
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
public class Match<E> extends AbstractRandomStreamTestCase<E> {

    @RndTest
    public void allMatch() {
        Predicate<? super E> p = new HashcodeModPredicate<>(random().nextInt(1, 4));
        assertEquals(expected().matchCount(p) == expected().size(), actual().allMatch(i -> p.test(i)));
        consumed();
    }

    @RndTest
    public void anyMatch() {
        Predicate<? super E> p = new HashcodeModPredicate<>(random().nextInt(2, 14));
        assertEquals(expected().matchCount(p) > 0, actual().anyMatch(i -> p.test(i)));
        consumed();
    }

    @RndTest
    public void noneMatch() {
        Predicate<? super E> p = new HashcodeModPredicate<>(random().nextInt(2, 14));
        assertEquals(expected().matchCount(p) == 0, actual().noneMatch(i -> p.test(i)));
        consumed();
    }
}
