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
package org.cakeframework.test.random.view.collectionview;

import static io.faststream.sisyphus.util.MoreAsserts.assertCollectionEqualInAnyOrder;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import org.cakeframework.util.view.CollectionView;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.javautil.stream.object.ExpectedStream;
import io.faststream.sisyphus.stubs.DefaultFunction;

/**
 * Test the {@link CollectionView#map(Function)} operation.
 * <p>
 * This class is called Mapping and not Map to avoid configurations with the Java's build in {@link Map}.
 *
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class Mapping<E> extends AbstractCollectionViewRandomTestCase<E> {

    @RndTest
    public void map() {
        Function<E, Object> mapper = DefaultFunction.newInstance(Math.round(random().nextGaussian(0, 5)));
        setNested(expected().map(mapper), actual().map(mapper));
    }

    @RndTest
    public void mapToIndex() {
        if (isOrdered()) {
            setNext(expected().mapToIndex(), actual().mapToIndex());
        } else {
            Map<Long, E> m = actual().mapToIndex().toMap();
            assertEquals(expected().size(), m.size());
            Long prev = 0L;
            for (Long l : m.keySet()) {
                assertEquals(prev, l);
                prev++;
            }
            assertCollectionEqualInAnyOrder(expected(), m.values());
        }
    }

    @RndTest
    // @CustomWeight(1000)
    public void stream() {
        nested(ExpectedStream.create(Arrays.asList(expected().toArray()), false, isOrdered()), actual().asStream());
        streamStart();
    }
}
