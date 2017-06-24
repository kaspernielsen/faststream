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

import static java.util.Objects.requireNonNull;

import io.faststream.codegen.model.expression.Expression;

/**
 *
 * @author Kasper Nielsen
 */
public class StreamingComposite extends Composite implements AnyStreaming {

    private Class<?> type;

    /**
     * @param accessor
     */
    public StreamingComposite(Class<?> type, Expression accessor) {
        super(accessor);
        this.type = requireNonNull(type);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "e"; // e for element
    }

    /**
     * @return the type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public StreamingComposite setType(Class<?> type) {
        this.type = type;
        return this;
    }

    /** {@inheritDoc} */
    public StreamingComposite withAccessor(Expression e) {
        return new StreamingComposite(type, e);
    }
}
