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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import io.faststream.sisyphus.generators.EntryGenerator;
import io.faststream.sisyphus.javautil.map.ExpectedMap;
import io.faststream.sisyphus.javautil.spi.AbstractMapLikeTestBuilder;
import io.faststream.sisyphus.spi.method.TestSelectorProvider;

/**
 * @param <K>
 *            the type of keys we test
 * @param <V>
 *            the type of values we test
 * @author Kasper Nielsen
 */
public abstract class MapRandomTestBuilder<K, V> extends AbstractMapLikeTestBuilder<ExpectedMap<K, V>, Map<K, V>, K, V> {

    /** A set of non standard options. */
    private final EnumSet<NonStandardOption> nonStandardOptions = EnumSet.noneOf(NonStandardOption.class);

    /** The test mapper used for generating the bootstrap map. */
    private Function<EntryGenerator<K, V>, ? extends Map<K, V>> testMapper = EntryGenerator.randomMap();

    protected MapRandomTestBuilder() {
        setTestSelectorProvider(TestSelectorProvider.ADD_REMOVE_BALANCE);
    }

    protected MapRandomTestBuilder(Class<K> keyType, Class<V> valueType) {
        super(keyType, valueType);
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

    protected abstract Map<K, V> createActual(Map<K, V> bootstrap);

    @Override
    protected final void createTestSet(Bootstrap<ExpectedMap<K, V>, Map<K, V>> factory) {
        Map<K, V> bootstrap = testMapper.apply(getGenerator());
        factory.setActual(createActual(bootstrap));
        factory.setExpected(new ExpectedMap<>(getKeyType(), getValueType(), getGenerator(), new LinkedHashMap<>(bootstrap), getIllegalKeyGenerator(),
                getIllegalValueGenerator(), nonStandardOptions));
    }

    /**
     * Sets the generator that generates the initial tests sets.
     * 
     * @param generator
     */
    public final void setTestDataGenerator(Function<EntryGenerator<K, V>, ? extends Map<K, V>> generator) {
        this.testMapper = requireNonNull(generator);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <K, V> MapRandomTestBuilder<K, V> from(Class<? extends Map> mapImplementation) {
        return (MapRandomTestBuilder<K, V>) from(mapImplementation, Object.class, Object.class);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <K, V> MapRandomTestBuilder<K, V> from(Class<? extends Map> mapImplementation, Class<?> keyType, Class<?> valueType) {
        try {
            final Constructor<? extends Map> constructor = mapImplementation.getConstructor(Map.class);
            return new MapRandomTestBuilder<K, V>((Class) keyType, (Class) valueType) {
                protected Map<K, V> createActual(Map<K, V> bootstrap) {
                    try {
                        return constructor.newInstance(bootstrap);
                    } catch (Exception e) {
                        throw new Error("Could not instantiate Map", e);
                    }
                }
            };
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Specified map class does not contain a public constructor taking a single Map argument");
        }
    }

    /**
     * An enum with non standard options that can be added using {@link #addNonStandardOptions(NonStandardOption...)}
     */
    public static enum NonStandardOption {
        /** Indicates that only key types determined by <K> can be added or removed. */
        KEY_TYPE_RESTRICTION,

        /** Indicates that the map does not accept {@code null} keys. */
        NULL_KEYS_UNSUPPORTED,

        /**
         * Indicates that {@link Map#containsKey(Object)}, {@link Map#containsValue(Object)} {@link Map#get(Object)}
         * cannot be called with {@code null}.
         */
        NULL_QUERIES_UNSUPPORTED,

        /** Indicates that the map does not accept {@code null} values. */
        NULL_VALUES_UNSUPPORTED,

        /** Indicates that {@link Map#put(Object, Object)} and {@link Map#putAll(Map)} is not supported. */
        PUT_UNSUPPORTED,

        /** Indicates that {@link Map#clear()} and {@link Map#remove(Object)}, is not supported. */
        REMOVE_UNSUPPORTED,

        /** Indicates that only value types determined by <V> can be added. */
        VALUE_TYPE_RESTRICTION;
    }
}
