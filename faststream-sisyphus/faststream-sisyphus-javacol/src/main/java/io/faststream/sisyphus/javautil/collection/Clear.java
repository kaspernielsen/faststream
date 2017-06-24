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
package io.faststream.sisyphus.javautil.collection;

import java.util.Collection;

import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.LifecycleTestMethod;
import io.faststream.sisyphus.annotations.Remove;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * A test step for {@link Collection#clear()}.
 * 
 * @param <E>
 *            the elements in the collection
 * 
 * @author Kasper Nielsen
 */
public class Clear<E> extends AbstractRandomCollectionTestCase<E> {

    @Remove
    @RndTest
    @LifecycleTestMethod(false)
    public void clear() {
        assumeRemoveSupported();

        actual().clear();
        expected().clear();
    }

    @FailWith(UnsupportedOperationException.class)
    public void clearUnsupported() {
        assumeRemoveUnsupported();

        actual().clear();
    }
}
