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
package io.faststream.query.db.query.node;

/**
 * A query operation processor process the terminal operation of a query chain and returns a a single result for the
 * query.
 * <p>
 * This class is used as the main entry point for all runtime generated queries.
 *
 * @author Kasper Nielsen
 */
public interface TerminalQueryOperationNodeProcessor {

    /**
     * Process the specified query terminal operation.
     *
     * @param operation
     *            the operation to process
     * @return the result of the processing
     */
    Object process(TerminalQueryOperationNode operation);
    //
    // boolean processBoolean(TerminalQueryOperationNode operation);
    //
    // int processInt(TerminalQueryOperationNode operation);
    //
    // long processLong(TerminalQueryOperationNode operation);
    //
    // double processDouble(TerminalQueryOperationNode operation);
}
