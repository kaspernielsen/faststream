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

import static java.util.Objects.requireNonNull;

import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.FieldAccessExpression;

/**
 * 
 * @author Kasper Nielsen
 */
public class ReflectionTypeResolver extends CodegenVisitor {

    private final Class<?> type;

    ReflectionTypeResolver(Class<?> type) {
        this.type = requireNonNull(type);
    }

    void fail(ReflectiveOperationException roe) {
        throw new RuntimeException(roe);
    }

    /** {@inheritDoc} */
    @Override
    public void visit(FieldAccessExpression n) {
        String name = n.getFieldName();
        try {
            type.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        super.visit(n);
    }

    // ParameterizedType??
    public static Class<?> resolver(Class<?> type, Expression e) {
        ReflectionTypeResolver rtr = new ReflectionTypeResolver(type);
        e.accept(rtr);
        return rtr.type;
    }
}
