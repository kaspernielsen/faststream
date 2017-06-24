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
package io.faststream.codegen.core.helper;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.Description;

/**
 * A test that compares an expected directory contents with an actual directory contents.
 * 
 * @author Kasper Nielsen
 */
public class AbstractVerifyingDirectoryTest extends AbstractTemporaryDirectoryTest {

    /** The output of these tests will be kept in the temporary folder */
    Set<Description> overwriteTestResources = new CopyOnWriteArraySet<>();

    /** The path to test resources (test/src/resources), maybe null */
    private Path resourceRoot;

    @After
    public void checkDiff() throws IOException {
        // No expected output exists, lets if any actual output exists

        // resourceRoot can only be non-null if setupTestResources failed
        if (overwriteTestResources.contains(rule.description)) {
            DiffTestUtil.copyDirectoryRecursively(testroot, resourceRoot);
        }
        if (resourceRoot != null) {
            // Either an expected output exists, or the test generated output while none was expected
            if (Files.exists(resourceRoot) || Files.newDirectoryStream(testroot).iterator().hasNext()) {
                DiffTestUtil.assertSame(resourceRoot, testroot);
            }

        }
    }

    /** Call this method to keep the tests contents */
    protected void overwriteTestResources() {
        System.err.println("WARNING: THIS TEST IS OVERWRITING CONTENTS IN THE TEST RESOURCE DIRECTORY");
        overwriteTestResources.add(rule.description);
    }

    @Before
    public final void setupTestResources() throws Exception {
        // Find if we have a resource dir for this test
        Class<?> clz = rule.description.getTestClass();
        String setupResource = clz.getSimpleName() + "/setup";
        URL url = clz.getClassLoader().getResource(setupResource);
        if (url == null) {
            throw new Error("No setup file could be found. You need to create a src/test/resources/"
                    + clz.getSimpleName() + "/setup file");
        }
        Path pp = Paths.get(url.toURI()).getParent();
        resourceRoot = pp.resolve(rule.description.getMethodName());
    }

    // TODO check that there are no unused directories in src/test/resources
}
