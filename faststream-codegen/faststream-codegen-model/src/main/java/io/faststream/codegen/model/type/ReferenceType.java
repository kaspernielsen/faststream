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
package io.faststream.codegen.model.type;

import static java.util.Objects.requireNonNull;

import io.faststream.codegen.model.visitor.CodegenVisitor;
import io.faststream.codegen.model.visitor.ModifyingCodegenVisitor;

/**
 *
 * @author Kasper Nielsen
 */
public class ReferenceType extends Type {

    private final int arrayDimensions;

    private final Type type;

    ReferenceType(Type type, int arrayDimensions) {
        this.type = requireNonNull(type);
        this.arrayDimensions = arrayDimensions;
    }

    public void accept(CodegenVisitor visitor) {
        visitor.visit(this);
    }

    /** {@inheritDoc} */
    @Override
    public Object accept(ModifyingCodegenVisitor visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the arrayDimensions
     */
    public int getArrayDimensions() {
        return arrayDimensions;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    public static ReferenceType createArray(Class<?> type) {
        if (type.isPrimitive()) {
            return new ReferenceType(new PrimitiveType(type), 1);
        } else {
            return new ReferenceType(new ClassOrInterfaceType(type), 1);
        }
    }
}
