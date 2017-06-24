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

import static org.junit.Assert.assertSame;

import java.util.concurrent.Callable;

import org.junit.Test;

import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenClass;

/**
 * 
 * @author Kasper Nielsen
 */
public class CodegenMultipleClasses {

    @Test
    public void twoClasses() throws Exception {
        Codegen c = new Codegen();

        CodegenClass clz1 = c.newClass("public class A implements Callable");
        clz1.addImport(Callable.class);
        clz1.addField("B b;");
        clz1.addMethod("public A(B b)").add("this.b = b;");
        clz1.addMethod("public Object call()").add("return b;");

        CodegenClass clz2 = c.newClass("public class B implements Callable");
        clz2.addImport(Callable.class);
        clz2.addField("Integer i1;");
        clz2.addMethod("public B(Integer i1)").add("this.i1 = i1;");
        clz2.addMethod("public Object call()").add("return i1;");

        @SuppressWarnings("unchecked")
        Callable<Integer> b = (Callable<Integer>) clz2.compile().getConstructors()[0].newInstance(123);
        assertSame(123, b.call());

        Callable<?> a = (Callable<?>) clz1.compile().getConstructors()[0].newInstance(b);
        assertSame(b, a.call());
    }
}
