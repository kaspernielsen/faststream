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

/**
 * 
 * 
 * @author Kasper Nielsen
 */
@SuppressWarnings("serial")
class ControlErrors extends Error {

    /** An error that signals that the test runner should start a new batch. */
    private static final GenerateNewBatch GENERATE_NEW_BATCH = new GenerateNewBatch();

    private static final boolean NEW_ERRORS = true;

    /** An error that signals that the test runner should start a new batch. */
    private static final ResetToMark RESET_TO_MARK = new ResetToMark();

    static void throwNewBatch() {
        if (NEW_ERRORS) {
            throw new GenerateNewBatch();
        }
        throw GENERATE_NEW_BATCH;
    }

    static void throwResetToMark() {
        if (NEW_ERRORS) {
            throw new ResetToMark();
        }
        throw RESET_TO_MARK;
    }

    static class GenerateNewBatch extends ControlErrors {}

    static class ResetToMark extends ControlErrors {}
}
