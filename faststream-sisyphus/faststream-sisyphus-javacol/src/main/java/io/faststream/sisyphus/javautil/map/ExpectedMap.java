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
package io.faststream.sisyphus.javautil.map;

import static java.util.Objects.requireNonNull;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;

import io.faststream.sisyphus.expected.Expected;
import io.faststream.sisyphus.generators.ElementGenerator;
import io.faststream.sisyphus.generators.EntryGenerator;
import io.faststream.sisyphus.generators.TypedElementSupplier;
import io.faststream.sisyphus.javautil.MapRandomTestBuilder.NonStandardOption;

/**
 * @param <K>
 *            the type of keys we test
 * @param <V>
 *            the type of values we test
 * @author Kasper Nielsen
 */
public class ExpectedMap<K, V> extends Expected {
    final Map<K, V> map;

    final ElementGenerator<K> illegalKeys;

    final ElementGenerator<V> illegalValues;

    final ElementGenerator<Object> incompatibleKeyTypes;

    final ElementGenerator<Object> incompatibleValueTypes;

    final EntryGenerator<K, V> mix;

    final EnumSet<NonStandardOption> nonStandardOptions;

    final EntryGenerator<K, V> generator;

    final Class<K> keyType;

    final Class<V> valueType;

    public ExpectedMap(Class<K> keyType, Class<V> valueType, EntryGenerator<K, V> generator, Map<K, V> expected, ElementGenerator<K> illegalKeyGenerator,
            ElementGenerator<V> illegalValueGenerator, EnumSet<NonStandardOption> nonStandardOptions) {
        this.keyType = requireNonNull(keyType);
        this.valueType = requireNonNull(valueType);
        this.generator = requireNonNull(generator);
        this.nonStandardOptions = requireNonNull(nonStandardOptions);
        this.incompatibleKeyTypes = nonStandardOptions.contains(NonStandardOption.KEY_TYPE_RESTRICTION)
                ? TypedElementSupplier.random(keyType).incompatibleType() : null;
        this.incompatibleValueTypes = nonStandardOptions.contains(NonStandardOption.VALUE_TYPE_RESTRICTION)
                ? TypedElementSupplier.random(valueType).incompatibleType() : null;
        this.illegalKeys = illegalKeyGenerator;
        this.illegalValues = illegalValueGenerator;
        this.map = expected;

        EntryGenerator<K, V> m = generator.mix().mixWith(0.5f, expected);
        mix = m.mix().mixWithNulls(0.99f, !nonStandardOptions.contains(NonStandardOption.NULL_KEYS_UNSUPPORTED),
                !nonStandardOptions.contains(NonStandardOption.NULL_VALUES_UNSUPPORTED));
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public V get(Object key) {
        return map.get(key);
    }

    public V put(K key, V value) {
        return map.put(key, value);
    }

    public V remove(Object key) {
        return map.remove(key);
    }

    public int size() {
        return map.size();
    }

    public void clear() {
        map.clear();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * Returns true if all values are unique. Otherwise returns false.
     */
    public boolean uniqueValues() {
        return new HashSet<>(map.values()).size() == map.size();
    }
}
