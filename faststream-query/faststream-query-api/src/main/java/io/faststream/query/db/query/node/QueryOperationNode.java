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

import java.io.Serializable;

/**
 * An abstract base class for a single operation in a query.
 *
 * @author Kasper Nielsen
 */
public abstract class QueryOperationNode implements Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The predecessor operation, or {@code null}if this is a root operation. */
    final QueryOperationNode previous;

    /**
     * The processor that will process the final query.
     * <p>
     * It is transient since it is assumed that a remote host will use another processor for a serialized query.
     */
    final transient TerminalQueryOperationNodeProcessor processor;

    /**
     * Creates a new operation with the specified operation as the predecessor.
     *
     * @param previous
     *            the predecessor operation
     */
    protected QueryOperationNode(QueryOperationNode previous) {
        this.previous = previous;
        this.processor = previous.processor;
    }

    /**
     * Creates a new operation that will use the specified processor for processing. The operation does not have a
     * predecessor
     *
     * @param processor
     *            the processor
     */
    protected QueryOperationNode(TerminalQueryOperationNodeProcessor processor) {
        this.previous = null;
        this.processor = processor;
    }

    QueryOperationNode() {
        this.previous = null;
        this.processor = (TerminalQueryOperationNodeProcessor) this;

    }

    /**
     * Returns the first parameter for the operation. Must be overridden by subclasses that accepts a parameter
     * (function). Otherwise returns {@code null}.
     *
     * @return the first parameter
     */
    protected Object getFirstParameter() {
        return null;
    }

    /**
     * Returns a unique integer id
     *
     * @return a unique integer id from for this operation type. This is mainly used in the caching sub system. Negative
     *         values are root operations
     */
    public int getNodeId() {
        throw new UnsupportedOperationException();
    }

    public int getNodeType() {
        throw new UnsupportedOperationException();
    }

    public QueryOperationNodeDefinition getOperationPackage() {
        throw new UnsupportedOperationException();
    }

    protected abstract String name();

    public final QueryOperationNode root() {
        QueryOperationNode qo = this;
        while (qo.previous != null) {
            qo = qo.previous;
        }
        return qo;
    }

    /** @return the operation that this operation was created from, or null if it is a root operation */
    public final QueryOperationNode previous() {
        return previous;
    }

    /**
     * Returns a representation of this query that is suitable as a name for a file.
     *
     * @return a representation of this query as a file name
     */
    public String toFileName() {
        return previous == null ? "" : toFileName(new StringBuilder()).toString();
    }

    /**
     * A recursive helper method for {@link #toFileName()}.
     *
     * @param sb
     *            the string builder
     * @return the specified string builder
     */
    private StringBuilder toFileName(StringBuilder sb) {
        if (previous.previous != null) {
            previous.toFileName(sb);
        }
        sb.append(capitalizeFirstLetter(name()));
        return sb;
    }

    /** {@inheritDoc} */
    public final String toString() {
        return previous == null ? "" : toString(new StringBuilder()).toString();
    }

    /**
     * A recursive helper method for {@link #toString()}.
     *
     * @param sb
     *            the string builder
     * @return the specified string builder
     */
    private StringBuilder toString(StringBuilder sb) {
        if (previous.previous != null) {
            previous.toString(sb);
            sb.append(".");
        }
        sb.append(name()).append("()");
        return sb;
    }

    /**
     * Returns a new string where the first letter of the specified string is capitalized.
     *
     * @param str
     *            the string to capitalize
     * @return the string to capitalize
     */
    static String capitalizeFirstLetter(String str) {
        if (str.length() > 0) {
            return replaceCharAt(str, 0, Character.toUpperCase(str.charAt(0)));
        }
        return str;
    }

    static String replaceCharAt(String s, int pos, char c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }
}
