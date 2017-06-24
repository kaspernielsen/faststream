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
package org.cakeframework.internal.db.query.util.tree;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * @param <E>
 *            the type of elements we create
 * 
 * @author Kasper Nielsen
 */
public abstract class AbstractIterator<E> implements Iterator<E> {
    E next;

    protected AbstractIterator() {
        next = next0(null);
    }

    protected AbstractIterator(E first) {
        next = first;
    }

    public Iterable<E> asIterable() {
        return new Iterable<E>() {
            public Iterator<E> iterator() {
                // TODO should warn about not being reusable, in other words can only be invoked once.
                return AbstractIterator.this;
            }
        };
    }

    public AbstractIterator<E> filtered(final Predicate<? super E> filter) {
        E e = next;
        while (e != null && !filter.test(e)) {
            e = AbstractIterator.this.next0(e);
        }
        next = e;
        return new AbstractIterator<E>(next) {
            @Override
            protected E next0(E ignore) {
                E e = AbstractIterator.this.next0(ignore);
                while (e != null && !filter.test(e)) {
                    e = AbstractIterator.this.next0(e);
                }
                return e;
            }

        };
    }

    /** {@inheritDoc} */
    @Override
    public final boolean hasNext() {
        return next != null;
    }

    /** {@inheritDoc} */
    @Override
    public final E next() {
        E result = next;
        if (result == null) {
            throw new NoSuchElementException();
        }
        next = next0(result);
        return result;
    }

    protected abstract E next0(E current);

    public static <E> Iterator<E> filterOnType(Iterator<?> iterator, Class<E> type) {
        return new AbstractIterator<E>() {
            @SuppressWarnings("unchecked")
            protected E next0(E current) {
                while (iterator.hasNext()) {
                    Object o = iterator.next();
                    if (type.isInstance(o)) {
                        return (E) o;
                    }
                }
                return null;
            }

            /** {@inheritDoc} */
            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }
}
