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
package io.faststream.sisyphus.view.collectionview;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.faststream.query.util.view.CollectionView;
import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.view.collectionview.Foreach.ConcurrentQueueAllowingNull;

/**
 * Test the {@link CollectionView#gather(Supplier)} operation.
 *
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class Gather<E> extends AbstractCollectionViewRandomTestCase<E> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RndTest
    public void gather() {
        final ConcurrentLinkedQueue<ConcurrentQueueAllowingNull<E>> q = new ConcurrentLinkedQueue<>();
        List<Consumer<E>> list = actual().gather(new Supplier<Consumer<E>>() {
            @Override
            public Consumer<E> get() {
                ConcurrentQueueAllowingNull<E> v = new ConcurrentQueueAllowingNull<>();
                q.add(v);
                return v;
            }
        }).toList();
        assertEquals(new HashSet(q), new HashSet(list));
        List<E> all = new ArrayList<>();
        for (ConcurrentQueueAllowingNull<E> v : q) {
            all.addAll(v.toCollection());
        }
        expected().assertEqualsTo(all, false);
    }
}
