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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.function.Function;

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
public abstract class CollectionRandomTestBuilder<E> extends AbstractCollectionLikeTestBuilder<ExpectedCollection<E>, E, Collection<E>> {

    /** A set of non standard options. */
    private final EnumSet<NonStandardOption> nonStandardOptions = EnumSet.noneOf(NonStandardOption.class);

    /** The test mapper used for generating the bootstrap collection. */
    private Function<ElementGenerator<E>, ? extends Collection<E>> testMapper = ElementGenerator.randomList();

    protected CollectionRandomTestBuilder() {
        setTestSelectorProvider(TestSelectorProvider.ADD_REMOVE_BALANCE);
    }

    /**
     * Adds the non standard options to this builder.
     * 
     * @param options
     *            the options
     */
    public final void addNonStandardOptions(NonStandardOption... options) {
        nonStandardOptions.addAll(Arrays.asList(options));
    }

    protected abstract Collection<E> createActual(Collection<E> bootstrap);

    /** {@inheritDoc} */
    @Override
    protected final void createTestSet(Bootstrap<ExpectedCollection<E>, Collection<E>> bootstrap) {
        Collection<E> expected = testMapper.apply(getElementGenerator());
        bootstrap.setActual(createActual(expected));
        bootstrap.setExpected(
                new ExpectedCollection<>(getType(), getElementGenerator(), new ArrayList<>(expected), getIllegalElementGenerator(), nonStandardOptions)); // ECLIPSE
    }

    /**
     * Sets the generator that generates the initial tests sets.
     * 
     * @param generator
     */
    public final void setTestDataGenerator(Function<ElementGenerator<E>, ? extends Collection<E>> generator) {
        this.testMapper = requireNonNull(generator);
    }

    /**
     * An enum with non standard options that can be added using {@link #addNonStandardOptions(NonStandardOption...)}
     */
    public enum NonStandardOption {

        /** Indicates that {@link Collection#add(Object)} and {@link Collection#addAll(Collection)} is not supported. */
        ADD_UNSUPPORTED,

        /**
         * Indicates that {@link Collection#contains(Object)} cannot be called with {@code null} and that
         * {@link Collection#containsAll(Collection)} cannot be called with any {@code null} elements.
         */
        NULL_QUERIES_UNSUPPORTED,

        /** Indicates that the collection does not accept {@code null} elements. */
        NULL_UNSUPPORTED,

        /**
         * Indicates that {@link Collection#clear()}, {@link Collection#remove(Object)},
         * {@link Collection#removeAll(Collection)} and {@link Collection#retainAll(Collection)} is not supported.
         */
        REMOVE_UNSUPPORTED,

        /** Indicates that only types determined by <T> can be added or removed. */
        TYPE_RESTRICTION;
    }
}
