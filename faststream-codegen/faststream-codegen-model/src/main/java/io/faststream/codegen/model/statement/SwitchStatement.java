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
package io.faststream.codegen.model.statement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.visitor.CodegenVisitor;
import io.faststream.codegen.model.visitor.ModifyingCodegenVisitor;

/**
 * This class has been autogenerated
 *
 * @author Kasper Nielsen
 */
public class SwitchStatement extends Statement implements Iterable<SwitchEntryStatement> {

    /** The select expression. */
    private Expression select;

    /** Each entry in the the switch table. */
    private List<SwitchEntryStatement> entries = new ArrayList<>(2);

    public Iterator<SwitchEntryStatement> iterator() {
        return entries.iterator();
    }

    public SwitchStatement() {}

    public SwitchStatement(Expression select, List<SwitchEntryStatement> entries) {
        this.select = select;
        this.entries = entries;
    }

    public Expression getSelect() {
        return select;
    }

    public List<SwitchEntryStatement> getEntries() {
        return entries;
    }

    public void setSelect(Expression select) {
        this.select = select;
    }

    public void setEntries(List<SwitchEntryStatement> entries) {
        this.entries = entries;
    }

    public void accept(CodegenVisitor visitor) {
        visitor.visit(this);
    }

    public Object accept(ModifyingCodegenVisitor visitor) {
        return visitor.visit(this);
    }

    /** {@inheritDoc} */
    public boolean equals(Object other) {
        return other instanceof SwitchStatement && equals((SwitchStatement) other);
    }

    public boolean equals(SwitchStatement other) {
        return super.equals(this) && Objects.equals(select, other.select) && Objects.equals(entries, other.entries);
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return Objects.hash(select, entries);
    }
}
