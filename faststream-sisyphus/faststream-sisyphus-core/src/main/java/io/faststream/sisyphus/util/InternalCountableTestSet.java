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
package io.faststream.sisyphus.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * A very simple set that counts the number of equivalent elements.
 * 
 * @param <T>
 *            the elements to count
 * @author Kasper Nielsen
 */
public class InternalCountableTestSet<T> {

    private final HashMap<T, Integer> hm = new HashMap<>();

    public void add(InternalCountableTestSet<T> other) {
        for (Map.Entry<T, Integer> e : other.hm.entrySet()) {
            Integer i = hm.get(e.getKey());
            hm.put(e.getKey(), i == null ? e.getValue() : e.getValue() + i);
        }
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
        hm.put(t, i == null ? 1 : i + 1);
        return i == null ? 0 : i;
    }

    public String toSortedString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<T, Integer> e : new TreeMap<>(hm).entrySet()) {
            sb.append(e.getKey() + " = " + e.getValue()).append("\n");
        }
        return sb.toString();
    }

    public String toString() {
        return hm.toString();
    }
}
