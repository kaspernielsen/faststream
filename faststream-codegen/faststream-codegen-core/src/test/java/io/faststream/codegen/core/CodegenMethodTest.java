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

import io.faststream.codegen.core.CodegenClass;
import io.faststream.codegen.core.CodegenMethod;

/**
 * 
 * @author Kasper Nielsen
 */
public class CodegenMethodTest extends AbstractCodegenTest {

    @Test
    public void methodAdd() {
        CodegenClass clz = c.newClass("public class Test");
        CodegenMethod m = clz.addMethod("public void foo()");
        m.add("String str = \"fooo\";");
        m.add("int i = ", 123, ";");
        m.add("if (i > 321) {");
        m.add("System.out.println(str);");
        m.add("}");
        m.add("int j = i;");
        clz.compile();
    }
    //
    // @Test
    // public void methodHolderAdd() {
    // CodegenClass clz = c.newClass("public class Test");
    // clz.newMethod(MethodElement.from(Runnable.class, "run"));
    // clz.compile();
    // }

}
