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
package io.faststream.sisyphus;

import static java.util.Objects.requireNonNull;

/**
 * The environment tests are running in.
 * 
 * @author Kasper Nielsen
 */
public enum ExecutionEnvironment {

    /** Running in Jenkins. */
    CI(TestScale.BIG),

    /** Running in Jenkins as a nightly build. */
    CI_NIGHTLY(TestScale.HUGE),

    /** Run from the command prompt by a developer. */
    COMMAND_PROMT(TestScale.NORMAL),

    /** Running inside and IDE such as eclipse. */
    INSIDE_AN_IDE(TestScale.SMALL),

    /** Unknown where this is run from. */
    UNKNOWN(TestScale.NORMAL);

    /** The current Execution environment */
    static volatile ExecutionEnvironment CURRENT;

    static final boolean RUNNING_IN_ECLIPSE;

    static {
        // Just some property we know that the surefire plugin sets.
        // And we assume maven is running it
        boolean runningInMaven = !System.getProperty("surefire.real.class.path", "zxcv").equals("zxcv");

        // This class is usually initialized by a Test and as such will have the eclipse test runner
        // in its call stack
        // We should update to run in more IDES
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        RUNNING_IN_ECLIPSE = st[st.length - 1].toString().contains("org.eclipse");

        // Explicitly set on the command line.
        String property = System.getProperty("cakex.environment");
        if (property == null) {
            CURRENT = runningInMaven ? COMMAND_PROMT : INSIDE_AN_IDE;
        } else {
            CURRENT = ExecutionEnvironment.valueOf(property);
        }
    }

    final TestScale scale;

    private ExecutionEnvironment(TestScale scale) {
        this.scale = requireNonNull(scale);
    }

    public static ExecutionEnvironment getEnvironment() {
        return CURRENT;
    }

    /**
     * Sets the current environment. This is primarily useful for sometimes reproducing tests in another environment.
     * 
     * @param environment
     *            the execution environment
     */
    public static void setEnvironment(ExecutionEnvironment environment) {
        CURRENT = environment;
    }
}
