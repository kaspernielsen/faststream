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
package org.cakeframework.internal.db.query.compiler.datasource;

import static io.faststream.codegen.model.expression.Expressions.literal;
import static io.faststream.codegen.model.expression.Expressions.newVar;
import static io.faststream.codegen.model.expression.Expressions.staticMethodInvoke;
import static java.util.Objects.requireNonNull;

import java.util.List;

import org.cakeframework.internal.db.query.compiler.render.util.AbstractOperationNode;
import org.cakeframework.internal.db.query.runtime.ArrayUtil;

import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Literal;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.util.Identifier;

/**
 *
 * @author Kasper Nielsen
 */
public class ArrayOrListComposite extends Composite {

    private boolean isImmutable;

    /** The lower bound. Is of int type. */
    private final Expression lowerBound;// Int Type

    /** The type of contained elements. */
    private final Class<?> type;

    /** The upper bound. Is of int type. */
    private final Expression upperBound;// Int Ype

    ArrayOrListComposite(Class<?> type, Expression accessor, Expression lowerBound, Expression upperBound,
            boolean isImmutable) {
        super(accessor);
        this.type = requireNonNull(type);
        this.lowerBound = requireNonNull(lowerBound);
        this.upperBound = requireNonNull(upperBound);
        this.isImmutable = isImmutable;
    }

    public ArrayOrListComposite cloneIfImmutable(AbstractOperationNode is, BlockStatement b) {
        if (!isImmutable) {
            return this;
        }
        Identifier id = new Identifier(Object[].class, "array");
        if (type.isArray()) {
            is.addImport(ArrayUtil.class);
            b.add(newVar(id, Object[].class, staticMethodInvoke(ArrayUtil.class, "copy", asExpressionIfNonDefault())));
        } else {
            b.add(newVar(id, Object[].class, getAccessor().invoke("toArray()")));
        }
        return create(Object[].class, id);
    }

    public Class<?> getComponentType() {
        if (type.isArray()) {
            return type.getComponentType();
        }
        return Object.class;
    }

    public Expression cloneForReturn(AbstractOperationNode is) {
        is.addImport(ArrayUtil.class);
        return staticMethodInvoke(ArrayUtil.class, "copy", asExpressionIfNonDefault());
    }

    public Expression[] asExpressionIfNonDefault() {
        if (isDefaultBounds()) {
            return new Expression[] { getAccessor() };
        } else {
            return asExpression();
        }
    }

    public Expression[] asExpression() {
        return new Expression[] { getAccessor(), getLowerBound(), getUpperBound() };
    }

    public Expression getElement(Expression index) {
        requireNonNull(index);
        if (type.isArray()) {
            return getAccessor().arrayAccess(index);
        } else {
            return getAccessor().invoke("get", index);
        }
    }

    /**
     * Returns an expression to access the first element.
     *
     * @return an expression to access the first element
     */
    public Expression getFirstElement() {
        return getElement(getLowerBound());
    }

    public Expression getLastElement() {
        return getElement(getUpperBound().minus(new Literal(1)));
    }

    /**
     * @return the lowerBound
     */
    public Expression getLowerBound() {
        return lowerBound;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return type.isArray() ? "a" : "list";
    }

    /**
     * @return the type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @return the upperBound
     */
    public Expression getUpperBound() {
        return upperBound;
    }

    /** @return an expression that can test whether or not this composite is empty or not */
    public Expression isEmpty() {
        if (!type.isArray() /* && hasDefaultBounds() */) {
            return getAccessor().invoke("isEmpty");
        } else {
            return getUpperBound().equalsTo(getLowerBound());
        }
    }

    /**
     * @return the isImmutable
     */
    public boolean isImmutable() {
        return isImmutable;
    }

    public void setImmutable(boolean b) {
        this.isImmutable = b;
    }

    public Expression size() {
        return getUpperBound().minus(getLowerBound()).simplify();
    }

    /** {@inheritDoc} */
    public ArrayOrListComposite withAccessor(Expression e) {
        return new ArrayOrListComposite(type, e, lowerBound, upperBound, isImmutable);
    }

    public ArrayOrListComposite withUpperBounds(Expression upperBound) {
        requireNonNull(upperBound);
        return withBounds(lowerBound, upperBound);
    }

    public ArrayOrListComposite withLowerBounds(Expression lowerBound) {
        return withBounds(lowerBound, upperBound);
    }

    public ArrayOrListComposite withBounds(Expression lowerBound, Expression upperBound) {
        return new ArrayOrListComposite(type, getAccessor(), lowerBound, upperBound, isImmutable);
    }

    public static ArrayOrListComposite create(Class<?> type, Expression accessor) {
        if (type.isArray() || List.class.isAssignableFrom(type)) {// ok
            return new ArrayOrListComposite(type, accessor, new Literal(0), defaultUpperBound(type, accessor), false);
        }
        throw new IllegalArgumentException("'" + type.getCanonicalName() + "' is not a valid type for an "
                + ArrayOrListComposite.class.getSimpleName());
    }

    public boolean isDefaultBounds() {
        return lowerBound.equals(literal(0)) && upperBound.equals(defaultUpperBound(getType(), getAccessor()));
    }

    /**
     * @param accessor
     * @return
     */
    private static Expression defaultUpperBound(Class<?> type, Expression accessor) {
        if (type.isArray()) {
            return accessor.fieldAccess("length");
        } else {
            return accessor.invoke("size");
        }
    }
}

// public BlockStatement define(BlockStatement bs) {
// VariableDeclarator vd = new VariableDeclarator(new NameExpression("i"), 0, lowerBound);
// VariableDeclarationExpression vde = new VariableDeclarationExpression(Type.of(int.class), Arrays.asList(vd));
// return bs;
// }
