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
package io.faststream.codegen.model.statement;

import java.util.Arrays;

import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Expressions;
import io.faststream.codegen.model.expression.NameExpression;

/**
 *
 * @author Kasper Nielsen
 */
public class Statements {
    //
    // public static TextLineStatement emptyLine() {
    // return new TextLineStatement("");
    // }
    //
    // public static TextLineStatement comment(String comment) {
    // return new TextLineStatement("// " + comment);
    // }

    public static IfStatement ifs(Expression condition, Expression thenExpression) {
        BlockStatement bs = new BlockStatement();
        bs.add(new ExpressionStatement(thenExpression));
        return new IfStatement(condition, bs, null);
    }

    public static IfStatement ifs(Expression condition, Statement thenStatement) {
        return new IfStatement(condition, thenStatement, null);
    }

    public static IfStatement ifs(Expression condition, Statement thenStatement, Statement elseStatement) {
        return new IfStatement(condition, thenStatement, elseStatement);
    }

    public static ExpressionStatement newVar(NameExpression name, Class<?> type, Expression init) {
        return new ExpressionStatement(Expressions.newVar(name, type, init));
    }

    public static BlockStatement blockOf(Statement... statements) {
        return new BlockStatement(Arrays.asList(statements));
    }

}
