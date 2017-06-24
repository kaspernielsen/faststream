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

import io.faststream.codegen.model.AbstractASTNode;

/**
 * 
 * @author Kasper Nielsen
 */
public abstract class Type extends AbstractASTNode {

    public static Type of(Class<?> type) {
        Class<?> cl = getRootComponentType(type);
        Type t = cl.isPrimitive() ? new PrimitiveType(cl) : new ClassOrInterfaceType(cl);
        return type.isArray() ? new ReferenceType(t, getNumberOfDimensions(type)) : t;
    }

    private static Class<?> getRootComponentType(Class<?> arrayType) {
        while (arrayType.isArray()) {
            arrayType = arrayType.getComponentType();
        }
        return arrayType;
    }

    static int getNumberOfDimensions(Class<?> arrayType) {
        int dim = 0;
        while (arrayType.isArray()) {
            dim++;
            arrayType = arrayType.getComponentType();
        }
        return dim;
    }

}
