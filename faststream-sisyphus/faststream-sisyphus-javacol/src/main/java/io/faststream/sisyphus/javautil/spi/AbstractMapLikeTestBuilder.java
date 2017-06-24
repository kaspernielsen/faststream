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
import io.faststream.sisyphus.generators.EntryGenerator;
import io.faststream.sisyphus.generators.TypedElementSupplier;

/**
 * @param <E>
 *            the expected map type
 * @param <A>
 *            the actual map type
 * @param <K>
 *            the type of keys we generate
 * @param <V>
 *            the type of values we generate
 * @author Kasper Nielsen
 */
public abstract class AbstractMapLikeTestBuilder<E extends Expected, A, K, V> extends TestBuilder<E, A> {

    private EntryGenerator<K, V> generator;

    private ElementGenerator<K> illegalKeyGenerator;

    private ElementGenerator<V> illegalValueGenerator;

    private final Class<K> keyType;

    private final Class<V> valueType;

    @SuppressWarnings("unchecked")
    protected AbstractMapLikeTestBuilder() {
        // keyType = (Class<K>) ClassTestUtil.getTypeOfArgument(AbstractMapLikeTestBuilder.class, getClass(), 2);
        // valueType = (Class<V>) ClassTestUtil.getTypeOfArgument(AbstractMapLikeTestBuilder.class, getClass(), 3);
        keyType = (Class<K>) TypeToken.of(getClass()).resolveType(AbstractMapLikeTestBuilder.class.getTypeParameters()[2]).getRawType();
        valueType = (Class<V>) TypeToken.of(getClass()).resolveType(AbstractMapLikeTestBuilder.class.getTypeParameters()[3]).getRawType();
        setGenerator(TypedElementSupplier.random(keyType), TypedElementSupplier.random(valueType));
    }

    protected AbstractMapLikeTestBuilder(Class<K> keyType, Class<V> valueType) {
        this.keyType = requireNonNull(keyType);
        this.valueType = requireNonNull(valueType);
        setGenerator(TypedElementSupplier.random(keyType), TypedElementSupplier.random(valueType));
    }

    public final EntryGenerator<K, V> getGenerator() {
        return generator;
    }

    public final ElementGenerator<K> getIllegalKeyGenerator() {
        return illegalKeyGenerator;
    }

    public final ElementGenerator<V> getIllegalValueGenerator() {
        return illegalValueGenerator;
    }

    public final Class<K> getKeyType() {
        return keyType;
    }

    public final Class<V> getValueType() {
        return valueType;
    }

    public final void setGenerator(EntryGenerator<K, V> generator) {
        this.generator = requireNonNull(generator);
    }

    public final void setGenerator(TypedElementSupplier<K> keySource, TypedElementSupplier<V> valueSource) {
        this.generator = keySource.mapTo(valueSource);
    }

    public final void setIllegalValueGenerator(ElementGenerator<V> illegalValueGenerator) {
        this.illegalValueGenerator = illegalValueGenerator;
    }

    /** Non standard options. */
    public static enum NonStandardOption {
        ADD_UNSUPPORTED,

        NULL_QUERIES_UNSUPPORTED,

        NULL_VALUES_UNSUPPORTED,

        REMOVE_UNSUPPORTED,

        TYPE_LIMITED;
    }
}
