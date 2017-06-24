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
package io.faststream.codegen.core;

import static io.faststream.codegen.core.CodegenUtil.LS;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.faststream.codegen.core.ImportSet;

/**
 * Tests {@link ImportSet}. {@link CodegenImportTest} is also used for testing it
 * 
 * @author Kasper Nielsen
 */
public class ImportSetTest {

    @Test
    public void test() {
        ImportSet set = new ImportSet();
        assertEquals(0, set.size());
        set.add(Integer.class);
        assertEquals(0, set.size());
        set.add(Iterable.class, Long.class);
        assertEquals(0, set.size());
        assertEquals("", set.toString());

        set.add(Map.class);
        assertEquals(1, set.size());
        assertEquals("import java.util.Map;" + LS + LS, set.toString());

        set.add(Map.class);
        assertEquals(1, set.size());
        assertEquals("import java.util.Map;" + LS + LS, set.toString());

        set.add(List.class);
        assertEquals(2, set.size());
        assertEquals("import java.util.List;" + LS + "import java.util.Map;" + LS + LS, set.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addIAE() throws ClassNotFoundException {
        new ImportSet().add(Class.forName("Foob"));
        // http://docs.oracle.com/javase/specs/jls/se7/html/jls-7.html#jls-7.5
    }

    @Test
    public void clone_() {
        ImportSet set = new ImportSet();
        set.add(Map.class, List.class);
        ImportSet clone = set.clone();
        assertEquals(set, clone);
        assertEquals(set.hashCode(), clone.hashCode());

        clone.add(HashMap.class);
        assertEquals(2, set.size());
        assertEquals(3, clone.size());

        set.add(LinkedHashMap.class);
        assertEquals(3, set.size());
        assertEquals(3, clone.size());

        set.add(HashMap.class, LinkedHashMap.class);
        clone.add(HashMap.class, LinkedHashMap.class);
        assertEquals(set, clone);
        assertEquals(set.hashCode(), clone.hashCode());

        assertEquals(clone, set.add(clone));
    }
}
