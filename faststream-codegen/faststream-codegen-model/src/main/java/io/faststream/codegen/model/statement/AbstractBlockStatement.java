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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Literal;
import io.faststream.codegen.model.expression.NewInstanceExpression;

/**
 *
 * @author Kasper Nielsen
 */
public abstract class AbstractBlockStatement extends Statement {

    public <T extends Expression> T add(T e) {
        a(new ExpressionStatement(e));
        return e;
    }

    public <T extends Statement> T add(T s) {
        a(s);
        return s;
    }

    public <T extends Expression> T addFirst(T e) {
        aFirst(new ExpressionStatement(e));
        return e;
    }

    public abstract List<Statement> getStatements();

    public <T extends Statement> T addFirst(T s) {
        aFirst(s);
        return s;
    }

    public <T extends Statement> T addBefore(T before, T s) {
        BlockStatement bs = (BlockStatement) this;
        List<Statement> l = bs.getStatements();
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i) == before) {
                l.add(i, s);
                return s;
            }
        }
        return s;
    }

    public ReturnStatement addReturn() {
        return addReturn(null);
    }

    public ReturnStatement addReturn(Expression e) {
        return a(new ReturnStatement(e));
    }

    public ReturnStatement addReturnNull() {
        return addReturn(Literal.nullLiteral());
    }

    public BlockStatement addForBlock(Expression init, Expression compare, Expression update) {
        BlockStatement bs = new BlockStatement();
        new ForStatement(new ArrayList<>(Arrays.asList(init)), compare, new ArrayList<>(Arrays.asList(update)), bs);
        return bs;
    }

    public BlockStatement addIfBlock(Expression condition) {
        BlockStatement bs = new BlockStatement();
        addIf(condition, bs);
        return bs;
    }

    public void addEmptyLine() {
        add(new TextLineStatement(""));
    }

    public void addComment(String comment) {
        add(new TextLineStatement("// " + comment));
    }

    public void addIf(Expression condition, Statement thenStatement) {
        addIf(condition, thenStatement, null);
    }

    public void addIf(Expression condition, Statement thenStatement, Statement elseStatement) {
        a(new IfStatement(condition, thenStatement, elseStatement));
    }

    public void addIf(Expression condition, Statement thenStatement, Expression condition1, Statement thenStatement1) {
        a(new IfStatement(condition, thenStatement, new IfStatement(condition1, thenStatement1, null)));
    }

    public void addIf(Expression condition, Statement thenStatement, Expression condition1, Statement thenStatement1,
            Statement elseStatement1) {
        a(new IfStatement(condition, thenStatement, new IfStatement(condition1, thenStatement1, elseStatement1)));
    }

    public ThrowStatement addThrowNew(Class<? extends Throwable> cause, String message) {
        return addThrowNew(cause, new Literal(message));
    }

    public ThrowStatement addThrowNew(Class<? extends Throwable> cause, Expression message) {
        new NewInstanceExpression(null, null, Arrays.asList(message));

        return new ThrowStatement();
    }

    <T extends Statement> T a(T statement) {
        BlockStatement bs = (BlockStatement) this;
        bs.getStatements().add(statement);
        return statement;
    }

    <T extends Statement> T aFirst(T statement) {
        BlockStatement bs = (BlockStatement) this;
        bs.getStatements().add(0, statement);
        return statement;
    }

    public boolean remove(Statement st) {
        return getStatements().removeIf(e -> e == st);
    }

    public boolean remove(Expression e) {
        return getStatements().removeIf(
                s -> s instanceof ExpressionStatement && ((ExpressionStatement) s).getExpression() == e);
    }

    public Statement last() {
        return getStatements().isEmpty() ? null : getStatements().get(getStatements().size() - 1);
    }
}
