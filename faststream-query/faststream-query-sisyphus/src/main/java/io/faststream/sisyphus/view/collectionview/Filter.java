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

import java.util.function.Predicate;

import io.faststream.query.util.view.CollectionView;
import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.HashcodeModPredicate;

/**
 * Test the {@link CollectionView#filter(Predicate)}, {@link CollectionView#filterNulls()} and
 * {@link CollectionView#filterOnType(Class)} operation.
 *
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class Filter<E> extends AbstractCollectionViewRandomTestCase<E> {

    @RndTest
    public void filter() {
        final Predicate<? super E> p;
        if (isOrdered() && expected().size() > 0 && random().nextDouble() < .2) {
            // Tests a previous error we had
            Object e = expected().first();
            p = e == null ? el -> el != null : el -> !el.equals(e);
        } else {
            // creates a _deterministic_ predicate filters elements based on their hashcode
            p = new HashcodeModPredicate<>(random().nextInt(2, 14));
        }
        setNested(expected().filter(p), actual().filter(p));
    }

    @RndTest
    public void filterNulls() {
        setNested(expected().filter(e -> e != null), actual().filterNulls());
    }

    @RndTest
    @SuppressWarnings("unchecked")
    public void filterOnType() {
        // TODO do we want to change the signature of
        // <U extends E> CollectionView<U> filterOnType(Class<U> clazz); to
        // <U> CollectionView<U> filterOnType(Class<U> clazz);
        Class<E> cl = (Class<E>) randomType(expected()); // use a random type
        setNested(expected().filter(e -> cl.isInstance(e)), actual().filterOnType(cl));
    }
}
