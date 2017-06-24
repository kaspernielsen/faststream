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

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;

import io.faststream.sisyphus.annotations.CustomWeight;
import io.faststream.sisyphus.annotations.LifecycleTestMethod;
import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.javautil.CollectionRandomTestBuilder;
import io.faststream.sisyphus.javautil.MapRandomTestBuilder;
import io.faststream.sisyphus.javautil.collection.ExpectedCollection;

/**
 * A test step for {@link Map#values()}.
 * 
 * @param <K>
 *            the type of keys we test
 * @param <V>
 *            the type of values we test
 * @author Kasper Nielsen
 */
public class Values<K, V> extends AbstractRandomMapTestCase<K, V> {

    @RndTest
    @LifecycleTestMethod(false)
    @CustomWeight(0.01)
    public void values() {
        boolean uniqueValues = expected().uniqueValues();
        EnumSet<CollectionRandomTestBuilder.NonStandardOption> set = EnumSet.of(CollectionRandomTestBuilder.NonStandardOption.ADD_UNSUPPORTED);
        if (expected().nonStandardOptions.contains(MapRandomTestBuilder.NonStandardOption.VALUE_TYPE_RESTRICTION)) {
            set.add(CollectionRandomTestBuilder.NonStandardOption.TYPE_RESTRICTION);
        }
        if (expected().nonStandardOptions.contains(MapRandomTestBuilder.NonStandardOption.NULL_VALUES_UNSUPPORTED)) {
            set.add(CollectionRandomTestBuilder.NonStandardOption.NULL_UNSUPPORTED);
        }
        if (expected().nonStandardOptions.contains(MapRandomTestBuilder.NonStandardOption.NULL_QUERIES_UNSUPPORTED)) {
            set.add(CollectionRandomTestBuilder.NonStandardOption.NULL_QUERIES_UNSUPPORTED);
        }
        if (expected().nonStandardOptions.contains(MapRandomTestBuilder.NonStandardOption.REMOVE_UNSUPPORTED)
                || !expected().uniqueValues() /* cannot remove if not all values are unique */) {
            set.add(CollectionRandomTestBuilder.NonStandardOption.REMOVE_UNSUPPORTED);
        }

        ExpectedCollection<V> ec = new ExpectedCollection<>(expected().valueType, expected().generator.values(), expected().map.values(), illegalValues(), set);
        if (uniqueValues) {
            nested(ec, actual().values());
        } else {
            WrappedCollection<V> wrapped = new WrappedCollection<>(actual().values());// Eclipse compiler 1.8
            nested(ec, wrapped);
        }
    }

    // Special unmodifiable collections supporting add
    // @formatter:off
    static class WrappedCollection<T> implements Collection<T> {
        private final Collection<T> wrapper;
        public WrappedCollection(Collection<T> wrapper) {this.wrapper = requireNonNull(wrapper);}
        public boolean add(T e) {return wrapper.add(e);}
        public boolean addAll(Collection<? extends T> c) {return wrapper.addAll(c);}
        public void clear() {throw new UnsupportedOperationException();}
        public boolean contains(Object o) {return wrapper.contains(o);}
        public boolean containsAll(Collection<?> c) {return wrapper.containsAll(c);}
        public boolean equals(Object o) {
            return o instanceof WrappedCollection ?
                    wrapper.equals(((WrappedCollection<?>) o).wrapper) : wrapper.equals(o);
            }
        public int hashCode() {return wrapper.hashCode();}
        public boolean isEmpty() {return wrapper.isEmpty();}
        public Iterator<T> iterator() {return Collections.unmodifiableCollection(wrapper).iterator();}
        public boolean remove(Object o) {throw new UnsupportedOperationException();}
        public boolean removeAll(Collection<?> c) {throw new UnsupportedOperationException();}
        public boolean retainAll(Collection<?> c) {throw new UnsupportedOperationException();}
        public int size() {return wrapper.size();}
        public Object[] toArray() {return wrapper.toArray();}
        public <S> S[] toArray(S[] a) {return wrapper.toArray(a);}
    }
    // @formatter:on
}
