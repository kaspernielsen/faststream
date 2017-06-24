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

import java.util.concurrent.Callable;

import org.junit.Test;

import io.faststream.codegen.core.CodegenClass;

/**
 * 
 * @author Kasper Nielsen
 */
public class CodegenFormattingTest extends AbstractCodegenTest {

    @Test
    public void simple() throws Exception {
        c.newClass("public class Test").compile();
    }

    @Test
    public void simpleWithPackage() throws Exception {
        c.newClass("public class Test").setPackage("test").compile();
    }

    @Test
    public void simpleCallable() throws Exception {
        CodegenClass clz = c.newClass("public class Test implements Callable");
        clz.addImport(Callable.class);
        clz.addMethod("public Object call()").add("return \"Hello\";");
        clz.compile();
    }

    @Test
    public void simpleCallableWithPackage() throws Exception {
        CodegenClass clz = c.newClass("public class Test implements Callable").setPackage("test.test2");
        clz.addImport(Callable.class);
        clz.addMethod("public Object call()").add("return \"Hello\";");
        clz.compile();
    }

    @Test
    public void twoClasses() throws Exception {
        c.newClass("public class Test").compile();
        c.newClass("public class Test2").setPackage("test").compile();
    }

}
