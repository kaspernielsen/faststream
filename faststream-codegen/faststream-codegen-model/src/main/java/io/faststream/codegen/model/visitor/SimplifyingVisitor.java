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
package io.faststream.codegen.model.visitor;

import java.util.Objects;

import io.faststream.codegen.model.AbstractASTNode;
import io.faststream.codegen.model.expression.BinaryExpression;
import io.faststream.codegen.model.expression.EncapsulatedExpression;
import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Literal;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.expression.UnaryExpression;
import io.faststream.codegen.model.expression.BinaryExpression.BiOperator;
import io.faststream.codegen.model.expression.UnaryExpression.UnOperator;

/**
 * A visitor that simplifies the generated code.
 *
 * @author Kasper Nielsen
 */
public class SimplifyingVisitor extends ModifyingCodegenVisitor {

    private AbstractASTNode v(AbstractASTNode a) {
        return (AbstractASTNode) a.accept(this);
    }

    public AbstractASTNode visit(EncapsulatedExpression n) {
        AbstractASTNode e = v(n.getInner());
        if (e instanceof NameExpression) {
            return e;
        }
        return n;
    }

    /** {@inheritDoc} */
    @Override
    public AbstractASTNode visit(UnaryExpression n) {
        Expression e = n.getExpression();
        if (e instanceof EncapsulatedExpression) {
            UnaryExpression.UnOperator o = n.getOperator();
            e = ((EncapsulatedExpression) e).getInner();
            if (e instanceof UnaryExpression) {
                UnaryExpression ee = (UnaryExpression) e;
                if (o == UnOperator.NOT && ee.getOperator() == UnOperator.NOT) {
                    return v(ee.getExpression());
                }
            } else if (e instanceof BinaryExpression) {
                BinaryExpression ee = (BinaryExpression) e;
                if (o == UnOperator.NOT && ee.getOperator() == BiOperator.EQUALS) {
                    ee.setOperator(BiOperator.NOT_EQUALS);
                    return v(ee);
                } else if (o == UnOperator.NOT && ee.getOperator() == BiOperator.NOT_EQUALS) {
                    ee.setOperator(BiOperator.EQUALS);
                    return v(ee);
                }
            }
        }
        return super.visit(n);
    }

    /** {@inheritDoc} */
    @Override
    public AbstractASTNode visit(BinaryExpression n) {
        if (n.getOperator() == BiOperator.PLUS) {
            if (isZeroLiterable(n.getLeft())) {
                return v(n.getRight());
            } else if (isZeroLiterable(n.getRight())) {
                return v(n.getLeft());
            }
        } else if (n.getOperator() == BiOperator.MINUS) {
            if (isZeroLiterable(n.getRight())) {
                return v(n.getLeft());
            }
        }
        return super.visit(n);
    }

    private static boolean isZeroLiterable(Expression e) {
        if (e instanceof Literal) {
            Literal l = (Literal) e;
            return Objects.equals(0, l.getConstant()) || Objects.equals(0L, l.getConstant());
        }
        return false;
    }
}
