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
package io.faststream.query.db.query.common.nodes.elements;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import io.faststream.query.db.query.compiler.render.PartialQuery;
import io.faststream.query.db.query.compiler.render.util.AbstractOperationNode;
import io.faststream.query.db.query.util.tree.WalkOrder;

/**
 * I really want to get rid of this one. But right now its here to maintain the original order of operation
 *
 * @author Kasper Nielsen
 */
public abstract class RealNode extends AbstractOperationNode {

    RealNode realNext;

    /**
     * @param realNext
     *            the realNext to set
     */
    public void setRealNext(RealNode realNext) {
        this.realNext = realNext;
    }

    public RealNode getRealNext() {
        return realNext;
    }

    public static RealNode getFirst(PartialQuery plan) {
        return plan.getRoot().find(WalkOrder.IN).first(RealNode.class);
    }

    public static Stream<RealNode> streamAll(PartialQuery plan) {
        RealNode n = getFirst(plan);
        requireNonNull(n);
        List<RealNode> list = new ArrayList<>();
        while (n != null) {
            list.add(n);
            n = n.getRealNext();
        }
        return list.stream();
    }
}
