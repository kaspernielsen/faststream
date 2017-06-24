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
package io.faststream.sisyphus.javautil;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import io.faststream.sisyphus.generators.ElementGenerator;
import io.faststream.sisyphus.javautil.collection.ExpectedCollection;
import io.faststream.sisyphus.javautil.spi.AbstractCollectionLikeTestBuilder;
import io.faststream.sisyphus.spi.method.TestSelectorProvider;

/**
 * A random test builder for testing a {@link Collection}.
 * 
 * @param <E>
 *            the type of elements we test
 * @author Kasper Nielsen
 */
public abstract class StreamRandomTestBuilder<E> extends AbstractCollectionLikeTestBuilder<ExpectedCollection<E>, E, Stream<E>>

{

    /** The test mapper used for generating the bootstrap collection. */
    Function<ElementGenerator<E>, ? extends Collection<E>> testMapper = ElementGenerator.randomList();

    protected StreamRandomTestBuilder() {
        setTestSelectorProvider(TestSelectorProvider.ADD_REMOVE_BALANCE);
    }

    protected abstract Stream<E> createActual(Collection<E> bootstrap);

    /** {@inheritDoc} */
    @Override
    protected final void createTestSet(Bootstrap<ExpectedCollection<E>, Stream<E>> bootstrap) {
        Collection<E> expected = testMapper.apply(getElementGenerator());
        bootstrap.setActual(createActual(expected));
        // new ExpectedStream<>()
        // bootstrap.setExpected(new ExpectedCollection<>(getType(), getElementGenerator(), new ArrayList<>(expected),
        // getIllegalElementGenerator(), nonStandardOptions)); // ECLIPSE
        // Stream<E> bootstrap = testMapper.apply(getElementGenerator());
    }

    /**
     * Sets the generator that generates the initial tests sets.
     * 
     * @param generator
     */
    public final void setTestDataGenerator(Function<ElementGenerator<E>, ? extends Collection<E>> generator) {
        this.testMapper = requireNonNull(generator);
    }
    // @Override
    // protected final void createTestSet(RandomTestSetFactory<ExpectedCollection<E>, Collection<E>> factory) {
    // Collection<E> bootstrap = testMapper.apply(getElementGenerator());
    // factory.setActual(createActual(bootstrap));
    // factory.setExpected(new ExpectedCollection<>(getType(), getElementGenerator(), new ArrayList<>(bootstrap),
    // getIllegalElementGenerator(), nonStandardOptions));
    // }
    //
    // /**
    // * Sets the generator that generates the initial tests sets.
    // *
    // * @param generator
    // */
    // public final void setTestDataGenerator(Function<ElementSupplier<E>, ? extends Stream<E>> generator) {
    // this.testMapper = requireNonNull(generator);
    // }
}
