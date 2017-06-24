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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import io.faststream.sisyphus.builder.TestBuilder;
import io.faststream.sisyphus.spi.InternalTestResult.Failure;

/**
 * 
 * @author Kasper Nielsen
 */
public class TestRunner {

    public static InternalTestResult start(TestBuilder<?, ?> builder, long iterations) {
        InternalTestResult r = new InternalTestResult(iterations);
        ForkJoinPool executor = builder.getExecutor();
        int parallelism = executor.getParallelism();
        r.runners = new BatchRunner[parallelism];
        for (int i = 0; i < parallelism; i++) {
            long runs = iterations / parallelism + (iterations % parallelism > i ? 1 : 0);
            r.runners[i] = new BatchRunner(new AtomicLong(runs), r, builder, i + builder.getInitialSeed());
        }
        try {
            r.printer.start();
            if (parallelism == 1) {// If parallelism = 1 just run in main thread
                r.runners[0].run();
            } else {
                List<Callable<Object>> callables = new ArrayList<>();
                for (BatchRunner runner : r.runners) {
                    callables.add(Executors.callable(runner));
                }
                try {
                    List<Future<Object>> invokeAll = executor.invokeAll(callables);
                    for (Future<Object> future : invokeAll) {
                        future.get();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (r.hasFailures()) {
                Failure first = r.getFirstFailure();
                for (InternalReproducibleException.Entry e : first.e.AE) {
                    System.out.println(e.method);
                }
                System.out.println(first.e.actual);
                // System.out.println("xxxx");
                if (builder.isReproduce()) {
                    first.reproduce(builder);
                }
                // System.out.println("xxxx");
                Throwable cause = first.e.getCause();
                if (cause instanceof Error) {
                    throw (Error) cause;
                } else if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else {
                    throw new AssertionError("unexpected exception", cause);
                }
            }
            return r;
        } finally {
            r.setRunnersToZero();
            r.printer.interrupt();
            try {
                r.printer.join();
            } catch (InterruptedException ignore) {}
        }
    }

}
