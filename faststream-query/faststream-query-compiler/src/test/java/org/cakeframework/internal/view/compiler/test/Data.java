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
package org.cakeframework.internal.view.compiler.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Kasper Nielsen
 */
public class Data {
    public Object[] aa = new Object[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "A", "B", "C", "D",
            "E", "F", "G", "H", "I", "J", "K", "L" };

    public Object[] aaSmall = new Object[] { "A", "B", "C", "C" };

    public Integer[] Ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

    public List<String> list = Arrays.asList("A", "B", "C", "D");

    public int[] ints = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

    public MapEntry[] entries = new MapEntry[] { null, null, new MapEntry(1, "A", null), null,
            new MapEntry(4, "D", new MapEntry(3, "C", new MapEntry(2, "B", null))) };

    public Map<Integer, String> entriesCopy = new HashMap<>();

    public Data() {
        for (MapEntry e : entries) {
            for (MapEntry ee = e; ee != null; ee = ee.next) {
                entriesCopy.put((Integer) ee.key, (String) ee.value);
            }
        }
    }

    /** Data. */
    public static class MapEntry {

        public MapEntry next;
        public Object key;
        public Object value;

        public MapEntry(Object key, Object value, MapEntry next) {
            this.next = next;
            this.key = key;
            this.value = value;
        }
    }
}
