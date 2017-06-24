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
import static org.junit.Assert.assertNull;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenClass;
import io.faststream.codegen.core.helper.TestUtil;

/**
 * Tests Codegen.
 *
 * @author Kasper Nielsen
 */
public class CodegenTest {

    @SuppressWarnings("rawtypes")
    @Test
    public void simple() throws Exception {
        Codegen c = new Codegen();
        CodegenClass clz = c.newClass("public class Test implements Callable");
        clz.addImport(Callable.class);
        clz.addMethod("public Object call()").add("return \"Hello\";");
        Class<Callable> call = clz.compile();
        assertEquals("Hello", call.newInstance().call());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void simpleInAPackage() throws Exception {
        Codegen c = new Codegen();
        CodegenClass clz = c.newClass("public class Test implements Callable").setPackage("test");
        clz.addImport(Callable.class);
        clz.addMethod("public Object call()").add("return \"Hello\";");
        Class<Callable> call = clz.compile();
        assertEquals("Hello", call.newInstance().call());
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void simple2() throws Exception {
        Codegen c = new Codegen();

        CodegenClass clz1 = c.newClass("public class Test implements Callable");
        clz1.addImport(Callable.class);
        clz1.addMethod("public Object call()").add("return getClass().getName();");

        CodegenClass clz2 = c.newClass("public class Test2 extends Test");

        Class<Callable> call = clz1.compile();
        assertEquals("Test", call.newInstance().call());

        call = clz2.compile();
        assertEquals("Test2", call.newInstance().call());
    }

    /**
     * Tests that we can create multiple sessions and they can refer to classes that where loaded in previous sessions.
     *
     */
    @SuppressWarnings("rawtypes")
    @Test
    public void canReusePreviousDefinitions() throws Exception {
        Codegen c = new Codegen();

        CodegenClass clz1 = c.newClass("public class Test implements Callable");
        clz1.addImport(Callable.class);
        clz1.addMethod("public Object call()").add("return getClass().getName();");

        Class<Callable> call = clz1.compile();
        assertEquals("Test", call.newInstance().call());
        // System.out.println("NewSession");
        // Now we make a new session and asserts that Test can still be referenced.

        clz1 = c.newClass("public class Test2 extends Test");

        call = clz1.compile();
        assertEquals("Test2", call.newInstance().call());
    }

    /** Just a little helper method that makes sure no reference is kept to codegen. */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Class<Callable<String>> createSimpleClass(Codegen codegen) {
        CodegenClass clz1 = codegen.newClass("public class Test implements Callable");
        clz1.addImport(Callable.class);
        clz1.addMethod("public Object call()").add("return getClass().getName();");
        return (Class) clz1.compile();
    }

    /**
     * Tests that a class can be garbage collected when we no longer hold a reference to Codegen
     */
    @Test
    public void testGc() throws Exception {
        AtomicReference<Codegen> ar = new AtomicReference<>(new Codegen());
        WeakReference<Class<Callable<String>>> wr = new WeakReference<>(createSimpleClass(ar.get()));
        assertEquals("Test", wr.get().newInstance().call());
        ar.set(null);
        assertNull(ar.get());
        TestUtil.assertReferenceIsGCed("All references to service not cleared by the container", wr);
    }
}
