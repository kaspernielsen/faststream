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
package io.faststream.codegen.model.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import io.faststream.codegen.core.CodegenUtil;
import io.faststream.codegen.model.AbstractASTNode;
import io.faststream.codegen.model.expression.AssignExpression.AsOperator;
import io.faststream.codegen.model.expression.BinaryExpression.BiOperator;
import io.faststream.codegen.model.expression.UnaryExpression.UnOperator;
import io.faststream.codegen.model.statement.ExpressionStatement;
import io.faststream.codegen.model.statement.ReturnStatement;
import io.faststream.codegen.model.statement.Statement;
import io.faststream.codegen.model.type.Type;
import io.faststream.codegen.model.visitor.SimplifyingVisitor;

/**
 *
 * @author Kasper Nielsen
 */
public abstract class Expression extends AbstractASTNode {

    public UnaryExpression not() {
        return new UnaryExpression(this, UnaryExpression.UnOperator.NOT);
    }

    public AssignExpression assign(Object value) {
        return assign(new Literal(value));
    }

    public AssignExpression assign(Expression other) {
        return new AssignExpression(this, AssignExpression.AsOperator.ASSIGN, other);
    }

    public BinaryExpression divide(Expression other) {
        return new BinaryExpression(this, BinaryExpression.BiOperator.DIVIDE, other);
    }

    public BinaryExpression minus(Expression other) {
        return new BinaryExpression(this, BinaryExpression.BiOperator.MINUS, other);
    }

    public BinaryExpression notEquals(Expression other) {
        return new BinaryExpression(this, BinaryExpression.BiOperator.NOT_EQUALS, other);
    }

    public UnaryExpression preIncrement() {
        return new UnaryExpression(this, UnOperator.PRE_INCREMENT);
    }

    public UnaryExpression postIncrement() {
        return new UnaryExpression(this, UnOperator.POST_INCREMENT);
    }

    public AssignExpression plusAssign(Expression other) {
        return new AssignExpression(this, AsOperator.ASSIGN_PLUS, other);
    }

    public BinaryExpression plus(Expression other) {
        return new BinaryExpression(this, BinaryExpression.BiOperator.PLUS, other);
    }

    public BinaryExpression greaterThen(Expression other) {
        return new BinaryExpression(this, BiOperator.GREATER_THEN, other);
    }

    public BinaryExpression greaterThenOrEquals(Expression other) {
        return new BinaryExpression(this, BiOperator.GREATER_THEN_OR_EQUALS, other);
    }

    public BinaryExpression lessThen(Expression other) {
        return new BinaryExpression(this, BiOperator.LESS_THEN, other);
    }

    public EncapsulatedExpression encapsulate() {
        return new EncapsulatedExpression(this);
    }

    public ArrayAccessExpression arrayAccess(Expression... indexes) {
        return new ArrayAccessExpression(this, new ArrayList<>(Arrays.asList(indexes)));
    }

    public Expression box(Class<?> type) {
        // new ArrayList<>(Collections.singletonList(this)) is because singleton lists behave badly.
        // With modifying visitors (because they are immutable)
        return new MethodInvocation(new NameExpression(CodegenUtil.boxClass(type).getSimpleName()), "valueOf",
                new ArrayList<>(Collections.singletonList(this)));
    }

    public Expression lazyBox(Class<?> type) {
        if (type.isPrimitive()) {
            // new ArrayList<>(Collections.singletonList(this)) is because singleton lists behave badly.
            // With modifying visitors (because they are immutable)
            return new MethodInvocation(new NameExpression(CodegenUtil.boxClass(type).getSimpleName()), "valueOf",
                    new ArrayList<>(Collections.singletonList(this)));
        }
        return this;
    }

    public Expression unbox(Class<?> type) {
        // new ArrayList<>(Collections.singletonList(this)) is because singleton lists behave badly.
        // With modifying visitors (because they are immutable)\
        String name = CodegenUtil.unboxClass(type).getSimpleName() + "Value";
        return new MethodInvocation(this, name, Collections.emptyList());
    }

    public CastExpression cast(Type type) {
        return new CastExpression(type, this);
    }

    public CastExpression cast(Class<?> type) {
        return new CastExpression(Type.of(type), this);
    }

    public Expression castAndBox(Class<?> type) {
        return new CastExpression(Type.of(type), this).box(type); // .box(type);
    }

    public FieldAccessExpression fieldAccess(String fieldName) {
        return new FieldAccessExpression(this, fieldName);
    }

    public MethodInvocation invoke(String name, Expression... arguments) {
        return new MethodInvocation(this, name, new ArrayList<>(Arrays.asList(arguments)));
    }

    public Expression equalsTo(Expression other) {
        return new BinaryExpression(this, BinaryExpression.BiOperator.EQUALS, other);
    }

    public Expression simplify() {
        SimplifyingVisitor v = new SimplifyingVisitor();
        return (Expression) this.accept(v);
    }

    public Statement returnIt() {
        return new ReturnStatement(this);
    }

    public Statement statement() {
        return new ExpressionStatement(this);
    }
}
