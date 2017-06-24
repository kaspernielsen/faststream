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

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.faststream.codegen.model.AbstractASTNode;
import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.statement.Statement;

/**
 *
 * @author Kasper Nielsen
 */
public class Visitors {

    public static void renameDuplicateNameExpressions(BlockStatement bs) {
        bs.accept(new NameCollisionAvoider());
    }

    public static <T extends AbstractASTNode> void forEach(AbstractASTNode root, Class<T> type,
            final Consumer<T> consumer) {
        requireNonNull(consumer, "consumer is null");
        root.accept(new CodegenVisitor() {
            @SuppressWarnings("unchecked")
            public void visitNodeBefore(AbstractASTNode e) {
                if (type.isAssignableFrom(e.getClass())) {
                    consumer.accept((T) e);
                }
            }
        });
    }

    public static void forEachExpression(AbstractASTNode node, final Consumer<Expression> consumer) {
        requireNonNull(consumer, "consumer is null");
        node.accept(new CodegenVisitor() {
            public void visitNodeBefore(AbstractASTNode e) {
                if (e instanceof Expression) {
                    consumer.accept((Expression) e);
                }
            }
        });
    }

    public static void replace(AbstractASTNode node, AbstractASTNode nodeToReplace, AbstractASTNode replaceWith) {
        requireNonNull(nodeToReplace, "nodeToReplace is null");
        requireNonNull(replaceWith, "replaceWith is null");
        node.accept(new ModifyingCodegenVisitor() {
            public AbstractASTNode visitNodeAfter(AbstractASTNode e) {
                return e == nodeToReplace ? replaceWith : e;
            }
        });
    }

    public static void replaceAll(AbstractASTNode node, Map<? extends AbstractASTNode, ? extends AbstractASTNode> map) {
        requireNonNull(map, "map is null");
        node.accept(new ModifyingCodegenVisitor() {
            public AbstractASTNode visitNodeAfter(AbstractASTNode e) {
                AbstractASTNode n = map.get(e);
                return n == null ? e : n;
            }
        });
    }

    public static void replaceStatements(BlockStatement root, Function<Statement, Statement> function) {
        root.accept(new CodegenVisitor() {
            @Override
            public void visit(BlockStatement n) {
                List<Statement> statements = n.getStatements();
                int i = 0;
                while (i < statements.size()) {
                    Statement s = statements.get(i);
                    Statement newS = function.apply(s);
                    if (newS == null) {
                        statements.remove(i);
                    } else {
                        if (newS == s) {
                            s.accept(this);
                        } else {
                            statements.set(i, newS);
                        }
                        i++;
                    }
                }
            }
        });
    }

    public static void removeStatements(BlockStatement root, Set<Statement> statementsToRemove) {
        root.accept(new CodegenVisitor() {
            @Override
            public void visit(BlockStatement n) {
                for (Iterator<Statement> iterator = n.getStatements().iterator(); iterator.hasNext();) {
                    if (statementsToRemove.contains(iterator.next())) {
                        iterator.remove();
                    }
                }
                super.visit(n);
            }
        });
    }

    public static void removeStatements(BlockStatement root, Predicate<Statement> predicate) {
        root.accept(new CodegenVisitor() {
            @Override
            public void visit(BlockStatement n) {
                for (Iterator<Statement> iterator = n.getStatements().iterator(); iterator.hasNext();) {
                    if (predicate.test(iterator.next())) {
                        iterator.remove();
                    }
                }
                super.visit(n);
            }
        });
    }
}
