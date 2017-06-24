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

import java.util.HashMap;
import java.util.Map;

import org.cakeframework.util.view.CollectionView;

import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link CollectionView#count()} operation.
 * 
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class Count<E> extends AbstractCollectionViewRandomTestCase<E> {

    @RndTest
    public void count() {
        Map<E, Long> m = new HashMap<>();
        for (E n : expected()) {
            Long current = m.get(n);
            m.put(n, current == null ? 1L : current + 1);
        }
        setNext(m, actual().count(), false);
    }
}
