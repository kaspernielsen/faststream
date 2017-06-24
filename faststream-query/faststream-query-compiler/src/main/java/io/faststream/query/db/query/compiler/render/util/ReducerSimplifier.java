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
package io.faststream.query.db.query.compiler.render.util;

import io.faststream.codegen.model.body.VariableDeclarator;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.expression.VariableDeclarationExpression;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.visitor.Visitors;
import io.faststream.query.db.query.plan.QueryPlan;

/**
 * Removes unused reducer statements
 *
 * @author Kasper Nielsen
 */
public class ReducerSimplifier {

    private final static String CACHE_KEY = ReducerSimplifier.class.getSimpleName();
    private final static String CACHE_KEY2 = ReducerSimplifier.class.getSimpleName() + "B";

    public static void addTo(QueryPlan tree, BlockStatement bs, VariableDeclarationExpression vdc) {
        tree.putInCache(CACHE_KEY, vdc);
        tree.putInCache(CACHE_KEY2, bs);
    }

    public static void check(QueryPlan tree, BlockStatement b) {
        VariableDeclarationExpression vde = (VariableDeclarationExpression) tree.getFromCache(CACHE_KEY);
        if (vde != null) {
            VariableDeclarator vd = vde.getDeclarators().get(0);
            if (vd.getInit() instanceof NameExpression) {
                BlockStatement bs = (BlockStatement) tree.getFromCache(CACHE_KEY2);
                bs.remove(vde);
                Visitors.replace(tree.getPq().main, vd.getName(), vd.getInit());
            }
        }
    }
}
