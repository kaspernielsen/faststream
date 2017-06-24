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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

import io.faststream.sisyphus.annotations.RndTest;

/**
 * Tests {@link IntStream#forEach(java.util.function.IntConsumer)} and
 * {@link IntStream#forEachOrdered(java.util.function.IntConsumer)}.
 * 
 * @author Kasper Nielsen
 */
public class ForEach extends AbstractRandomIntStreamTestCase {

    @RndTest
    public void forEach() {
        ConcurrentLinkedQueue<Integer> q = new ConcurrentLinkedQueue<>();
        actual().forEach(e -> q.add(e));
        expected().assertEqualsTo(q, false);
        consumed();
    }

    @RndTest
    public void forEachOrdered() {
        if (expected().isOrdered()) {
            ConcurrentLinkedQueue<Integer> q = new ConcurrentLinkedQueue<>();
            actual().forEachOrdered(e -> q.add(e));
            expected().assertEqualsTo(q, true);
            consumed();
        } else {
            forEach();
        }
    }
}
