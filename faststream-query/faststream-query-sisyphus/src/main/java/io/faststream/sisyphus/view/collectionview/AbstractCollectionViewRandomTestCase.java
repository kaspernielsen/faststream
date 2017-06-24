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

import org.faststream.sisyphus.view.spi.ExpectedCollectionView;

import io.faststream.query.util.view.CollectionView;
import io.faststream.sisyphus.view.AbstractViewRandomTestCase;

/**
 * An abstract test step for {@link CollectionView}.
 *
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
abstract class AbstractCollectionViewRandomTestCase<E> extends
AbstractViewRandomTestCase<ExpectedCollectionView<E>, CollectionView<E>> {

    public E getBase() {
        return null; // TODO Auto-generated method stub
    }

    public final boolean isOrdered() {
        return expected().isOrdered();
    }
}
