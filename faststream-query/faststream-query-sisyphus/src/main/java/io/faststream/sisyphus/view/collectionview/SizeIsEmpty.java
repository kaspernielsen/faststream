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

import static org.junit.Assert.fail;

import io.faststream.query.util.view.CollectionView;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link CollectionView#size()} and {@link CollectionView#isEmpty()} operation.
 *
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class SizeIsEmpty<E> extends AbstractCollectionViewRandomTestCase<E> {

    @RndTest
    public void size() {
        long actualSize = actual().size();
        long expectedSize = expected().size();
        if (expectedSize != actualSize) {
            fail("Expected a CollectionView with " + expectedSize + " elements but collectionView.size() returned "
                    + actualSize);
        }
    }

    @RndTest
    public void isEmpty() {
        boolean isEmpty = actual().isEmpty();
        if (isEmpty != expected().isEmpty()) {
            if (isEmpty) {
                fail("Found an empty CollectionView but expected a CollectionView with the following elements "
                        + expected().expectedString());
            } else {
                fail("Expected a CollectionView with no elements, but contained the following elements "
                        + actual().toList());
            }
        }
    }
}
