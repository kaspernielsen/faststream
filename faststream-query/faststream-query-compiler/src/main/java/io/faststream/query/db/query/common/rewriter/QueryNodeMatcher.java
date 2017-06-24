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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import io.faststream.query.db.query.node.Operation;
import io.faststream.query.db.query.plan.QueryNode;

/**
 *
 * @author Kasper Nielsen
 */
public abstract class QueryNodeMatcher implements Predicate<QueryNode> {
    final List<Node> nodes = new ArrayList<>(0);

    // final QueryNode thiz;
    //
    // QueryNodeMatcher(QueryNode thiz) {
    // this.thiz = requireNonNull(thiz);
    // }

    public N next() {
        return new N();
    }

    public N previous() {
        return new N();
    }

    public N child() {
        return new N();
    }

    public N parent() {
        return new N();
    }

    public N thiz() {
        return new N();
    }

    static Predicate<QueryNode> thizIz(Operation e) {
        return p -> p.getOperation().is(e);
    }

    QueryNodeMatcher nextIs(Operation e) {
        return this;
    }

    QueryNodeMatcher nextIgnoreZeroOrMoreOf(Operation e) {
        return this;
    }

    public static class N {
        public QueryNodeMatcher is(Operation e) {
            return null;
        }

        public QueryNodeMatcher zeroOrMore(Operation e) {
            return null;
        }

        // Given den mening child???
        public QueryNodeMatcher ignoreZeroOrOf(Operation e) {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean test(QueryNode t) {
        return false;
    }

    static class Node {
        final BiFunction<QueryNode, Predicate<QueryNode>, QueryNode> mapper;
        final Predicate<QueryNode> predicate;

        /**
         * @param mapper
         * @param predicate
         */
        public Node(BiFunction<QueryNode, Predicate<QueryNode>, QueryNode> mapper, Predicate<QueryNode> predicate) {
            this.mapper = requireNonNull(mapper);
            this.predicate = requireNonNull(predicate);
        }

    }
}
