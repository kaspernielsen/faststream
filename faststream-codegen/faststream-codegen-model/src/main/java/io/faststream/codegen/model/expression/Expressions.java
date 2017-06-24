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

import java.util.Arrays;

import io.faststream.codegen.model.body.VariableDeclarator;
import io.faststream.codegen.model.type.Type;

/**
 *
 * @author Kasper Nielsen
 */
public class Expressions {

    public static NameExpression var(String name) {
        return new NameExpression(name);
    }

    public static Literal literal(Object o) {
        return new Literal(o);
    }

    public static VariableDeclarationExpression newVar(NameExpression name, Class<?> type, Expression init) {
        return newVar(name, Type.of(type), init);
    }

    public static VariableDeclarationExpression newVar(NameExpression name, Type type, Expression init) {
        VariableDeclarator vd = new VariableDeclarator(name, 0, init);
        return new VariableDeclarationExpression(type, Arrays.asList(vd));
    }

    public static FieldAccessExpression staticField(Class<?> type, String name) {
        NameExpression ne = new NameExpression(type.getSimpleName());
        return ne.fieldAccess(name);
    }

    public static MethodInvocation staticMethodInvoke(Class<?> type, String name, Expression... arguments) {
        NameExpression ne = new NameExpression(type.getSimpleName());
        return ne.invoke(name, arguments);
    }
}
