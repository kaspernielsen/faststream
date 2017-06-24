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

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.faststream.query.db.query.common.rewriter.QueryPlanRewriter;
import io.faststream.query.db.query.compiler.datasource.Composite;
import io.faststream.query.db.query.node.TerminalQueryOperationNode;
import io.faststream.query.db.query.plan.logical.QueryPlanLogicalAnalyzer;

/**
 * A query engine generates {@link QueryPlan query plans}. It takes a terminal node and generates a single plan.
 *
 * @author Kasper Nielsen
 */
public class QueryEngine {

    private final QueryPlanLogicalAnalyzer logicalAnalyzer;

    private final AtomicLong plansGenerated = new AtomicLong();

    private final AtomicLong plansNanos = new AtomicLong();

    private final List<QueryPlan.Processor> processors;
    private final List<ModelProcessor> processors2;

    /** An optional rewriter. */
    private final QueryPlanRewriter rewriter;

    /** Creates a new QueryOptimizer. */
    public QueryEngine(QueryEngineBuilder maker) {
        rewriter = maker.getRewriter();
        logicalAnalyzer = maker.getLogicalAnalyzer();
        processors = maker.getProcessors();
        processors2 = maker.getProcessors2();
    }

    /**
     * Finds the best query plan from the specified terminal node of query.
     *
     * @param terminalNode
     *            the terminal node.
     * @return a query plan
     */
    public final QueryPlan createPlan(TerminalQueryOperationNode terminalNode) {
        long start = System.nanoTime();

        // Currently we only know how to create one type of plan.
        // This will change when indexes and joins are supported
        QueryPlan plan = new QueryPlan(this, terminalNode, processors2);

        // originalOperations.addAll(Arrays.asList(terminalNode.operations()).stream().map(e -> e.getOperationPackage())
        // .collect(Collectors.toList()));
        // Do rewriting and logical analysis
        if (rewriter == null) {
            logicalAnalyzer.analyze(plan);
        } else {
            boolean retry = true;
            plan.getDebugger().startRewrite(plan, rewriter);
            while (retry) {
                retry = rewriter.rewrite(plan, false);
                if (!retry) {
                    logicalAnalyzer.analyze(plan);
                    retry = rewriter.rewrite(plan, true);
                }
            }
            plan.getDebugger().stopRewrite(plan, rewriter);
        }

        // set source

        Composite v = plan.pq.getRoot().sources().getMain();
        plan.pq.getRoot().firstChild().sources().setMain(v);

        // create access plan
        for (QueryPlan.Processor p : processors) {
            p.process(plan);
        }

        for (ModelProcessor p : processors2) {
            p.afterAnalyze(plan);
        }
        plansGenerated.incrementAndGet();
        plansNanos.addAndGet(System.nanoTime() - start);
        return plan;
    }

    public final long getNumberOfPlansGenerated() {
        return plansGenerated.get();
    }

    public final long getTotalPlanTime(TimeUnit unit) {
        return unit.convert(plansNanos.get(), TimeUnit.NANOSECONDS);
    }

    public void initRoot(QueryPlan plan) {}
}
