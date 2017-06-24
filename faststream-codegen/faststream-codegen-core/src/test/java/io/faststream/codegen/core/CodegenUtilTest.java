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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import io.faststream.codegen.core.CodegenUtil;

/**
 * Tests {@link CodegenUtil}.
 * 
 * @author Kasper Nielsen
 */
public class CodegenUtilTest {

    @Test
    public void indent() {
        assertEquals("    ", CodegenUtil.INDENT_STRING);
        assertEquals("", CodegenUtil.indent(0));
        assertEquals("    ", CodegenUtil.indent(1));
        assertEquals("        ", CodegenUtil.indent(2));
    }

    @Test
    public void defaultValues() {
        assertEquals("false", CodegenUtil.defaultValue(boolean.class));
        assertEquals("(byte) 0", CodegenUtil.defaultValue(byte.class));
        assertEquals("(char) 0", CodegenUtil.defaultValue(char.class));
        assertEquals("0d", CodegenUtil.defaultValue(double.class));
        assertEquals("0f", CodegenUtil.defaultValue(float.class));
        assertEquals("0", CodegenUtil.defaultValue(int.class));
        assertEquals("0L", CodegenUtil.defaultValue(long.class));
        assertEquals("(short) 0", CodegenUtil.defaultValue(short.class));
        assertEquals("null", CodegenUtil.defaultValue(String.class));
    }

    @Test
    public void flatten() {
        Object[] o0 = new Object[] {};
        Object[] o1 = new Object[] { 1 };
        Object[] o2 = new Object[] { 2, "4" };
        assertSame(o0, CodegenUtil.flatten(o0));
        assertSame(o1, CodegenUtil.flatten(o1));
        assertSame(o2, CodegenUtil.flatten(o2));
        assertArrayEquals(new Object[] { 1 }, CodegenUtil.flatten(new Object[] { new Object[] { 1 } }));
        assertArrayEquals(new Object[] { 1, 4, "3" },
                CodegenUtil.flatten(new Object[] { new Object[] { 1 }, 4, new Object[] { "3" } }));
        assertArrayEquals(new Object[] { 1, 4, "3" },
                CodegenUtil.flatten(new Object[] { new Object[] { 1 }, 4, new Object[] { new Object[] { "3" } } }));

    }
}
