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
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * An abstract test that creates temporary directories for test outputs.
 *
 * @author Kasper Nielsen
 */
public class AbstractTemporaryDirectoryTest {

    /** The output of these tests will be kept in the temporary folder */
    Set<Description> keepThisTest = new CopyOnWriteArraySet<>();

    @Rule
    public DeleteOnTestSuccess rule = new DeleteOnTestSuccess();

    protected Path testroot;

    // perhaps we should move failed tests into user.tmp/cake/failedtests/TestName

    public String getTestName() {
        return rule.description.getMethodName();
    }

    /** Call this method to keep the tests contents. */
    protected void keepTestContents() {
        keepThisTest.add(rule.description);
    }

    @Before
    public final void setupDirectory() throws Exception {
        testroot = Files.createTempDirectory("faststream-" + getTestName() + "-test");
        // testroot = rootPath.resolve(getTestName());
        Files.createDirectories(testroot);
    }

    class DeleteOnTestSuccess extends TestWatcher {

        Description description;

        @Override
        public void starting(Description description) {
            this.description = description;
        }

        /** Delete test directory on successful test. */
        @Override
        public void succeeded(Description description) {
            if (!keepThisTest.contains(description)) {
                Path root = testroot; // .resolve(description.getMethodName());
                try {
                    DiffTestUtil.deleteDirectoryRecursively(root);
                } catch (IOException e) {
                    String file = e instanceof FileSystemException ? ((FileSystemException) e).getFile() : "unknown";
                    throw new Error("Could not cleanup directory, maybe the file " + file + " is open in an editor", e);
                }
            }
        }
    }
}
