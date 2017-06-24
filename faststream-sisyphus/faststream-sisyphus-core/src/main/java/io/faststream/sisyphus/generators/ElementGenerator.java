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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.faststream.sisyphus.TestScale;
import io.faststream.sisyphus.util.RandomSource;

/**
 * A class that is responsible for creating various test elements of a specific type.
 * 
 * @param <E>
 *            the type of elements that is generated
 * 
 * @author Kasper Nielsen
 */
public abstract class ElementGenerator<E> implements Iterable<E>, Supplier<E> {

    @Override
    public final Iterator<E> iterator() {
        return new Iterator<E>() {

            public E get() {
                return ElementGenerator.this.get();
            }

            public boolean hasNext() {
                return true;
            }

            public E next() {
                return get();
            }

            public void remove() {
                throw new UnsupportedOperationException("cannot remove elements");
            }
        };
    }

    /**
     * Returns new entry supplier.
     * 
     * @param function
     *            the function that maps elements produced by this supplier to values.
     * @return a new entry supplier
     */
    public final <V> EntryGenerator<E, V> mapTo(final Function<E, V> function) {
        return new EntryGenerator<E, V>() {
            @Override
            public Entry<E, V> get() {
                E t = ElementGenerator.this.get();
                return new AbstractMap.SimpleImmutableEntry<>(t, function.apply(t));
            }
        };
    }

    public <V> EntryGenerator<E, V> mapTo(Supplier<V> supplier) {
        return mapTo(supplier, Integer.MAX_VALUE);
    }

    <V> EntryGenerator<E, V> mapTo(final Supplier<V> supplier, final int maximumSize) {
        return new EntryGenerator<E, V>() {
            @Override
            public Entry<E, V> get() {
                return new AbstractMap.SimpleImmutableEntry<>(ElementGenerator.this.get(), supplier.get());
            }

            @Override
            int maximumSize() {
                return maximumSize;
            }
        };
    }

    public final Mix mix() {
        return new Mix();
    }

    public final NextList nextList() {
        if (RandomSource.current().nextInt(20) == 0) {
            return nextList(0);
        }
        return nextList(0, TestScale.defaultScale().getScale() * 2);
    }

    /**
     * A prefix operation that returns a {@link NextList} object that can be used to create various types of lists.
     * 
     * @param size
     *            the default number of elements in the lists being created
     * @return
     */
    public final NextList nextList(int size) {
        return new NextList(size);
    }

    public final NextList nextList(int minimumSize, int maximumSize) {
        return nextList(minimumSize + RandomSource.current().nextInt(maximumSize - minimumSize));
    }

    public final NextSet nextSet(int size) {
        return new NextSet(size);
    }

    /**
     * Returns a new {@link ElementGenerator} that only returns unique elements.
     */
    public final ElementGenerator<E> unique() {
        return this instanceof ElementGenerator.Unique ? this : new Unique();
    }

    public static <E> Function<ElementGenerator<E>, List<E>> randomList() {
        return e -> e.nextList().create();
    }

    public static <E> Function<ElementGenerator<E>, Stream<E>> randomStream() {
        return e -> e.nextList().create().stream();
    }

    /** Creates a mix of data. */
    public final class Mix {
        public ElementGenerator<E> mixWith(final float selectThisGenerator, final Collection<? extends E> alternative) {
            assertTrue(selectThisGenerator >= 0 && selectThisGenerator < 1);
            requireNonNull(alternative);
            return new ElementGenerator<E>() {
                @Override
                public E get() {
                    RandomSource rnd = RandomSource.current();
                    if (alternative.isEmpty() || rnd.nextFloat() < selectThisGenerator) {
                        return ElementGenerator.this.get();
                    } else {
                        return rnd.nextElement(alternative);
                    }
                }
            };
        }

        public ElementGenerator<E> mixWith(final float selectThisGenerator, final E alternative) {
            assertTrue(selectThisGenerator >= 0 && selectThisGenerator < 1);
            return new ElementGenerator<E>() {
                @Override
                public E get() {
                    RandomSource rnd = RandomSource.current();
                    if (rnd.nextFloat() < selectThisGenerator) {
                        return ElementGenerator.this.get();
                    } else {
                        return alternative;
                    }
                }
            };
        }
    }

    /** Transforms the data to a list. */
    public final class NextList {
        private final int size;

        NextList(int size) {
            this.size = size;
        }

        /**
         * Create a new list of the predefined size.
         * 
         * @return a new list of the predefined size
         */
        public List<E> create() {
            return list(size);
        }

        /**
         * Returns a new list of the predefined size including the specified element at a random place.
         * 
         * @param element
         *            an element to add
         * @return a new list of the predefined size + 1
         */
        @SuppressWarnings("unchecked")
        public List<Object> include(Object element) {
            if (size == 0) {
                return Collections.singletonList(element);
            }
            List<Object> l = (List<Object>) list(size - 1);
            l.add(element);
            Collections.shuffle(l, RandomSource.current());
            return l;
        }

        @SuppressWarnings("unchecked")
        public List<E> includeNull() {
            return (List<E>) include(null);
        }

        @SuppressWarnings("unchecked")
        public List<Object> includeSome(Iterable<?> include) {
            requireNonNull(include);
            if (size == 0) {
                return Collections.singletonList((Object) include.iterator().next());
            }
            int i = RandomSource.current().nextInt(1, size);
            List<Object> l = (List<Object>) list(size - i);
            Iterator<?> iter = include.iterator();
            for (int j = 0; j < i; j++) {
                l.add(iter.next());
            }
            Collections.shuffle(l, RandomSource.current());
            return l;
        }

        List<E> list(int size) {
            ArrayList<E> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                list.add(ElementGenerator.this.get());
            }
            return list;
        }

        /**
         * Returns a new list with a single element.
         * 
         * @return a new singleton list containing 1 element
         */
        public List<E> singleton() {
            return Collections.singletonList(get());
        }
    }

    /** Transforms the data to a set. */
    public final class NextSet {
        private final int size;

        NextSet(int size) {
            this.size = size;
        }

        public Set<E> create() {
            return set(size);
        }

        protected Set<E> set(int size) {
            HashSet<E> set = new HashSet<>(size);
            while (set.size() < size) {
                set.add(get());
            }
            return set;
        }

    }

    /**
     * 
     * @author Kasper Nielsen
     */
    final class Unique extends ElementGenerator<E> {
        final HashSet<E> seen = new HashSet<>();

        @Override
        public E get() {
            for (int i = 0; i < 10000; i++) {
                E t = ElementGenerator.this.get();
                if (!seen.contains(t)) {
                    seen.add(t);
                    return t;
                }
            }
            throw new AssertionError("Looks like this test is running an infinite loop");
        }
    }
}
