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
package io.faststream.sisyphus.spi;

import static java.util.Objects.requireNonNull;

import java.util.ArrayDeque;

import io.faststream.sisyphus.spi.method.TestMethod;

/**
 * This exception is used to capture information about the failure in such a way as it can be reproduced.
 * 
 * @author Kasper Nielsen
 */
class InternalReproducibleException extends AssertionError {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    final Object actual;

    final ArrayDeque<Entry> AE = new ArrayDeque<>();

    final Object expected;

    final long initialSeed;

    InternalReproducibleException(Throwable cause, Object expected, Object actual, long initialSeed) {
        super(cause);
        this.expected = requireNonNull(expected);
        this.actual = requireNonNull(actual);
        this.initialSeed = initialSeed;
    }

    void add(long seed, TestMethod method) {
        AE.addFirst(new Entry(seed, method));
    }

    static class Entry {
        TestMethod method;
        long seed;

        Entry(long seed, TestMethod method) {
            this.seed = seed;
            this.method = requireNonNull(method);
        }
    }
}
