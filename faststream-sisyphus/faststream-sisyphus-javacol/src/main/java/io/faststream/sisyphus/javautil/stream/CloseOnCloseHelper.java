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
package io.faststream.sisyphus.javautil.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.BaseStream;

/**
 *
 * @author Kasper Nielsen
 */
public class CloseOnCloseHelper {

    static final AtomicLong ORDER = new AtomicLong();

    public static <E, S extends BaseStream<E, S>> void close(AbstractRandomAnyStreamTestCase<E, S> tc, Runnable action) {
        boolean consumeBeforeClose = tc.random().nextBoolean();
        if (consumeBeforeClose) {
            action.run();
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<CheckRun> onClose = (List) tc.expected().context().onClose();

        if (onClose.size() == 0) {
            try {
                tc.actual().close();
            } catch (Throwable t) {
                throw new AssertionError(t);
            }
            if (consumeBeforeClose) {
                tc.consumed();
            } else {
                tc.streamTerminate();
            }
            return;
        }

        List<RuntimeException> allCause = new ArrayList<>();
        for (CheckRun cr : onClose) {
            assertFalse(cr.ran);
            if (cr.e != null) {
                allCause.add(cr.e);
            }
        }

        try {
            tc.actual().close();
        } catch (RuntimeException e) {
            assertTrue(allCause.size() > 0);
            assertSame(allCause.get(0), e);
            assertEquals(allCause.size() - 1, e.getSuppressed().length);
            for (int i = 0; i < e.getSuppressed().length; i++) {
                assertSame(allCause.get(i + 1), e.getSuppressed()[i]);
            }
        }
        long order = -1;
        for (CheckRun cr : onClose) {
            assertTrue(cr.ran);
            assertTrue(cr.order > order);
            order = cr.order;
        }
        if (consumeBeforeClose) {
            tc.consumed();
        } else {
            tc.streamTerminate();
        }
    }

    public static <E, S extends BaseStream<E, S>> void onClose(AbstractRandomAnyStreamTestCase<E, S> tc) {
        CheckRun r = new CheckRun(tc.random().nextBoolean() ? null : new RuntimeException());
        tc.expected().context().addRunnable(r);
        tc.nested(tc.expected(), tc.actual().onClose(r));
    }

    @SuppressWarnings("serial")
    static class CheckRun extends RuntimeException implements Runnable {
        boolean ran;
        final RuntimeException e;
        long order;

        CheckRun(RuntimeException e) {
            this.e = e;
        }

        /** {@inheritDoc} */
        @Override
        public void run() {
            order = ORDER.incrementAndGet();
            if (ran) {
                throw new AssertionError();
            }
            ran = true;
            if (e != null) {
                throw e;
            }
        }
    }
}
