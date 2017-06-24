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
package io.faststream.query.view.interpreter;

import io.faststream.query.db.query.node.QueryOperationNode;
import io.faststream.query.db.query.node.TerminalQueryOperationNode;

/**
 * TODO move to api. Og dog vil vi beholde interpreteren??
 *
 * @author Kasper Nielsen
 */
abstract class AbstractSingleNodeViewQueryProcessor {

    /** A token indicating that no result has been set. */
    private static final Object NO_RESULT = new Object();

    /** The next processor. */
    private AbstractSingleNodeViewQueryProcessor nextProcessor = this;

    /** The result of the query, initialized to no result. */
    private Object result = NO_RESULT;

    Object getResult() {
        if (result == NO_RESULT) {
            throw new IllegalStateException("No result");
        }
        return result;
    }

    boolean hasResult() {
        return result != NO_RESULT;
    }

    /**
     * Process the specified node
     *
     * @param node
     *            the node to process
     */
    abstract void process(QueryOperationNode node);

    public final Object run(TerminalQueryOperationNode last) {
        return run(last.operations());
    }

    final Object run(QueryOperationNode[] nodes) {
        AbstractSingleNodeViewQueryProcessor context = this;
        for (int i = 0; i < nodes.length; i++) {
            context.process(nodes[i]);
            if (context.result != NO_RESULT) {
                return context.result;
            }
            context = context.nextProcessor;
        }
        throw new IllegalStateException();
    }

    /**
     * Sets the next processor when processing.
     *
     * @param sharedContext
     *            the next context
     */
    void setNext(AbstractSingleNodeViewQueryProcessor processor) {
        nextProcessor = processor;
    }

    /**
     * Aborts the current processing and returns the specified result.
     *
     * @param result
     *            the result of the processing
     */
    void setResult(Object result) {
        this.result = result;
    }
}
