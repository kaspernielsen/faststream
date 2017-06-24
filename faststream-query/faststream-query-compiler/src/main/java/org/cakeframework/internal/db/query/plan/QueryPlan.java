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
package org.cakeframework.internal.db.query.plan;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.cakeframework.internal.db.query.compiler.render.PartialQuery;
import org.cakeframework.internal.db.query.node.QueryOperationNode;
import org.cakeframework.internal.db.query.node.TerminalQueryOperationNode;

import io.faststream.codegen.core.ImportSet;

/**
 * A query plan is an ordered set of steps/operations used to access or modify data in some kind of data structure. A
 * plan is typically produced by a {@link QueryEngine}.
 *
 * @author Kasper Nielsen
 */
public final class QueryPlan {

    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    private final QueryPlanDebugger debugger = new QueryPlanDebugger();

    public ImportSet imports = new ImportSet();

    private final List<ModelProcessor> processors2;

    /** The terminal node of the query. */
    private final TerminalQueryOperationNode terminalNode;

    final PartialQuery pq = new PartialQuery(this);

    /**
     * @return the pq
     */
    public PartialQuery getPq() {
        return pq;
    }

    /**
     * Creates a new plan for the specified terminal node.
     *
     * @param terminalNode
     *            the terminal node of the query
     * @param processors2
     */
    // @SuppressWarnings("serial")
    QueryPlan(QueryEngine planner, TerminalQueryOperationNode terminalNode, List<ModelProcessor> processors2) {
        this.terminalNode = requireNonNull(terminalNode);
        // setRoot(new QueryNode(new AliasedElement("root") {}) {});
        planner.initRoot(this);
        // Create initial nodes
        for (QueryOperationNode n : terminalNode.operations()) {
            QueryNode child = InstalledNodes.newInstance(n.getOperationPackage().getOperation());
            child.setOperation(n.getOperationPackage().getOperation());
            child.setParameterList(QueryOperationParameterList.from(n));
            pq.getRoot().addChild(child);
        }
        this.processors2 = processors2;
        // resets modCount, not strictly necessary but nice to be able to see the exact number of modifications
        pq.modCount = 0;
    }

    public QueryPlanDebugger getDebugger() {
        return debugger;
    }

    public Object getFromCache(String key) {
        return cache.get(key);
    }

    /**
     * Returns how many times the plan has been structurally modified. This is commonly used to see if a
     * {@link QueryPlanProcessor processor} has modified the plan.
     *
     * @return how many times the plan has been structurally modified
     */
    public int getModCount() {
        return pq.modCount;
    }

    /**
     * @return the processors2
     */
    public List<ModelProcessor> getProcessors() {
        return processors2;
    }

    /**
     * Returns the terminal operation of the original query that this plan was created from.
     *
     * @return the query
     */
    public TerminalQueryOperationNode getTerminalOperation() {
        return terminalNode;
    }

    public Object putInCache(String key, Object value) {
        return cache.put(key, value);
    }

    /** {@inheritDoc} */
    public String toString() {
        return pq.getRoot().toQueryString();
    }

    /** A processor that takes a query plan and modifies it in some way. */
    public interface Processor {

        /**
         * Processes the query plan.
         *
         * @param plan
         *            the query plan to process
         */
        void process(QueryPlan plan);

    }
}
// /** Currently all plans have a cost of 1. */
// private final double cost = 1;
//
// /**
// * Returns the relative cost of the plan. Currently all plans have a cost of 1.
// *
// * @return the cost of the plan
// */
// public double getCost() {
// return cost;
// }
