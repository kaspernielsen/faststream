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
package org.cakeframework.internal.db.query.compiler.render.util;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * A very simple set that counts the number of equivalent elements.
 *
 * @param <T>
 *            the elements to count
 * @author Kasper Nielsen
 */
public class CountableSet<T> implements Iterable<T> {
    // important it is identity
    final IdentityHashMap<T, Integer> hm = new IdentityHashMap<>();

    public CountableSet() {}

    public CountableSet(CountableSet<T> other) {
        hm.putAll(other.hm);
    }

    /**
     * Adds the specified element to this set, returning the previous count of matching elements
     *
     * @param t
     *            the element to count
     * @return the number of existing elements of the specified type
     * @see Set#add(Object)
     */
    public int add(T t) {
        Integer i = hm.get(t);
        if (i == null) {
            hm.put(t, 1);
            return 0;
        } else {
            hm.put(t, i + 1);
            return i;
        }
    }

    public String addName(T t) {
        int c = add(t);
        return c == 0 ? t.toString() : t.toString() + c;
    }

    public int get(T t) {
        return hm.getOrDefault(t, 0);
    }

    public Set<T> findGreaterThan(int greaterThan) {
        Set<T> result = Collections.newSetFromMap(new IdentityHashMap<T, Boolean>());
        hm.forEach((k, v) -> {
            if (v > greaterThan) {
                result.add(k);
            }
        });
        // Map.Entry<> e = null;
        return result;
    }

    public String toString() {
        return hm.toString();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<T> iterator() {
        return hm.keySet().iterator();
    }

}

// public void add(CountableSet<T> other) {
// for (Map.Entry<T, Integer> e : other.hm.entrySet()) {
// Integer i = hm.get(e.getKey());
// hm.put(e.getKey(), i == null ? e.getValue() : e.getValue() + i);
// }
// }
//
// public String toSortedString() {
// StringBuilder sb = new StringBuilder();
// for (Map.Entry<T, Integer> e : new TreeMap<T, Integer>(hm).entrySet()) {
// sb.append(e.getKey() + " = " + e.getValue()).append("\n");
// }
// return sb.toString();
// }
