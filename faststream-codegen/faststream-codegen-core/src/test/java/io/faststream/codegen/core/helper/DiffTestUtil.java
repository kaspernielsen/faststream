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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * 
 * @author Kasper Nielsen
 */
class DiffTestUtil {

    public static void assertSame(Path expected, Path actual) throws IOException {
        assertTrue("File " + expected + " does not exist", Files.exists(expected));
        assertTrue("File " + actual + " does not exist", Files.exists(actual));

        // First compare structure, that is does all files and directories exists both places
        Map<Path, Path> files = assertPathStructure(expected, actual);

        // now compare the actual contents of files
        int fails = 0;
        for (Map.Entry<Path, Path> e : files.entrySet()) {
            List<String> expectedContents = Files.readAllLines(e.getKey(), Charset.defaultCharset());
            List<String> actualContents = Files.readAllLines(e.getValue(), Charset.defaultCharset());
            Patch patch = DiffUtils.diff(expectedContents, actualContents);
            if (patch.getDeltas().size() > 0) {
                System.err.println("Contents of expected file " + e.getKey() + " differs from actual file "
                        + e.getValue());
                System.err
                        .println("---------------------------------EXPECTED------------------------------------------------");
                for (String str : expectedContents) {
                    System.err.println(str);
                }
                System.err
                        .println("---------------------------------ACTUAL--------------------------------------------------");
                for (String str : actualContents) {
                    System.err.println(str);
                }
                System.err
                        .println("---------------------------------DELTAS------------------------------------------------");
                for (Delta delta : patch.getDeltas()) {
                    System.err.println(delta);
                }
                System.err
                        .println("---------------------------------------------------------------------------------------");

            }
            fails += patch.getDeltas().size();
        }
        checkFail(fails);
    }

    static Map<Path, Path> assertPathStructure(final Path expected, final Path actual) throws IOException {
        LinkedHashMap<Path, Path> result = new LinkedHashMap<>();

        Set<Path> allExpected = getAllPaths(expected);
        allExpected.remove(expected);
        Set<Path> allActual = getAllPaths(actual);
        allActual.remove(actual);
        int indexExpected = expected.getNameCount();
        int fails = 0;
        // First check expected directories
        for (Iterator<Path> iterator = allExpected.iterator(); iterator.hasNext();) {
            Path p = iterator.next();
            if (Files.isDirectory(p)) {
                Path rel = p.subpath(indexExpected, p.getNameCount());
                Path shouldExist = actual.resolve(rel);
                if (!Files.exists(shouldExist)) {
                    System.err.println("Expected directory " + shouldExist + " corresponding to " + p
                            + " does not exist");
                    fails++;
                } else if (!Files.isDirectory(shouldExist)) {
                    System.err.println("File " + shouldExist + " should be a directory");
                    fails++;
                }
                allActual.remove(shouldExist);
                iterator.remove();
            }
        }

        checkFail(fails);
        // Second check expected files, directories have been removed
        for (Path p : allExpected) {
            Path rel = p.subpath(indexExpected, p.getNameCount());
            Path shouldExist = actual.resolve(rel);
            if (!Files.exists(shouldExist)) {
                System.err.println("Expected file " + shouldExist + " does not exist");
                fails++;
            } else if (Files.isDirectory(shouldExist)) {
                System.err.println("Directory " + shouldExist + " should be a file");
                fails++;
            } else {}
            allActual.remove(shouldExist);
            result.put(p, shouldExist);
        }

        checkFail(fails);
        // anything thats left is unknown
        if (allActual.size() > 0) {
            for (Path p : allActual) {
                if (Files.isDirectory(p)) {
                    System.err.println("Unexpected directory " + p);
                } else {
                    System.err.println("Unexpected file " + p);
                }
            }
            checkFail(1);
        }
        return result;
    }

    static void checkFail(int count) {
        if (count > 0) {
            throw new AssertionError("Contents of paths differed, see System.err for details");
        }
    }

    static Set<Path> getAllPaths(Path base) throws IOException {
        final LinkedHashSet<Path> paths = new LinkedHashSet<>();
        // First check that all expected directories exist
        Files.walkFileTree(base, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                paths.add(dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                paths.add(file);
                return super.visitFile(file, attrs);
            }
        });
        return new LinkedHashSet<>(paths); // return reversed
    }

    /**
     * Copies the directory contents recursively, including the specified path
     */
    static void copyDirectoryRecursively(final Path source, final Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.copy(dir, target.resolve(source.relativize(dir)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Delete the directory contents recursively, including the specified path
     */
    static void deleteDirectoryRecursively(Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
