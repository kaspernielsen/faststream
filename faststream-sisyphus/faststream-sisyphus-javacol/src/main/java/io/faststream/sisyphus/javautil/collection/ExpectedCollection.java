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
package io.faststream.sisyphus.javautil.collection;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

import io.faststream.sisyphus.generators.ElementGenerator;
import io.faststream.sisyphus.generators.TypedElementSupplier;
import io.faststream.sisyphus.javautil.CollectionRandomTestBuilder.NonStandardOption;
import io.faststream.sisyphus.javautil.iterable.ExpectedIterable;
import io.faststream.sisyphus.javautil.stream.object.ExpectedStream;

/**
 * @param <E>
 *            the elements in the expected collection
 * 
 * @author Kasper Nielsen
 */
public class ExpectedCollection<E> extends ExpectedIterable<E> {

    final Collection<E> delegate;

    final ElementGenerator<E> illegalElements;

    final ElementGenerator<Object> incompatibleTypes;

    final ElementGenerator<E> mix;

    final EnumSet<NonStandardOption> nonStandardOptions;

    final ElementGenerator<E> source;

    final Class<E> type;

    public ExpectedCollection(Class<E> type, ElementGenerator<E> source, Collection<E> expected, ElementGenerator<E> illegalElements,
            EnumSet<NonStandardOption> nonStandardOptions) {
        this.type = requireNonNull(type);
        this.source = requireNonNull(source);
        this.nonStandardOptions = requireNonNull(nonStandardOptions);
        this.incompatibleTypes = nonStandardOptions.contains(NonStandardOption.TYPE_RESTRICTION) ? TypedElementSupplier.random(type).incompatibleType() : null;
        this.illegalElements = illegalElements;

        final boolean useNull = !nonStandardOptions.contains(NonStandardOption.NULL_UNSUPPORTED);

        ElementGenerator<E> m = source.mix().mixWith(0.5f, expected);
        mix = useNull ? m.mix().mixWith(0.99f, (E) null) : m;
        this.delegate = requireNonNull(expected);
    }

    public boolean add(E e) {
        return delegate.add(e);
    }

    public boolean addAll(Collection<? extends E> c) {
        return delegate.addAll(c);
    }

    public void clear() {
        delegate.clear();
    }

    public final boolean contains(Object o) {
        return delegate.contains(o);
    }

    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    public ExpectedStream<E> toStream() {
        return ExpectedStream.create(new ArrayList<>(delegate), false, false);
    }

    public boolean remove(Object o) {
        return delegate.remove(o);
        // Iterator<E> it = iterator();
        // while (it.hasNext()) {
        // if (Objects.equals(it.next(), null)) {
        // it.remove();
        // return true;
        // }
        // }
        // return false;
    }

    public final int size() {
        return delegate.size();
    }

    public final Object[] toArray() {
        return delegate.toArray();
    }

    public final boolean allIsNull() {
        for (E e : delegate) {
            if (e != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void assumeRemovable() {
        // TODO Auto-generated method stub
    }

    @Override
    protected boolean isOrdered() {
        // TODO Auto-generated method stub
        return false;
    }
}
