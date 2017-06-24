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
package io.faststream.query.db.nodes.stream;


/**
 *
 * @author Kasper Nielsen
 */
public class StreamContext {
    boolean consumed;
    Runnable closeAction;

    Throwable t;

    public void close() {
        consumed = true;
        Runnable closeAction = this.closeAction;
        if (closeAction != null) {
            this.closeAction = null;
            closeAction.run();
        }
    }

    public void consume() {
        if (consumed) {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
        consumed = true;
    }

    public void checkNotConsumed() {
        if (consumed) {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
    }

    public void onClose(Runnable runOnClose) {
        Runnable closeAction = this.closeAction;
        this.closeAction = (closeAction == null) ? runOnClose : suppressSecondRunnable(closeAction, runOnClose);
    }

    static Runnable suppressSecondRunnable(Runnable r1, Runnable r2) {
        return new Runnable() {
            public void run() {
                try {
                    r1.run();
                } catch (Throwable e1) {
                    try {
                        r2.run();
                    } catch (Throwable e2) {
                        try {
                            e1.addSuppressed(e2);
                        } catch (Throwable ignore) {}
                    }
                    throw e1;
                }
                r2.run();
            }
        };
    }
}

// public static void close(QueryOperation o) {
// QueryOperation prev = o.previous();
// if (prev != null) {
// close(prev);
// }
// Runnable r = null;
// if (o instanceof SO_OnClose) {
// r = ((SO_OnClose) o).getRunnable();
// } else if (o instanceof SI_OnClose) {
// r = ((SI_OnClose) o).getRunnable();
// } else if (o instanceof SL_OnClose) {
// r = ((SL_OnClose) o).getRunnable();
// } else if (o instanceof SD_OnClose) {
// r = ((SD_OnClose) o).getRunnable();
// }
// if (r != null) {
// try {
// r.run();
// } catch (RuntimeException e) {
//
// }
// }
// }
//
// public static void main(String[] args) {
// Stream<Integer> s = Stream.of(1, 2, 3);
// s.onClose(() -> System.out.println("JE1:"));
// s.onClose(() -> System.out.println("JE2:"));
// s.distinct().onClose(() -> System.out.println("JE4:"));
// s.close();
// s.distinct().onClose(() -> System.out.println("JE4:"));
// s.onClose(() -> System.out.println("JE3:"));
// s.close();
//
// s.onClose(() -> {
// throw new Error1();
// });
// s.onClose(() -> {
// throw new Error2();
// });
// s.onClose(() -> {
// throw new Error3();
// });
// s.close();
// }
