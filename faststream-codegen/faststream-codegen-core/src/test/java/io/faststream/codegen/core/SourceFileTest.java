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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Ignore;
import org.junit.Test;

import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenClass;
import io.faststream.codegen.core.CodegenConfiguration;

/**
 * Some additional tests.
 * 
 * @author Kasper Nielsen
 */
public class SourceFileTest extends AbstractCodegenTest {

    @Test
    public void testWritesFileToCorrectPath() {
        Path p = testroot.resolve("Test.java");
        // If this fails, we might have some leftovers from a previous test
        // Check p
        assertFalse(Files.exists(p));
        CodegenClass clz = c.newClass("public class Test");
        clz.compile();
        assertTrue(Files.exists(p));
    }

    @Test
    @Ignore
    public void doesNotOverwriteFileWithSameHash() throws IOException {
        c.newClass("public class Test").compile();
        Path p = testroot.resolve("Test.java");
        LinkedList<String> readAllLines = new LinkedList<>(Files.readAllLines(p, Charset.defaultCharset()));
        readAllLines.addFirst("Hash should still be valid");
        Files.write(p, readAllLines, Charset.defaultCharset(), StandardOpenOption.TRUNCATE_EXISTING);

        CodegenConfiguration cc = new CodegenConfiguration();
        cc.setSourcePath(testroot);
        c = new Codegen(cc);// lets make we dont cache the existing version
        c.newClass("public class Test").compile();
    }

    @Test
    public void testOverwritesExistingFile() throws IOException {
        Path p = testroot.resolve("Test.java");
        // If this fails, we might have some leftovers from a previous test
        // Check p
        CodegenClass clz = c.newClass("public class Test");
        clz.compile();
        assertTrue(Files.exists(p));
        assertEquals("public class Test {", Files.readAllLines(p, Charset.defaultCharset()).get(0));

        Files.write(p, Arrays.asList("goo", "foo"), Charset.defaultCharset(), StandardOpenOption.TRUNCATE_EXISTING);
        assertEquals(Arrays.asList("goo", "foo"), Files.readAllLines(p, Charset.defaultCharset()));

        CodegenConfiguration cc = new CodegenConfiguration();
        cc.setSourcePath(testroot);
        c = new Codegen(cc);
        clz = c.newClass("public class Test");
        clz.compile();
        assertEquals("public class Test {", Files.readAllLines(p, Charset.defaultCharset()).get(0));

        // Special case for overwriting an empty file
        p = testroot.resolve("Test2.java");
        Files.write(p, new byte[0]);
        c.newClass("public class Test2").compile();

        p = testroot.resolve("Test3.java");
        Files.write(p, Arrays.asList("asd"), Charset.defaultCharset());
        c.newClass("public class Test3").compile();

        p = testroot.resolve("Test4.java");
        Files.write(p, Arrays.asList("1234567890123456789012345678901234567890123"), Charset.defaultCharset());
        c.newClass("public class Test4").compile();
    }
}
