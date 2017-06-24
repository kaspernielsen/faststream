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
package io.faststream.codegen.model.visitor;

import java.util.ArrayDeque;

import io.faststream.codegen.model.body.VariableDeclarator;
import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.expression.VariableDeclarationExpression;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.statement.ExpressionStatement;
import io.faststream.codegen.model.statement.ForStatement;
import io.faststream.codegen.model.statement.Statement;

/**
 * Makes sure that names of identifiers does not collide. It works by renaming any {@link NameExpression name
 * expressions} that have the same name as a previous encountered name expression with the same scope.
 *
 * @author Kasper Nielsen
 */
// Vi kunne godt flytte denne til modellen
// Og saa lave nogle tests
// RenameDuplicateIdentifiers

class NameCollisionAvoider extends CodegenVisitor {

    /** Names that have already been taken in the current scope. */
    private final ArrayDeque<String> taken = new ArrayDeque<>();

    /** {@inheritDoc} */
    @Override
    public void visit(BlockStatement n) {
        int size = 0;
        for (Statement s : n) {
            if (s instanceof ExpressionStatement) {
                Expression e = ((ExpressionStatement) s).getExpression();
                size += checkExpression(e);
            }
            s.accept(this);
        }
        for (int i = 0; i < size; i++) {
            taken.pollLast();
        }
    }

    int checkExpression(Expression e) {
        int count = 0;
        if (e instanceof VariableDeclarationExpression) {
            VariableDeclarationExpression v = (VariableDeclarationExpression) e;
            for (VariableDeclarator d : v) {
                String name = d.getName().getName();
                int c = 0;
                while (taken.contains(name)) {
                    // if it is a single character value such as i, we use i,j,k,l,..
                    // Else we will add an incrementing integer
                    if (name.length() == 1 && !name.equals("z")) {
                        name = Character.toString(((char) (name.charAt(0) + 1)));
                    } else {
                        name = d.getName().getName() + ++c;
                    }
                }
                // System.out.println("REnamed " + d.getName().getName() + "  -> " + name);
                d.getName().setName(name);
                taken.add(name);
                count++;
            }
        }
        return count;
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ForStatement n) {
        int size = 0;
        for (Expression e : n.getInit()) {
            size += checkExpression(e);
        }

        n.getBody().accept(this);
        for (int i = 0; i < size; i++) {
            taken.pollLast();
        }
    }

    // static String n(Object nn) {
    // return nn + "[" + System.identityHashCode(nn) + "]";
    // }
}
