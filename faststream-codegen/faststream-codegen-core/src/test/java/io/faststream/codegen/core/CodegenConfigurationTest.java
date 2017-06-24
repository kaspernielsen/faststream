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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.faststream.codegen.core.CodegenConfiguration;
import io.faststream.codegen.core.helper.TestUtil;

/**
 * Tests {@link CodegenConfiguration}.
 *
 * @author Kasper Nielsen
 */
public class CodegenConfigurationTest {

    private CodegenConfiguration conf;

    @Before
    public void setup() {
        conf = new CodegenConfiguration();
    }

    @Test
    public void classLoaderParent() {
        assertNull(conf.getClassLoaderParent());
        assertSame(conf, conf.setClassLoaderParent(CodegenConfigurationTest.class.getClassLoader()));
        assertSame(CodegenConfigurationTest.class.getClassLoader(), conf.getClassLoaderParent());
    }

    @Test
    public void sourcePath() {
        assertNull(conf.getSourcePath());
        Path dummy = TestUtil.dummy(Path.class);
        assertSame(conf, conf.setSourcePath(dummy));
        assertSame(dummy, conf.getSourcePath());

        String userDir = System.getProperty("user.dir");
        assertSame(conf, conf.setSourcePath(userDir));
        assertEquals(Paths.get(userDir), conf.getSourcePath());

        conf.setSourcePath((String) null);
        assertNull(conf.getSourcePath());
    }

    @Test
    public void defaultPackage() {
        assertNull(conf.getDefaultPackage());
        assertSame(conf, conf.setDefaultPackage(List.class.getPackage()));
        assertEquals(List.class.getPackage().getName(), conf.getDefaultPackage());

        conf.setDefaultPackage((Package) null);
        assertNull(conf.getDefaultPackage());

        assertSame(conf, conf.setDefaultPackage("foo.boo"));
        assertEquals("foo.boo", conf.getDefaultPackage());
    }

    @Test
    public void codePrinter() {
        assertTrue(conf.getCodeWriters().isEmpty());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        PrintWriter pw = new PrintWriter(new ByteArrayOutputStream());

        assertSame(conf, conf.addCodeWriter(pw));
        List<PrintWriter> l = conf.getCodeWriters();
        assertEquals(Collections.singletonList(pw), l);

        assertSame(conf, conf.addCodeWriter(ps));
        assertEquals(1, l.size());// L is a copy

        l = conf.getCodeWriters();
        l.remove(pw);
        l.get(0).append("foo").flush();
        assertEquals("foo", baos.toString());
    }

    @Test(expected = NullPointerException.class)
    public void codePrinterNPE1() {
        conf.addCodeWriter((PrintStream) null);
    }

    @Test(expected = NullPointerException.class)
    public void codePrinterNPE2() {
        conf.addCodeWriter((PrintWriter) null);
    }
}
