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
package io.faststream.sisyphus.javautil.spi;

import static java.util.Objects.requireNonNull;

import com.google.common.reflect.TypeToken;

import io.faststream.sisyphus.builder.TestBuilder;
import io.faststream.sisyphus.expected.Expected;
import io.faststream.sisyphus.generators.ElementGenerator;
import io.faststream.sisyphus.generators.TypedElementSupplier;

/**
 * @param <E>
 *            the expected collection
 * @param <T>
 *            the type of elements
 * @param <A>
 *            the actual collection
 * 
 * @author Kasper Nielsen
 */
public abstract class AbstractCollectionLikeTestBuilder<E extends Expected, T, A> extends TestBuilder<E, A> {

    private TypedElementSupplier<T> elementSource;

    private ElementGenerator<T> illegalElementGenerator;

    private final Class<T> type;

    @SuppressWarnings("unchecked")
    protected AbstractCollectionLikeTestBuilder() {
        // type = (Class<T>) ClassTestUtil.getTypeOfArgument(AbstractMapLikeTestBuilder.class, getClass(), 1);
        type = (Class<T>) TypeToken.of(getClass()).resolveType(AbstractCollectionLikeTestBuilder.class.getTypeParameters()[1]).getRawType();
        setElementGenerator(TypedElementSupplier.random(type));
    }

    public final Class<T> getType() {
        return type;
    }

    public final TypedElementSupplier<T> getElementGenerator() {
        return elementSource;
    }

    public final ElementGenerator<T> getIllegalElementGenerator() {
        return illegalElementGenerator;
    }

    public final void setElementGenerator(TypedElementSupplier<T> elementSource) {
        this.elementSource = requireNonNull(elementSource);
    }

    public final void setIllegalElementGenerator(ElementGenerator<T> illegalElementGenerator) {
        this.illegalElementGenerator = illegalElementGenerator;
    }
}
