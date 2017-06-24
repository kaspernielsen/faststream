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
package io.faststream.sisyphus.stubs;

import java.util.function.BiPredicate;

/**
 * @param <K>
 *            the type of keys we filter
 * @param <V>
 *            the type of values we filter
 * @author Kasper Nielsen
 */
public class ModBinaryPredicate<K, V> implements BiPredicate<K, V> {
    final int i;

    public ModBinaryPredicate(int i) {
        this.i = i;
    }

    public String toString() {
        return "ModBinaryPredicate.from(" + i + ")";
    }

    @Override
    public boolean test(K a, V b) {
        return a != null && b != null && (longValue(a) % i == 0 || longValue(b) % i == 0);
    }

    static long longValue(Object o) {
        if (o instanceof Number) {
            return ((Number) o).longValue();
        }
        return o == null ? 0 : o.hashCode();
    }

    public static <K, V> BiPredicate<K, V> from(int mod) {
        return new ModBinaryPredicate<>(mod);
    }
};
