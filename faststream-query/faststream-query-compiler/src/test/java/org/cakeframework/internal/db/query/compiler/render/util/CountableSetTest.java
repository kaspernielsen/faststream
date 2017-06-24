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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests {@link CountableSet}.
 * 
 * @author Kasper Nielsen
 */
public class CountableSetTest {

    @Test
    public void toMap() {
        CountableSet<String> cs = new CountableSet<>();
        assertEquals(0, cs.add("foo"));
        assertEquals(1, cs.add("foo"));
        assertEquals(2, cs.add("foo"));
        assertEquals(0, cs.add("Foo"));
    }

    @Test
    public void copyConstructor() {
        CountableSet<String> cs = new CountableSet<>();
        assertEquals(0, cs.add("foo"));
        assertEquals(1, cs.add("foo"));
        assertEquals(0, cs.add("Foo"));
        cs = new CountableSet<>(cs);
        assertEquals(2, cs.add("foo"));
        assertEquals(3, cs.add("foo"));
        assertEquals(1, cs.add("Foo"));
    }

    @Test
    public void toStringg() {
        CountableSet<String> cs = new CountableSet<>();
        assertEquals(0, cs.add("foo"));
        assertEquals(1, cs.add("foo"));
        assertEquals(cs.hm.toString(), cs.toString());
    }
}
