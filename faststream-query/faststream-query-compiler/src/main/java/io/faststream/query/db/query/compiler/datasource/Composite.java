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
package io.faststream.query.db.query.compiler.datasource;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Expressions;

/**
 *
 * @author Kasper Nielsen
 */
// Rename to data container
public abstract class Composite {

    private Expression accessor;

    Map<String, Expressions> parts;

    Composite(Expression accessor) {
        this.accessor = requireNonNull(accessor, "accessor is null");
    }

    /**
     * Returns an accessor expression that can be used to reference the data structure .
     *
     * @return the accessor
     */
    public final Expression getAccessor() {
        return accessor;
    }

    public abstract String getName();

    public abstract Class<?> getType();

    // alle expressions
    // Naar man kommer udefra har man altid kun en part, f.eks SubListImpl, CHMImpl

    /**
     * @param accessor
     *            the accessor to set
     */
    public void setAccessor(Expression accessor) {
        this.accessor = accessor;
    }

}
