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
package org.cakeframework.internal.db.query.node;

/**
 * A terminal query operation is a special type of query operation that is the final operation in a chain. A terminal
 * operation object is never exposed to users.
 *
 * @author Kasper Nielsen
 */
public abstract class TerminalQueryOperationNode extends QueryOperationNode {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of this class
     *
     * @param previous
     *            the predecessor operation
     * @throws NullPointerException
     *             if the specified operation is null
     */
    protected TerminalQueryOperationNode(QueryOperationNode previous) {
        super(previous);
    }

    /**
     * Returns an array with this operation and all its predecessors. <tt>Not</tt> including the root operation.
     *
     * @return an array with this operation and all its predecessors excluding the root operation.
     */
    public final QueryOperationNode[] operations() {
        int depth = 0;
        QueryOperationNode n = previous;
        while (n != null) {
            depth++;
            n = n.previous;
        }

        QueryOperationNode[] operations = new QueryOperationNode[depth];
        n = this;
        for (int i = depth - 1; i >= 0; i--) {
            operations[i] = n;
            n = n.previous;
        }
        return operations;
    }

    /**
     * Called by terminal methods, such as {@link org.cakeframework.util.view.CollectionView#any()} to obtain a result.
     *
     * @return the result of the evaluation
     */
    public final Object process() {
        return processor.process(this);
    }
    //
    // public final boolean processBoolean() {
    // return processor.processBoolean(this);
    // }
    //
    // public final long processLong() {
    // return processor.processLong(this);
    // }
    //
    // public final int processInt() {
    // return processor.processInt(this);
    // }
    //
    // public final double processDouble() {
    // return processor.processDouble(this);
    // }
}
