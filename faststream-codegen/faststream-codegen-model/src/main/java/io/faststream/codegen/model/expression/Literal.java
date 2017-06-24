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

import static io.faststream.codegen.model.expression.Expressions.literal;

import java.util.Objects;

import io.faststream.codegen.model.visitor.CodegenVisitor;
import io.faststream.codegen.model.visitor.ModifyingCodegenVisitor;

/**
 * A constant such as a long or a string.
 *
 * @author Kasper Nielsen
 */
public final class Literal extends Expression {

    /** The constant */
    private final Object constant;

    /**
     * Creates a new literal.
     *
     * @param constant
     *            the constant
     */
    public Literal(Object constant) {
        this.constant = constant;
    }

    /** {@inheritDoc} */
    @Override
    public void accept(CodegenVisitor visitor) {
        visitor.visit(this);
    }

    /** {@inheritDoc} */
    public boolean equals(Object other) {
        return other instanceof Literal && Objects.equals(constant, ((Literal) other).constant);
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return Objects.hashCode(constant);
    }

    public Object getConstant() {
        return constant;
    }

    protected int hashCode0() {
        return Objects.hashCode(constant);
    }

    public static Literal nullLiteral() {
        return new Literal(null);
    }

    public static Literal identity(Class<?> type) {
        if (type == double.class) {
            return new Literal(Double.valueOf(0d));
        } else if (type == float.class) {
            return new Literal(Float.valueOf(0f));
        } else if (type == int.class) {
            return new Literal(Integer.valueOf(0));
        } else if (type == long.class) {
            return new Literal(Long.valueOf(0L));
        } else {
            return literal(null);
        }
    }

    /** {@inheritDoc} */
    public String toString() {
        if (constant instanceof String) {
            return "\"" + constant + "\"";
        } else if (constant == null) {
            return "null";
        } else if (constant instanceof Long) {
            return constant + "L";
        } else if (constant instanceof Double) {
            return constant + "d";
        } else if (constant instanceof Float) {
            return constant + "f";
        } else {
            return constant.toString();
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object accept(ModifyingCodegenVisitor visitor) {
        return visitor.visit(this);
    }
}
