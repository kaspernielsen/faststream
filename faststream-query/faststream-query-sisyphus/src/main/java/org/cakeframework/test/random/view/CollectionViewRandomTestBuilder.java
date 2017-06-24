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
package org.cakeframework.test.random.view;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.cakeframework.test.random.view.spi.ExpectedCollectionView;
import org.cakeframework.util.view.CollectionView;

import io.faststream.sisyphus.generators.ElementGenerator;
import io.faststream.sisyphus.javautil.spi.AbstractCollectionLikeTestBuilder;

/**
 * @param <E>
 *            the type of elements
 * @author Kasper Nielsen
 */
public abstract class CollectionViewRandomTestBuilder<E> extends
        AbstractCollectionLikeTestBuilder<ExpectedCollectionView<E>, E, CollectionView<E>> {
    private Function<ElementGenerator<E>, ? extends List<E>> testMapper = ElementGenerator.randomList();

    @Override
    protected final void createTestSet(Bootstrap<ExpectedCollectionView<E>, CollectionView<E>> factory) {
        List<E> bootstrap = Collections.unmodifiableList(testMapper.apply(getElementGenerator()));
        factory.setActual(createTestStructure(bootstrap));
        factory.setExpected(new ExpectedCollectionView<>(bootstrap, true));// hmm why true?
    }

    protected abstract CollectionView<E> createTestStructure(List<E> bootstrap);

    public void setTestDataGenerator(Function<ElementGenerator<E>, ? extends List<E>> testMapper) {
        this.testMapper = requireNonNull(testMapper);
    }
}
