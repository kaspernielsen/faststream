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

import org.junit.Test;

import io.faststream.codegen.core.CodegenBlock;
import io.faststream.codegen.core.CodegenClass;
import io.faststream.codegen.core.CodegenMethod;

/**
 * 
 * @author Kasper Nielsen
 */
public class CodegenBlockTest extends AbstractCodegenTest {

    @Test
    public void simpleBlock() {
        CodegenClass clz = c.newClass("public class Test");
        CodegenMethod m = clz.addMethod("public void foo()");
        m.add("String str = \"fooo\";");
        m.add("int i = ", 123, ";");
        CodegenBlock b = m.newNestedBlock("if (i > 321)");
        b.add("System.out.println(str);");
        m.add("int j = i;");
        clz.compile();
    }

    @Test
    public void multipleBlocks() {
        CodegenClass clz = c.newClass("public class Test");
        CodegenMethod m = clz.addMethod("public void foo()");
        m.add("String str = \"fooo\";");
        m.add("int i = ", 123, ";");
        CodegenBlock b1 = m.newNestedBlock("if (i > 321)");
        CodegenBlock b2 = m.newNestedBlock("if (i < 121)");
        b2.add("System.out.println(str + \"second\");");
        b1.add("System.out.println(str + \"first\");");
        clz.compile();
    }

    @Test
    public void multipleBlockArguments() {
        CodegenClass clz = c.newClass("public class Test");
        CodegenMethod m = clz.addMethod("public void foo()");
        m.add("int i = ", 123, ";");
        m.newNestedBlock("if (i > ", 321, ")");
        clz.compile();
    }

    @Test
    public void nestedBlocks() {
        CodegenClass clz = c.newClass("public class Test");
        CodegenMethod m = clz.addMethod("public void foo()");
        m.add("String str = \"fooo\";");
        m.add("int i = ", 123, ";");
        CodegenBlock b1 = m.newNestedBlock("if (i > 321)");
        b1.add("System.out.println(str + \"first1\");");
        CodegenBlock b2 = b1.newNestedBlock("if (i < 121)");
        b2.add("System.out.println(str + \"second\");");
        b1.add("System.out.println(str + \"first2\");");
        clz.compile();
    }
}
