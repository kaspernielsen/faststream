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
package io.faststream.query.db.query.common.rewriter.nodes;

import static io.faststream.query.db.query.common.rewriter.QueryNodePredicate.nextIs;

import io.faststream.query.db.query.common.nodes.elements.MapElement;
import io.faststream.query.db.query.common.nodes.elements.MultimapElement;
import io.faststream.query.db.query.common.nodes.elements.SingleElement;
import io.faststream.query.db.query.common.rewriter.AbstractQueryNodeRewriter;
import io.faststream.query.db.query.node.defaults.AllQueryOperations;

/**
 * A rewriter removing unneeded filtering of nulls if the underlying data structure is known not to contain nulls.
 *
 * @author Kasper Nielsen
 */
public class NullableRewriter extends AbstractQueryNodeRewriter implements AllQueryOperations {

    /** The default instance of this query rewriter. */
    public static final NullableRewriter INSTANCE = new NullableRewriter();

    public NullableRewriter() {
        super(C_FILTER_NULLS, true);

        eliminate(C_FILTER_NULLS, nextIs(C_FILTER_ON_TYPE));

        // eliminate(C_FILTER_NULLS, SingleElement.class, e -> {
        // return !e.isNullable();
        // });
        eliminate(C_FILTER_NULLS, SingleElement.class, e -> !e.isNullable());
        eliminate(M_FILTER_BY_VALUE_NULLS, MapElement.class, e -> !e.getValue().isNullable());
        eliminate(U_FILTER_BY_VALUE_NULLS, MultimapElement.class, e -> !e.getValue().isNullable());
    }
}
