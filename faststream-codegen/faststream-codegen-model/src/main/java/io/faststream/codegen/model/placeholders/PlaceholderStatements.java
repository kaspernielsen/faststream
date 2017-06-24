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

import java.util.ArrayList;
import java.util.List;

import io.faststream.codegen.model.AbstractASTNode;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.statement.Statement;
import io.faststream.codegen.model.visitor.CodegenVisitor;
import io.faststream.codegen.model.visitor.ModifyingCodegenVisitor;

/**
 * A special statement that does nothing.
 * 
 * @author Kasper Nielsen
 */
public class PlaceholderStatements extends Statement {

    List<Statement> statements = new ArrayList<>();

    public void addStatement(Statement s) {
        statements.add(s);
    }

    /** {@inheritDoc} */
    @Override
    public void accept(CodegenVisitor visitor) {
        for (Statement s : statements) {
            s.accept(visitor);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object accept(ModifyingCodegenVisitor visitor) {
        for (int i = 0; i < statements.size(); i++) {
            statements.set(i, (Statement) (statements.get(i).accept(visitor)));
        }
        return this;
    }

    public static void flattenAll(AbstractASTNode n) {
        n.accept(new CodegenVisitor() {
            public void visit(BlockStatement n) {
                List<Statement> l = n.getStatements();
                int i = 0;
                while (i < l.size()) {
                    Statement s = l.get(i);
                    if (s instanceof PlaceholderStatements) {
                        PlaceholderStatements ps = (PlaceholderStatements) s;
                        for (Statement st : ps.statements) {
                            l.add(i++, st);
                        }
                        l.remove(i);
                    }
                    i++;
                }
                super.visit(n);
            }
        });
    }
}
