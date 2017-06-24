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
package io.faststream.query.db.query.plan;

import static java.util.Objects.requireNonNull;

import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.query.db.query.compiler.render.PartialQuery;
import io.faststream.query.db.query.compiler.render.util.TemporaryBlockStatement;
import io.faststream.query.db.query.node.Operation;
import io.faststream.query.db.query.util.tree.OrderedTreeNode;

/**
 * A query node contains information about a specific step in a query plan.
 *
 * @author Kasper Nielsen
 */
public abstract class QueryNode extends OrderedTreeNode<PartialQuery, QueryNode> {

    private BlockStatement in;

    /** The operation of the node. */
    private Operation operation;

    private QueryOperationParameterList parameterList = QueryOperationParameterList.EMPTY;

    private final Sources sources = new Sources();

    public QueryNode() {
        this.operation = Operation.of(getClass().getSimpleName());
    }

    /**
     * Creates a new non root node.
     *
     * @param plan
     *            the plan of the node
     * @param operation
     *            the operation of this node
     */
    public QueryNode(Operation operation) {
        this.operation = requireNonNull(operation);
    }

    public final void analyzeIt(QueryNode next) {}

    /**
     * @return the in
     */
    public final BlockStatement b() {
        if (in == null) {
            return in = getParent().b();
        }
        return in;
    }

    protected abstract void buildModel();

    public QueryNode cloneIt() {
        QueryNode qn = InstalledNodes.clone(this);
        for (QueryNode c : children()) {
            qn.addChild(c.cloneIt());
        }
        qn.setSource(sources);
        qn.setParameterList(parameters());
        return qn;
    }

    /**
     * Returns the operation of this node.
     *
     * @return the operation of this node
     */
    public final Operation getOperation() {
        return operation;
    }

    /**
     * Tests whether the operation of this node is the same as the specified operation.
     *
     * @param operation
     *            the operation to compare with
     * @return true if it is the same operation, otherwise false
     */
    public final boolean is(Operation operation) {
        return this.operation.is(operation);
    }

    public final boolean is(Operation... operations) {
        return operation.isAnyOf(operations);
    }

    /**
     * @return the parameterList
     */
    public final QueryOperationParameterList parameters() {
        return parameterList;
    }

    public BlockStatement firstBlock() {
        return first;
    }

    public final void render() {
        first = new TemporaryBlockStatement();
        b().add(first);
        buildModel();
    }

    public final void renderChildren() {
        for (QueryNode n : children()) {
            n.render();
        }
    }

    /**
     * @param in
     *            the in to set
     */
    public final void setIn(BlockStatement in) {
        this.in = requireNonNull(in);
    }

    public final QueryNode setOperation(Operation tag) {
        this.operation = requireNonNull(tag);
        return this;
    }

    /**
     * @param parameterList
     *            the parameterList to set
     */
    public final void setParameterList(QueryOperationParameterList parameterList) {
        requireNonNull(parameterList);
        this.parameterList = parameterList;
    }

    public final void setSource(Sources sources) {
        this.sources.main = sources.main;
    }

    public final Sources sources() {
        return sources;
    }

    public final String toQueryString() {
        return toString(0, new StringBuilder()).toString();
    }

    @Override
    public String toString() {
        return operation == null ? "NoOp" : operation.toString();
    }

    BlockStatement first;

    final StringBuilder toString(int level, StringBuilder sb) {
        if (level != 0) {
            sb.append("\n");
        }
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        for (QueryNode child : this.children()) {
            child.toString(level + 1, sb);
        }
        return sb;
    }
}
