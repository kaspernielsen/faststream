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

import java.lang.reflect.Array;
import java.util.function.Supplier;

/**
 * @param <E>
 *            the type of elements that is generated
 * @author Kasper Nielsen
 */
public class TypedElementSupplier<E> extends ElementGenerator<E> {

    private final InternalGenerators factory;

    private final Class<E> type;

    TypedElementSupplier(InternalGenerators factory, Class<E> type) {
        this.factory = requireNonNull(factory);
        this.type = InternalGenerators.boxClass(requireNonNull(type));
    }

    @SuppressWarnings("unchecked")
    public E get() {
        return (E) factory.getWrapper(type).factory.get();
    }

    public Class<E> getType() {
        return type;
    }

    /**
     * Returns a random object that is not assignable to {@link #getType()}.
     * 
     * @return
     */
    public ElementGenerator<Object> incompatibleType() {
        return factory.incompatibleType(type);
    }

    @Override
    public <V> EntryGenerator<E, V> mapTo(Supplier<V> generator) {
        if (type == Boolean.class) {
            return mapTo(generator, 2);
        } else if (type == Byte.class) {
            return mapTo(generator, 256);
        }
        return super.mapTo(generator);
    }

    public Object[] randomAssignableArray(int size) {
        return (Object[]) Array.newInstance(randomAssignableType(), size);
    }

    /**
     * Returns a random type that is assignable to {@link #getType()}.
     * 
     * @return a random type that is assignable to the type returned by getType()
     */
    public Class<? super E> randomAssignableType() {
        return factory.randomAssignableType(type);
    }

    /**
     * NOTE: Be very careful when using this with multiple threads. As different thread interleaving might produce
     * different numbers
     * 
     * @param type
     * @return
     */
    public static final <T> TypedElementSupplier<T> incremental(Class<T> type) {
        return new TypedElementSupplier<>(InternalGenerators.INCREMENTAL, type);
    }

    public static final <T> TypedElementSupplier<T> random(Class<T> type) {
        return new TypedElementSupplier<>(InternalGenerators.RANDOM, type);
    }
}
//
// DataGenerator<T> excluding(@SuppressWarnings("unchecked") T... excludeTheseElements) {
// final List<T> list = Arrays.asList(excludeTheseElements);
// return new DataGenerator<T>() {
// @Override
// public T next() {
// for (int i = 0; i < 10000; i++) {
// T t = next();
// if (!list.contains(t)) {
// return t;
// }
// }
// throw new AssertionError("Looks like the test is running an infinite loop");
// }
// };
// }
