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

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;

import org.junit.Test;

import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenClass;
import io.faststream.codegen.core.CodegenMethod;

/**
 * 
 * @author Kasper Nielsen
 */
@SuppressWarnings("rawtypes")
public class CodegenDecorateteConstructorTest {

    @Test
    public void decorateConstructor() throws Exception {
        Codegen c = new Codegen();
        // c.setSourceRoot(Paths.get("c:/java/tmp"));
        CodegenClass clz1 = c.newClass("public class DecorateConstructor implements Callable");
        clz1.addImport(Callable.class);
        clz1.addField("Integer i1;");
        clz1.addMethod("public (Integer i1)").add("this.i1 = i1;");

        // Now lets decorate
        clz1.addField("Integer i2;");

        for (CodegenMethod d : clz1.getMethods()) {
            d.addParameter("Integer i2");
            d.add("this.i2 = i2;");
        }

        clz1.addMethod("public Object call()").add("return i1 + i2;");

        Class<Callable> call = clz1.compile();
        assertEquals(1, call.getDeclaredConstructors().length);
        Constructor<Callable> con = call.getConstructor(Integer.class, Integer.class);
        assertEquals(3, con.newInstance(1, 2).call());
        assertEquals(11, con.newInstance(6, 5).call());
    }

    @Test
    public void decorateMultipleConstructors() throws Exception {
        Codegen c = new Codegen();

        CodegenClass clz1 = c.newClass("public class DecorateMultipleConstructors implements Callable");
        clz1.addImport(Callable.class);
        clz1.addField("Integer i1;");
        clz1.addMethod("public (Integer i1)").add("this.i1 = i1;");
        clz1.addMethod("public (String ignore, Integer i1)").add("this.i1 = i1;");

        // Now lets decorate
        clz1.addField("Integer i2;");
        for (CodegenMethod d : clz1.getMethods()) {
            d.addParameter("Integer i2");
            d.add("this.i2 = i2;");
        }

        clz1.addMethod("public Object call()").add("return i1 + i2;");

        Class<Callable> call = clz1.compile();
        assertEquals(2, call.getDeclaredConstructors().length);
        assertEquals(3, call.getConstructor(Integer.class, Integer.class).newInstance(1, 2).call());
        assertEquals(11, call.getConstructor(String.class, Integer.class, Integer.class).newInstance("ignore", 6, 5)
                .call());
    }
}
