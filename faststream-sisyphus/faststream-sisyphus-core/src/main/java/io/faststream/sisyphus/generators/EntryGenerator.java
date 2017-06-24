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
package io.faststream.sisyphus.generators;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertTrue;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import io.faststream.sisyphus.TestScale;
import io.faststream.sisyphus.util.RandomSource;

/**
 * @param <K>
 *            the type of keys we generate
 * @param <V>
 *            the type of values we generate
 * @author Kasper Nielsen
 */
public abstract class EntryGenerator<K, V> implements Supplier<Map.Entry<K, V>> {

    public ElementGenerator<Map.Entry<K, V>> entries() {
        return new ElementGenerator<Map.Entry<K, V>>() {
            public Map.Entry<K, V> get() {
                return EntryGenerator.this.get();
            }
        };
    }

    public final ElementGenerator<K> keys() {
        return new ElementGenerator<K>() {
            public K get() {
                return EntryGenerator.this.get().getKey();
            }
        };
    }

    int maximumSize() {
        return Integer.MAX_VALUE;
    }

    public final Mix mix() {
        return new Mix();
    }

    public final NextMap nextMap() {
        if (RandomSource.current().nextInt(10) == 0) {
            return nextMap(0);
        }
        return nextMap(0, TestScale.defaultScale().getScale() * 2);
    }

    public final NextMap nextMap(int size) {
        return new NextMap(Math.min(size, maximumSize()));
    }

    public final NextMap nextMap(int minimumSize, int maximumSize) {
        return nextMap(minimumSize + RandomSource.current().nextInt(maximumSize - minimumSize));
    }

    EntryGenerator<K, V> uniqueEntries() {
        return null;
    }

    public final ElementGenerator<V> values() {
        return new ElementGenerator<V>() {
            public V get() {
                return EntryGenerator.this.get().getValue();
            }
        };
    }

    public static <K, V> Function<EntryGenerator<K, V>, Map<K, V>> randomMap() {
        return e -> e.nextMap().create();
    }

    /** Creates a mix of data. */
    public final class Mix {
        public EntryGenerator<K, V> mixWith(final float selectThisGenerator, final Map<? extends K, ? extends V> alternative) {
            assertTrue(selectThisGenerator >= 0 && selectThisGenerator < 1);
            requireNonNull(alternative);
            return new EntryGenerator<K, V>() {
                @SuppressWarnings("unchecked")
                @Override
                public Map.Entry<K, V> get() {
                    RandomSource rnd = RandomSource.current();
                    if (alternative.isEmpty() || rnd.nextFloat() < selectThisGenerator) {
                        return EntryGenerator.this.get();
                    } else {
                        return (Map.Entry<K, V>) rnd.nextElement(new ArrayList<>(alternative.entrySet()));
                    }
                }
            };
        }

        public EntryGenerator<K, V> mixWithNulls(final float selectThisGenerator, final boolean mixWithNullKeys, final boolean mixWithNullValues) {
            assertTrue(selectThisGenerator >= 0 && selectThisGenerator < 1);
            if (!(mixWithNullKeys || mixWithNullValues)) {
                return EntryGenerator.this;
            }
            return new EntryGenerator<K, V>() {
                @Override
                public Map.Entry<K, V> get() {
                    RandomSource rnd = RandomSource.current();
                    if (rnd.nextFloat() < selectThisGenerator) {
                        return EntryGenerator.this.get();
                    } else {
                        boolean nullKey = mixWithNullKeys && rnd.nextBoolean();
                        boolean nullValue = mixWithNullValues && rnd.nextBoolean();
                        return new AbstractMap.SimpleImmutableEntry<>(nullKey ? null : EntryGenerator.this.get().getKey(),
                                nullValue ? null : EntryGenerator.this.get().getValue());
                    }
                }
            };
        }
    }

    /** Transforms data into a map. */
    public final class NextMap {
        private final int size;

        NextMap(int size) {
            this.size = size;
        }

        public Map<K, V> create() {
            return map(size);
        }

        protected Map<K, V> map(int size) {
            HashMap<K, V> map = new HashMap<>(size);
            while (map.size() < size) {
                Map.Entry<K, V> e = get();
                map.put(e.getKey(), e.getValue());
            }
            return map;
        }

    }
}
