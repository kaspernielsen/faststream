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
package io.faststream.sisyphus.javautil.iterable;

import java.util.Iterator;

import io.faststream.sisyphus.expected.Expected;

/**
 * @param <E>
 *            the elements in the iterable
 * 
 * @author Kasper Nielsen
 */
public abstract class ExpectedIterable<E> extends Expected implements Iterable<E> {

    protected abstract void assumeRemovable();

    public abstract Iterator<E> iterator();

    protected abstract boolean isOrdered();

    @SuppressWarnings("unused")
    public int size() {
        int count = 0;
        for (E e : this) {
            count++;
        }
        return count;
    }
}
