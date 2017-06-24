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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.faststream.codegen.core.CodegenClass;

/**
 * 
 * @author Kasper Nielsen
 */
public class CodegenClassTest extends AbstractCodegenTest {

    @Test
    public void getName() {
        CodegenClass clz = c.newClass("public class TestZXV");
        assertEquals("TestZXV", clz.getSimpleName());
        clz.compile();
    }

    @Test
    public void constructors() {
        CodegenClass clz = c.newClass("public class Test");
        clz.addField("String foo;");
        clz.addField("int boo;");
        clz.addMethod("Test(String foo)").add("this.foo = foo;");
        clz.addMethod("public Test(int boo)").add("this.boo = boo;");
        clz.compile();
    }

}
