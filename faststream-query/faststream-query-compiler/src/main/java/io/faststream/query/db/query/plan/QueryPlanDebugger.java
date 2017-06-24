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

import io.faststream.query.db.query.common.rewriter.AbstractQueryNodeRewriter;
import io.faststream.query.db.query.common.rewriter.QueryPlanRewriter;

/**
 *
 * @author Kasper Nielsen
 */
public class QueryPlanDebugger {
    private final boolean isSilent = true;

    public void startRewrite(QueryPlan plan, QueryPlanRewriter rewriter) {
        if (!isSilent) {
            System.out.println("--------------Rewriting Started--------------");
            System.out.println(plan);
        }
    }

    public void rewrite(QueryPlan plan, AbstractQueryNodeRewriter rewriter, QueryNodeProcessor processor,
            QueryNode node, int previousModCount) {
        if (!isSilent) {
            System.out.println("@@ " + processor + "[" + previousModCount + " -> " + plan.getModCount() + "]");
            System.out.println(plan);
            System.out.println();
        }
    }

    public void stopRewrite(QueryPlan plan, QueryPlanRewriter queryPlanRewriter) {
        if (!isSilent) {
            System.out.println();
            System.out.println(plan);
            System.out.println("--------------Rewriting Stopped--------------");
            System.out.println();
        }
    }
}
