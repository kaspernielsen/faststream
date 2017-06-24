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

import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Expressions;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.statement.Statement;

/**
 *
 * @author Kasper Nielsen
 */
public class StreamingGroup extends AbstractOperationNode {

    public NameExpression cache(Class<?> type, NameExpression name, Expression init) {
        Expression e = Expressions.newVar(name, type, init);
        bAddFirst(e);
        return name;
    }

    public final void bAddFirst(Expression e) {
        bAddFirst(e.statement());
    }

    public final void bAddFirst(Statement statement) {
        firstBlock().add(statement);
    }

    public final void bReturnLast(Expression e) {
        getTree().main.add(e.returnIt());
    }

    /** {@inheritDoc} */
    @Override
    public void buildModel() {
        firstChild().setSource(sources());
        renderChildren();
    }
}
