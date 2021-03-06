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
/* 
 * This class was automatically generated by cake.bootstrap.codegen.model.GenerateModel 
 * Available in the https://github.com/cakeframework/cake-developers/ project 
 */
package io.faststream.codegen.model.statement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.visitor.CodegenVisitor;
import io.faststream.codegen.model.visitor.ModifyingCodegenVisitor;

/**
 * This class has been autogenerated
 *
 * @author Kasper Nielsen
 */
public class ExplicitConstructorInvocationStatement extends Statement implements Iterable<Expression> {

    /** Is this or super. */
    private boolean isThis;

    /** The arguments. */
    private List<Expression> expression = new ArrayList<>(2);

    public Iterator<Expression> iterator() {
        return expression.iterator();
    }

    public ExplicitConstructorInvocationStatement() {}

    public ExplicitConstructorInvocationStatement(boolean isThis, List<Expression> expression) {
        this.isThis = isThis;
        this.expression = expression;
    }

    public boolean getIsThis() {
        return isThis;
    }

    public List<Expression> getExpression() {
        return expression;
    }

    public void setIsThis(boolean isThis) {
        this.isThis = isThis;
    }

    public void setExpression(List<Expression> expression) {
        this.expression = expression;
    }

    public void accept(CodegenVisitor visitor) {
        visitor.visit(this);
    }

    public Object accept(ModifyingCodegenVisitor visitor) {
        return visitor.visit(this);
    }

    /** {@inheritDoc} */
    public boolean equals(Object other) {
        return other instanceof ExplicitConstructorInvocationStatement && equals((ExplicitConstructorInvocationStatement) other);
    }

    public boolean equals(ExplicitConstructorInvocationStatement other) {
        return super.equals(this) && Objects.equals(isThis, other.isThis) && Objects.equals(expression, other.expression);
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return Objects.hash(isThis, expression);
    }
}
