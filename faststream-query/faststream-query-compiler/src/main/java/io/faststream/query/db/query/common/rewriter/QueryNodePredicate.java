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
package io.faststream.query.db.query.common.rewriter;

import java.util.function.Predicate;

import io.faststream.query.db.query.node.Operation;
import io.faststream.query.db.query.plan.QueryNode;

/**
 * A query node predicate used when rewriting.
 *
 * @author Kasper Nielsen
 */
public abstract class QueryNodePredicate implements Predicate<QueryNode> {

    public abstract QueryNodePredicate followedByOneOf(Operation... elements);

    public static QueryNodePredicate nextIs(Operation element) {
        return nextIsOneOf(element);
    }

    public static QueryNodePredicate nextIsOneOf(Operation... elements) {
        final Operation oneOf = Operation.aggregate(elements);
        return new QueryNodePredicate() {
            public boolean test(QueryNode element) {
                return element.next() != null && element.next().is(oneOf);
            }

            public QueryNodePredicate followedByOneOf(Operation... elements) {
                throw new UnsupportedOperationException("Deep nesting not allowed");
            }
        };
    }

    public static QueryNodePredicate previousIs(Operation element) {
        return previousIsOneOf(element);
    }

    public static QueryNodePredicate previousIsOneOf(Operation... elements) {
        final Operation oneOf = Operation.aggregate(elements);
        return new QueryNodePredicate() {
            public boolean test(QueryNode element) {
                return element.previous() != null && element.previous().is(oneOf);
            }

            public QueryNodePredicate followedByOneOf(Operation... elements) {
                throw new UnsupportedOperationException("Deep nesting not allowed");
            }
        };
    }

    public static QueryNodePredicate nextIsZeroOrMoreOf(Operation... elements) {
        final Operation zeroOf = Operation.aggregate(elements);
        return new QueryNodePredicate() {
            public boolean test(QueryNode element) {
                return true;
            }

            public QueryNodePredicate followedByOneOf(Operation... elements) {
                final Operation oneOf = Operation.aggregate(elements);
                return new QueryNodePredicate() {
                    @Override
                    public boolean test(QueryNode element) {
                        if (element.next() == null) {
                            return true;
                        }
                        element = element.next();
                        while (element.is(zeroOf)) {
                            element = element.next();
                        }
                        return element.is(oneOf);
                    }

                    @Override
                    public QueryNodePredicate followedByOneOf(Operation... elements) {
                        throw new UnsupportedOperationException("Deep nesting not allowed");
                    }
                };
            }
        };
    }
}
