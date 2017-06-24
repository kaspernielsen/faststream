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

import java.util.ArrayList;

/**
 * A processor that takes a query node and modifies it in some way.
 *
 * @author Kasper Nielsen
 */
public abstract class QueryNodeProcessor {

    /**
     * Processes the node.
     *
     * @param plan
     *            the node to process
     */
    public abstract void processNode(QueryNode node);

    public QueryPlan.Processor processAll() {
        return new QueryPlan.Processor() {
            @Override
            public void process(QueryPlan plan) {
                QueryNode root = plan.getPq().getRoot();
                // We make a copy in case the processor changes the layout
                for (QueryNode n : new ArrayList<>(root.children())) {
                    if (root == n.getParent()) {
                        processNode(n);
                    }
                }
            }
        };
    }
}
