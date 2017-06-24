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
package org.cakeframework.internal.db.query.plan.logical;

import org.cakeframework.internal.db.query.common.nodes.analyze.Analyzers;
import org.cakeframework.internal.db.query.plan.QueryNode;
import org.cakeframework.internal.db.query.plan.QueryPlan;

/**
 * A query plan logical analyzer does various analysis of a query. For example, whether a particular element can be
 * null. Or what it is.
 *
 * @author Kasper Nielsen
 */
public final class QueryPlanLogicalAnalyzer {

    public static final QueryPlanLogicalAnalyzer DEFAULT = new QueryPlanLogicalAnalyzer();

    public void analyze(QueryPlan plan) {
        QueryNode node = plan.getPq().getRoot();
        QueryNode next = node.firstChild();
        while (next != null) {
            Analyzers.analyze(node, next);
            node = next;
            next = next.next();
        }
    }
}

//
// @SuppressWarnings("serial")
// public static final ObjectAttribute<AbstractOperationNode> INITIATOR = new ObjectAttribute<AbstractOperationNode>(
// AbstractOperationNode.class) {};
//
// public void analyzeOld(QueryPlan plan) {
// // First we extract the base logical information which is stored in the root node
// AbstractOperationNode n = (AbstractOperationNode) plan.getRoot().firstChild();
//
// ((AbstractOperationNode) plan.getRoot()).analyzeIt(n);
// while (n.hasNext()) {
// AbstractOperationNode next = (AbstractOperationNode) n.next();
// n.analyzeIt(next);
// n = next;
// }
// }

// // AbstractOperationNode aon = plan.getRoot().getAttribute(INITIATOR);
//
// // n.setLogical(plan.getRoot().getLogical());
//
// // first we create all the variables that information should be stored
// // while ((n = n.next()) != null) {
// // QueryNodeLogicalAnalyst<?> a = get(n.getOperation());
// // n.setLogical(requireNonNull(a.createVariable()));
// // }
//
// /** The default plan rewriter. Uses a ServiceLoader to all {@link QueryNodeLogicalAnalyst} instances. */
// public static final QueryPlanLogicalAnalyzer DEFAULT = new QueryPlanLogicalAnalyzer(
// ServiceLoader.load(QueryNodeLogicalAnalyst.class));

// /** The query node analyzers. */
// private final QueryNodeLogicalAnalyst<?>[] analyzers;
//
// /** A cache from type to analyst. */
// private final ConcurrentHashMap<Tag, QueryNodeLogicalAnalyst<?>> CACHE = new ConcurrentHashMap<>();
//
// /**
// * Creates a new QueryPlanLogicalAnalyzer from the specified node analyzers.
// *
// * @param rewriters
// * the node analyzers to use
// */
// @SuppressWarnings("rawtypes")
// public QueryPlanLogicalAnalyzer(Iterable<? extends QueryNodeLogicalAnalyst> analyzers) {
// this.analyzers = checkIterableForNullsAndCopyToArray("analyzers", QueryNodeLogicalAnalyst.class, analyzers);
// }
//
// /**
// * Creates a new QueryPlanLogicalAnalyzer from the specified node analyzers.
// *
// * @param rewriters
// * the node analyzers to use
// */
// public QueryPlanLogicalAnalyzer(QueryNodeLogicalAnalyst<?>... analyzers) {
// this.analyzers = checkArrayForNullsAndCopy("analyzers", analyzers);
// }

// /**
// * Returns the analyst that can analyze elements of the specified type
// *
// * @param e
// * the type of elements the analyst must be able to analyze
// * @return a matching analyst
// */
// private QueryNodeLogicalAnalyst<?> get(Tag e) {
// QueryNodeLogicalAnalyst<?> a = CACHE.get(e);
// if (a == null) {
// for (QueryNodeLogicalAnalyst<?> la : analyzers) {
// if (la.match(e)) {
// CACHE.put(e, la);
// return la;
// }
// }
// throw new Error(
// "This is a severe internal error. Do not know how to process a node of the following type: " + e);
// }
// return a;
// }
