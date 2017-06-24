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

import java.util.ArrayList;

import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.statement.Statement;
import io.faststream.codegen.model.visitor.Visitors;
import io.faststream.query.db.query.compiler.render.PartialQuery;

/**
 *
 * @author Kasper Nielsen
 */
public class TemporaryBlockStatement extends BlockStatement {

    public static void cleanup(PartialQuery plan) {
        Visitors.forEach(plan.main, BlockStatement.class, e -> {
            for (Statement s : new ArrayList<>(e.getStatements())) {
                if (s instanceof TemporaryBlockStatement) {
                    for (Statement move : ((TemporaryBlockStatement) s).getStatements()) {
                        e.addBefore(s, move);
                    }
                    e.remove(s);
                }
            }
        });
    }
}
