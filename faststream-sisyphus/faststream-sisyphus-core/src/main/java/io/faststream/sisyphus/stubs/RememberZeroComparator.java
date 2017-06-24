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

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This comparator delegates to another comparator. The problem is that we do not provide stable sorting. So in the case
 * where hasZero=true we know that there are two elements that are equal. So there is more than one permutation when
 * sorting.
 * 
 * @param <T>
 *            the type of objects that may be compared by this comparator
 * @author Kasper Nielsen
 */
public final class RememberZeroComparator<T> implements Comparator<T> {

    private final AtomicBoolean hasZero = new AtomicBoolean();
    private final Comparator<T> comparator;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public RememberZeroComparator(Comparator comparator) {
        this.comparator = Objects.requireNonNull(comparator);
    }

    public boolean hadZero() {
        return hasZero.get();
    }

    public boolean reset() {
        return hasZero.getAndSet(false);
    }

    @Override
    public int compare(T o1, T o2) {
        int diff = comparator.compare(o1, o2);
        if (diff == 0) {
            hasZero.set(true);
        }
        return diff;
    }

    public static <T> RememberZeroComparator<T> from(Comparator<T> comparator) {
        return new RememberZeroComparator<>(comparator);
    }
}
