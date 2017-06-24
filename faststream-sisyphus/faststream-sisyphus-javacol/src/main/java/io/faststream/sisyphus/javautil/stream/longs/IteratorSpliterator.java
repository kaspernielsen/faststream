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

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.util.IteratorTestUtil;
import io.faststream.sisyphus.util.SpliteratorTestUtil;

/**
 *
 * @author Kasper Nielsen
 */
public class IteratorSpliterator extends AbstractRandomLongStreamTestCase {

    @RndTest
    public void iterator() {
        if (expected().isOrdered()) {
            IteratorTestUtil.testIterator(actual().iterator(), expected().toArray());
        } else {
            IteratorTestUtil.testIteratorAnyOrder(actual().iterator(), expected().toArray());
        }
        consumed();
    }

    @RndTest
    public void spliterator() {
        Spliterator<Long> spliterator = actual().spliterator();
        List<Long> actual = new ArrayList<>();
        SpliteratorTestUtil.testSpliterator(random(), spliterator, actual);
        expected().assertEqualsTo(actual, expected().isOrdered());
        consumed();
    }
}
