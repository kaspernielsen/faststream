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
/* 
 * This class was automatically generated by cake.bootstrap.codegen.model.GenerateModel 
 * Available in the https://github.com/cakeframework/cake-developers/ project 
 */
package io.faststream.codegen.model.expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import io.faststream.codegen.model.visitor.CodegenVisitor;
import io.faststream.codegen.model.visitor.ModifyingCodegenVisitor;

/**
 * This class has been autogenerated
 *
 * @author Kasper Nielsen
 */
public class MethodInvocation extends Expression implements Iterable<Expression> {

    /** The scope. */
    private Expression scope;

    /** The name of the method. */
    private String name;

    /** The arguments. */
    private List<Expression> arguments = new ArrayList<>(2);

    public Iterator<Expression> iterator() {
        return arguments.iterator();
    }

    public MethodInvocation() {}

    public MethodInvocation(Expression scope, String name, List<Expression> arguments) {
        this.scope = scope;
        this.name = name;
        this.arguments = arguments;
    }

    public Expression getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public void setScope(Expression scope) {
        this.scope = scope;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArguments(List<Expression> arguments) {
        this.arguments = arguments;
    }

    public void accept(CodegenVisitor visitor) {
        visitor.visit(this);
    }

    public Object accept(ModifyingCodegenVisitor visitor) {
        return visitor.visit(this);
    }

    /** {@inheritDoc} */
    public boolean equals(Object other) {
        return other instanceof MethodInvocation && equals((MethodInvocation) other);
    }

    public boolean equals(MethodInvocation other) {
        return super.equals(this) && Objects.equals(scope, other.scope) && Objects.equals(name, other.name) && Objects.equals(arguments, other.arguments);
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return Objects.hash(scope, name, arguments);
    }
}
