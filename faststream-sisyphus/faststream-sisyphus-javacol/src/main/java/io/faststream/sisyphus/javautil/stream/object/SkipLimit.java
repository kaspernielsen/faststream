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

import java.util.Arrays;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.util.MoreAsserts;

/**
 *
 * @author Kasper Nielsen
 */
public class SkipLimit<E> extends AbstractRandomStreamTestCase<E> {

    @RndTest
    public void limit() {
        int expectedSize = expected().size();
        long limit = nextSkipLimit();
        if (isOrdered() || limit == 0 || limit >= expectedSize) {
            ExpectedStream<?> l = expected().subList(0, (int) Math.min(expectedSize, limit));
            nested(l, actual().limit(limit));
        } else {
            // We don't really know which elements the stream are going to skip since it is unordered.
            // best we can do is consume it and check.
            Object[] l = actual().limit(limit).toArray();
            assertEquals(Math.min(expectedSize, limit), l.length);
            MoreAsserts.assertCollectionIsCollectionSubset(expected(), Arrays.asList(l));
            consumed();
        }
    }

    @RndTest
    public void skip() {
        int expectedSize = expected().size();
        long skip = nextSkipLimit();
        if (isOrdered() || skip == 0 || skip >= expectedSize) {
            ExpectedStream<?> l = expected().subList((int) Math.min(expectedSize, skip), expectedSize);
            nested(l, actual().skip(skip));
        } else {
            // We don't really know which elements the stream are going to skip since it is unordered.
            // best we can do is consume it and check.
            Object[] l = actual().skip(skip).toArray();
            long es = expectedSize - Math.min(expectedSize, skip);
            assertEquals(es, l.length);
            MoreAsserts.assertCollectionIsCollectionSubset(expected(), Arrays.asList(l));
            consumed();
        }
    }

}
