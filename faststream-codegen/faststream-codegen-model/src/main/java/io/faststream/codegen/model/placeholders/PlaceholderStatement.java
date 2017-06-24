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
package io.faststream.codegen.model.placeholders;

import io.faststream.codegen.model.statement.Statement;
import io.faststream.codegen.model.visitor.CodegenVisitor;
import io.faststream.codegen.model.visitor.ModifyingCodegenVisitor;

/**
 * A special statement that does nothing.
 * 
 * @author Kasper Nielsen
 */
public abstract class PlaceholderStatement extends Statement {

    Statement statement;

    /** {@inheritDoc} */
    @Override
    public void accept(CodegenVisitor visitor) {
        if (statement != null) {
            statement.accept(visitor);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object accept(ModifyingCodegenVisitor visitor) {
        if (statement != null) {
            return statement.accept(visitor);
        }
        return this;
    }

    /**
     * @return the statement
     */
    public Statement getStatement() {
        return statement;
    }

    /**
     * @param statement
     *            the statement to set
     */
    public void setStatement(Statement statement) {
        this.statement = statement;
    }
}
