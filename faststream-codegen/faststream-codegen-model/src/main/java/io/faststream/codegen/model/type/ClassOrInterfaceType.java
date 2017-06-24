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

import io.faststream.codegen.model.visitor.CodegenVisitor;
import io.faststream.codegen.model.visitor.ModifyingCodegenVisitor;

/**
 *
 * @author Kasper Nielsen
 */
public class ClassOrInterfaceType extends Type {

    private ClassOrInterfaceType scope;

    String name;

    public ClassOrInterfaceType(Class<?> type) {
        this.name = type.getSimpleName();
    }

    public ClassOrInterfaceType(String name) {
        this.name = name;
    }

    public ClassOrInterfaceType(String scope, String name) {
        this.scope = new ClassOrInterfaceType(scope);
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public void accept(CodegenVisitor visitor) {
        visitor.visit(this);
    }

    /** {@inheritDoc} */
    @Override
    public Object accept(ModifyingCodegenVisitor visitor) {
        return visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    /**
     * @return the scope
     */
    public ClassOrInterfaceType getScope() {
        return scope;
    }

    /**
     * @param scope
     *            the scope to set
     */
    public ClassOrInterfaceType setScope(ClassOrInterfaceType scope) {
        this.scope = scope;
        return this;
    }

}
