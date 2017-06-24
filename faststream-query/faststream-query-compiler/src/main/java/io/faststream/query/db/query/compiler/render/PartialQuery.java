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
package io.faststream.query.db.query.compiler.render;

import io.faststream.codegen.core.ImportSet;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.query.db.query.plan.QueryNode;
import io.faststream.query.db.query.plan.QueryPlan;
import io.faststream.query.db.query.util.tree.OrderedTree;

/**
 *
 * @author Kasper Nielsen
 */
public class PartialQuery extends OrderedTree<PartialQuery, QueryNode> {
    public BlockStatement main = new BlockStatement();

    private final QueryPlan plan;

    public PartialQuery(QueryPlan plan) {
        this.plan = plan;
    }

    public final ImportSet imports() {
        return getPlan().imports;
    }

    public final void addImport(Class<?> clazz) {
        getPlan().imports.add(clazz);
    }

    public QueryPlan getPlan() {
        return plan;
    }
}
