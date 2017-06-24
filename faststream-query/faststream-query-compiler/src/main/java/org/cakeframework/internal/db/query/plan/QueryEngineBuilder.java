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

import java.util.ArrayList;
import java.util.List;

import org.cakeframework.internal.db.query.common.rewriter.QueryPlanRewriter;
import org.cakeframework.internal.db.query.plan.logical.QueryPlanLogicalAnalyzer;

/**
 *
 * @author Kasper Nielsen
 */
public class QueryEngineBuilder {

    private QueryPlanLogicalAnalyzer logicalAnalyzer = QueryPlanLogicalAnalyzer.DEFAULT;

    final List<QueryPlan.Processor> processors = new ArrayList<>();

    final List<ModelProcessor> processors2 = new ArrayList<>();

    private QueryPlanRewriter rewriter = QueryPlanRewriter.DEFAULT;

    public QueryEngineBuilder add(ModelProcessor processor) {
        processors2.add(requireNonNull(processor));
        return this;
    }

    public QueryEngineBuilder add(QueryPlan.Processor processor) {
        processors.add(requireNonNull(processor));
        return this;
    }

    /**
     * @return the logicalAnalyzer
     */
    public QueryPlanLogicalAnalyzer getLogicalAnalyzer() {
        return logicalAnalyzer;
    }

    /**
     * @return the processors
     */
    public List<QueryPlan.Processor> getProcessors() {
        return processors;
    }

    /**
     * @return the processors2
     */
    public List<ModelProcessor> getProcessors2() {
        return processors2;
    }

    /**
     * @return the rewriter
     */
    public QueryPlanRewriter getRewriter() {
        return rewriter;
    }

    /**
     * @param logicalAnalyzer
     *            the logicalAnalyzer to set
     */
    public void setLogicalAnalyzer(QueryPlanLogicalAnalyzer logicalAnalyzer) {
        this.logicalAnalyzer = requireNonNull(logicalAnalyzer, "logicalAnalyzer is null");
    }

    /**
     * @param rewriter
     *            the rewriter to set
     */
    public void setRewriter(QueryPlanRewriter rewriter) {
        this.rewriter = rewriter;
    }
}
