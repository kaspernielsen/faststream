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
package org.cakeframework.internal.db.query.common.rewriter;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.cakeframework.internal.db.query.node.Operation;
import org.cakeframework.internal.db.query.plan.InstalledNodes;
import org.cakeframework.internal.db.query.plan.QueryNode;
import org.cakeframework.internal.db.query.plan.QueryNodeProcessor;

/**
 *
 * @author Kasper Nielsen
 */
public abstract class AbstractQueryNodeRewriter {

    final Operation elementMatcher;

    final boolean needsLogicalAnalysis;

    private final List<QueryNodeProcessor> transformers = new ArrayList<>();

    protected AbstractQueryNodeRewriter(Operation all) {
        this(all, false);
    }

    protected AbstractQueryNodeRewriter(Operation elementMatcher, boolean needsLogicalAnalysis) {
        this.elementMatcher = requireNonNull(elementMatcher);
        this.needsLogicalAnalysis = needsLogicalAnalysis;
    }

    private <T extends QueryNodeProcessor> void add(T vos) {
        transformers.add(vos);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected final <T extends QueryNode> void eliminate(final Operation operation, Class<T> type,
            final Predicate<? super T> predicate) {
        Predicate p = e -> type.isInstance(e);
        eliminate(operation, p.and(predicate));
    }

    /**
     * Eliminates the specified type of operation if the specified predicate accepts the current node.
     *
     * @param operation
     *            the type of operation to eliminate
     * @param predicate
     *            the predicate that must be accepted by the current node to be eliminated
     */
    protected final void eliminate(final Operation operation, final Predicate<? super QueryNode> predicate) {
        add(new QueryNodeProcessor() {
            /** {@inheritDoc} */
            public void processNode(QueryNode node) {
                if (node.is(operation) && predicate.test(node)) {
                    node.remove();
                }
            }

            /** {@inheritDoc} */
            public String toString() {
                return "Eliminate " + operation;
            }
        });
    }

    /**
     * Eliminated two nodes following each other. If they are of the specified type.
     *
     * @param first
     *            the first type of operation
     * @param second
     *            the second type of operation
     */
    protected final void eliminate2(final Operation first, final Operation second) {
        add(new QueryNodeProcessor() {
            /** {@inheritDoc} */
            public void processNode(QueryNode node) {
                if (node.is(first) && node.hasNext() && node.next().is(second)) {
                    node.next().remove();// next is removed first because we need the current node the get the next node
                    node.remove();
                }
            }

            /** {@inheritDoc} */
            public String toString() {
                return "Eliminate [" + first + ", " + second + "] -> []";
            }
        });
    }

    public final List<QueryNodeProcessor> getTransformers() {
        return transformers;
    }

    public boolean needsLogicalAnalysis() {
        return needsLogicalAnalysis;
    }

    protected final void replace2With1(final Operation operation1, final Operation operation2,
            final Operation replaceWith) {
        add(new QueryNodeProcessor() {
            public void processNode(QueryNode node) {
                if (node.is(operation1) && node.hasNext() && node.next().is(operation2)) {
                    node.insertBeforeThis(newNode(replaceWith));
                    node.next().remove();
                    node.remove();
                }
            }

            public String toString() {
                return "Replace [" + operation1 + ", " + operation2 + "] ->" + replaceWith;
            }
        });

    }

    protected final void replace2With2(final Operation operation1, final Operation operation2,
            final Operation replaceWith1, final Operation replaceWith2) {
        add(new QueryNodeProcessor() {
            public void processNode(QueryNode node) {
                if (node.is(operation1) && node.hasNext() && node.next().is(operation2)) {
                    node.insertBeforeThis(newNode(replaceWith1));
                    node.insertBeforeThis(newNode(replaceWith2));
                    node.next().remove();
                    node.remove();
                }
            }

            public String toString() {
                return "Replace [" + operation1 + ", " + operation2 + "] -> [" + replaceWith1 + ", " + replaceWith2
                        + "]";
            }
        });
    }

    protected final void replace2With2AndSwapParameters(final Operation operation1, final Operation operation2,
            final Operation replaceWith1, final Operation replaceWith2) {
        add(new QueryNodeProcessor() {
            public void processNode(QueryNode node) {
                if (node.is(operation1) && node.next() != null && node.next().is(operation2)) {
                    QueryNode q1 = newNode(replaceWith1);
                    q1.setParameterList(node.next().parameters());
                    // q1.setAttribute(QueryPlan.USER_SPECIFIED_PARAMETERS,
                    // node.next().getAttribute(QueryPlan.USER_SPECIFIED_PARAMETERS));

                    QueryNode q2 = newNode(replaceWith2);
                    q2.setParameterList(node.parameters());
                    // q2.setAttribute(QueryPlan.USER_SPECIFIED_PARAMETERS,
                    // node.getAttribute(QueryPlan.USER_SPECIFIED_PARAMETERS));

                    node.insertBeforeThis(q1);
                    node.insertBeforeThis(q2);
                    node.next().remove();
                    node.remove();
                }
            }

            public String toString() {
                return "Replace&Swap [" + operation1 + ", " + operation2 + "] -> [" + replaceWith1 + ", "
                        + replaceWith2 + "]";
            }
        });
    }

    protected final void swapRightAndStealAnyParametersFromFirst(final Operation left, final Operation... right) {
        for (Operation viewElement : right) {
            replace2With2AndSwapParameters(left, viewElement, viewElement, left);
        }
    }

    static QueryNode newNode(Operation tag) {
        return InstalledNodes.newInstance(tag);
    }
}
