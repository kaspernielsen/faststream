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
package io.faststream.query.util;

import java.util.Comparator;
import java.util.Map;

/**
 * A comparison function, which imposes a <i>total ordering</i> on some collection of objects. The main difference
 * compared to {@link Comparator} is that this interface compares two components instead of just one. For example, both
 * the <tt>key</tt> and <tt>value</tt> in a {@link Map}.
 * 
 * @param <S>
 *            the first type of objects that may be compared by this comparator
 * @param <T>
 *            the first type of objects that may be compared by this comparator
 * @author Kasper Nielsen
 * 
 * @see Comparator
 */
@FunctionalInterface
public interface BiComparator<S, T> {

    /**
     * Compares first and second parameter against the third and fourth argument for order.
     * 
     * @param s1
     *            the first object to be compared with the third object
     * @param t1
     *            the second object to be compared with the forth object
     * @param s2
     *            the third object to be compared with the first object
     * @param t2
     *            the fourth object to be compared with the second object
     * @return a negative integer, zero, or a positive integer as the first and second argument is less than, equal to,
     *         or greater than the third and fourth argument respectively.
     */
    int compare(S s1, T t1, S s2, T t2);
}
