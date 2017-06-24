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

import java.util.function.Function;

import org.cakeframework.internal.util.Multimaps;
import org.cakeframework.util.Multimap;
import org.cakeframework.util.view.CollectionView;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.LogHashcodeFunction;

/**
 * Test the {@link CollectionView#groupBy(Function)} operation.
 *
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class GroupBy<E> extends AbstractCollectionViewRandomTestCase<E> {

    @RndTest
    public void groupBy() {
        Function<E, Number> mapper = LogHashcodeFunction.instance();

        Multimap<Number, E> map = Multimaps.newOrderedListMultimap();
        // System.out.println("AA");
        for (E l : expected()) {
            // System.out.println(mapper.apply(l) + " " + l);
            map.put(mapper.apply(l), l);
        }
        setNext(map, actual().groupBy(mapper), false, false);
        // System.out.println(map);

    }
}
