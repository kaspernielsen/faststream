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
package io.faststream.codegen.model.util;

import static java.util.Objects.requireNonNull;

import io.faststream.codegen.model.expression.NameExpression;

/**
 * An identifier is just a special name expression with an attached type.
 * 
 * @author Kasper Nielsen
 */
public class Identifier extends NameExpression {

    private final Class<?> type;

    public Identifier(Class<?> type, String name) {
        super(requireNonNull(name));
        this.type = requireNonNull(type);
    }

    /**
     * @return the type
     */
    public Class<?> getType() {
        return type;
    }
}
