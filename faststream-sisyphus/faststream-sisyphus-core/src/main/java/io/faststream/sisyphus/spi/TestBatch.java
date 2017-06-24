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

import java.util.concurrent.atomic.AtomicLong;

import io.faststream.sisyphus.expected.Expected;
import io.faststream.sisyphus.spi.method.TestMethod;
import io.faststream.sisyphus.util.RandomSource;

/**
 *
 * @author Kasper Nielsen
 */
public class TestBatch {

    final long initialSeed;

    final AtomicLong operationsFinished = new AtomicLong();

    final AtomicLong remaining;

    final Expected rootExpected;

    final Object rootActual;

    final RandomSource rnd;

    final RandomSource methodSelector;

    int currentDepth;

    final GlobalContext globalContext;

    boolean isFinished;

    int streamStartedLevel;

    TestBatch(GlobalContext globalContext, RandomSource rnd, Expected expected, Object actual, AtomicLong remaining, long initialSeed) {
        this.rnd = requireNonNull(rnd);
        this.rootExpected = requireNonNull(expected);
        this.rootActual = requireNonNull(actual);
        this.remaining = remaining;
        this.initialSeed = initialSeed;
        this.methodSelector = rnd.split();
        this.globalContext = requireNonNull(globalContext);
    }

    public void runBatch(Expected expected, Object actual) {
        while (runNext()) {

            runTest(null, expected, actual);

            //

            if (currentDepth > 0) {
                // we might want to skip
            }
        }
    }

    private boolean runNext() {
        if (isFinished) {
            return false;
        } else if (remaining.get() > 0) {
            return true;
        }
        globalContext.tryStealBatch(this);
        return remaining.get() > 0;
    }

    private void runTest(TestMethod method, Expected expected, Object actual) {

    }

    public final class Context {
        public long getInitialSeed() {
            return initialSeed;
        }

        public int getDepth() {
            return currentDepth;
        }
    }
}
