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

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.processing.Filer;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Comment;

import difflib.Delta;
import difflib.Patch;
import io.faststream.codegen.core.CodegenClass;
import io.faststream.codegen.stubs.classes.PublicClassWithInnerStaticClass;

/**
 * Tests various imports
 * 
 * @author Kasper Nielsen
 */
public class CodegenImportTest extends AbstractCodegenTest {

    @Test
    public void complexImports() throws Exception {
        CodegenClass clz = c.newClass("public class Test");
        clz.addImport(Callable.class).addField("Callable a = null;");
        clz.addImport(ConcurrentHashMap.class).addField("ConcurrentHashMap b = null;");
        clz.addImport(Exception.class).addField("Exception c = null;");
        clz.addImport(Patch.class).addField("Patch d = null;");
        clz.addImport(Delta.class).addField("Delta e = null;");
        clz.addImport(HashMap.class).addField("HashMap g = null;");
        clz.addImport(Comment.class).addField("Comment h = null;");
        clz.addImport(Filer.class).addField("Filer i = null;");
        clz.compile();
    }

    @Test
    public void arrayImports() throws Exception {
        CodegenClass clz = c.newClass("public class Test");
        clz.addImport(Callable[].class).addField("Callable a = null;");
        clz.addImport(HashMap[][].class).addField("HashMap g = null;");
        clz.compile();
    }

    @Test
    public void dontImportPrimitivesOrJavaLang() throws Exception {
        CodegenClass clz = c.newClass("public class Test");
        clz.addImport(long.class).addField("long a = 123;");
        clz.addImport(String.class).addField("String g = null;");
        clz.compile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotImportFormPackagePrivate() {
        CodegenClass clz = c.newClass("public class Test");
        clz.addImport(PackagePrivate.class);
    }

    @Test
    public void importPackagePrivate() {
        CodegenClass clz = c.newClass("public class Test");
        clz.addImport(PublicClassWithInnerStaticClass.InnerClass.class);
        clz.compile();
    }

    @Test
    public void variousNonStandardPackages() {
        CodegenClass clz = c.newClass("public class Test");
        clz.addImport(c.newClass("public class TestResA").setPackage("a").compile());
        clz.addImport(c.newClass("public class TestResAB").setPackage("a.b").compile());
        clz.addImport(c.newClass("public class TestResAC").setPackage("a.c").compile());
        clz.addImport(c.newClass("public class TestResABC").setPackage("a.b.c").compile());
        clz.addImport(c.newClass("public class TestResB").setPackage("b").compile());
        clz.addImport(c.newClass("public class TestResC").setPackage("c").compile());
        clz.compile();
    }

    @Test
    public void ignoreNullImports() {
        CodegenClass clz = c.newClass("public class Test");
        clz.addImport((Class<?>) null);
        clz.compile();
    }

    @Test
    @Ignore
    // Ive taken out support for specified imports via a string
    public void importOtherPackage() {
        CodegenClass clz = c.newClass("public class Test");
        clz.addImport(c.newClass("public class TestResA").setPackage("a").compile());
        CodegenClass clz2 = c.newClass("public class TestResAB extends TestResA").setPackage("a.b");
        // clz2.addImport("a.TestResA");
        clz.addImport(clz2.compile());
    }

    static class PackagePrivate {}
}
