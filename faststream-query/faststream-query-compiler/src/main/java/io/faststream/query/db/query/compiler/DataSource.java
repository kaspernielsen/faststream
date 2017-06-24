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
package io.faststream.query.db.query.compiler;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Field;

import io.faststream.codegen.core.CodegenUtil;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.query.db.query.compiler.datasource.ArrayOrListComposite;

/**
 * A data source is th
 *
 * @author Kasper Nielsen
 */
public class DataSource {

    /** The name of the data source. */
    private final String name;

    /** The type of the data source. */
    private final Class<?> type;

    /** A name expression to access the data source. */
    private final NameExpression ne;

    /**
     * Creates a new data source.
     *
     * @param name
     *            the name of the data source
     * @param type
     *            the type of the data source
     */
    DataSource(String name, Class<?> type) {
        this.name = CodegenUtil.checkValidJavaIdentifier(name);
        this.type = requireNonNull(type);
        this.ne = new NameExpression(name);
    }

    /**
     * Returns the name of the data source.
     *
     * @return the name of the data source
     */
    public String getName() {
        return name;
    }

    /**
     * @return the type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @param fieldOrMethod
     *            the field or method t
     * @return
     */
    public ArrayOrListComposite createIndexBoundComposite(String fieldOrMethod) {
        if (fieldOrMethod.contains("(")) {
            throw new UnsupportedOperationException("Methods not yet accepted");
        } else {
            try {
                Field f = type.getDeclaredField(fieldOrMethod);
                ArrayOrListComposite ibv = ArrayOrListComposite.create(f.getType(), ne.fieldAccess(f.getName()));
                return ibv;
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Could not find a field with the specified name, name = "
                        + fieldOrMethod, e);
            }
        }
    }
}
