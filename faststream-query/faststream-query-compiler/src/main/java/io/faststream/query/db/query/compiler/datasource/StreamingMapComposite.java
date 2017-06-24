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

import io.faststream.codegen.model.expression.Expression;

/**
 *
 * @author Kasper Nielsen
 */
public class StreamingMapComposite extends Composite implements AnyStreaming {

    private final Expression keyAccessor;

    private final Class<?> keyType;
    private final Expression valueAccessor;

    private final Class<?> valueType;

    /**
     * @param accessor
     */
    public StreamingMapComposite(Class<?> keyType, Expression keyAccessor, Class<?> valueType, Expression valueAccessor) {
        super(keyAccessor);
        this.keyType = requireNonNull(keyType);
        this.valueType = requireNonNull(valueType);
        this.keyAccessor = requireNonNull(keyAccessor);
        this.valueAccessor = requireNonNull(valueAccessor);
    }

    /**
     * Returns an accessor expression that can be used to reference the data structure .
     *
     * @return the accessor
     */
    public final Expression getKeyAccessor() {
        return keyAccessor;
    }

    /** {@inheritDoc} */
    public String getKeyName() {
        return "key"; // e for element
    }

    public String getValueName() {
        return "value";
    }

    /**
     * @return the keyType
     */
    public Class<?> getKeyType() {
        return keyType;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "entry";
    }

    /** {@inheritDoc} */
    @Override
    public Class<?> getType() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the valueAccessor
     */
    public Expression getValueAccessor() {
        return valueAccessor;
    }

    /**
     * @return the valueType
     */
    public Class<?> getValueType() {
        return valueType;
    }

    /** {@inheritDoc} */
    public StreamingMapComposite withAccessor(Expression e) {
        throw new UnsupportedOperationException();
    }
}
