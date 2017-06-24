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
package org.cakeframework.internal.db.query.common.rewriter.nodes;

import static org.cakeframework.internal.db.query.common.rewriter.QueryNodePredicate.nextIs;
import static org.cakeframework.internal.db.query.common.rewriter.QueryNodePredicate.nextIsOneOf;
import static org.cakeframework.internal.db.query.node.Operation.aggregate;

import org.cakeframework.internal.db.query.common.rewriter.AbstractQueryNodeRewriter;
import org.cakeframework.internal.db.query.node.defaults.AllQueryOperations;

/**
 * A rewriter removing operations that are not needed to calculate the exact size or whether or a not data structure is
 * empty.
 *
 * @author Kasper Nielsen
 */
public class SizeableRewriter extends AbstractQueryNodeRewriter implements AllQueryOperations {

    /** The default instance of this query rewriter. */
    public static final SizeableRewriter INSTANCE = new SizeableRewriter();

    public SizeableRewriter() {
        super(ALL, true);

        // From CollectionView
        replace2With1(C_MAP_TO_INDEX, MT_SIZE, CT_SIZE);
        replace2With1(C_MAP_TO_INDEX, MT_IS_EMPTY, CT_IS_EMPTY);
        replace2With1(C_FREQUENCY_COUNT, MT_IS_EMPTY, CT_IS_EMPTY);

        replace2With1(C_GROUP_BY, UT_IS_EMPTY, CT_IS_EMPTY);
        replace2With1(C_GROUP_BY, UT_SIZE, CT_SIZE);

        eliminate(CA_SIZE_CONSTANT, nextIs(CT_SIZEABLE));
        eliminate(aggregate(C_GATHER, C_DISTINCT, C_TRUNCATE_TAKE), nextIs(CT_IS_EMPTY));

        // From MapView
        replace2With1(aggregate(M_VIEW_ENTRIES, M_VIEW_KEYS, M_VIEW_VALUES, M_MAP_BI), CT_IS_EMPTY, MT_IS_EMPTY);
        replace2With1(aggregate(M_VIEW_ENTRIES, M_VIEW_KEYS, M_VIEW_VALUES, M_MAP_BI), CT_SIZE, MT_SIZE);

        replace2With1(M_MAP_KEY, UT_IS_EMPTY, MT_IS_EMPTY);
        replace2With1(M_MAP_KEY, UT_SIZE, MT_SIZE);

        eliminate(MA_SIZE_CONSTANT, nextIs(MT_SIZEABLE));
        eliminate(M_TRUNCATE_TAKE, nextIs(MT_IS_EMPTY));

        // From MultimapView
        replace2With1(aggregate(U_VIEW_VALUES, U_VIEW_KEYS, U_MAP_BI), CT_SIZE, MT_SIZE);
        replace2With1(aggregate(U_VIEW_VALUES, U_VIEW_KEYS, U_MAP_BI), CT_IS_EMPTY, MT_IS_EMPTY);
        replace2With1(aggregate(U_REDUCE, U_COUNT, U_DISTINCT, U_ANY, U_FIRST, U_LAST, U_ONE), MT_IS_EMPTY, UT_IS_EMPTY);

        eliminate(UA_SIZE_CONSTANT, nextIsOneOf(UT_SIZEABLE));
        eliminate(U_TRUNCATE_TAKE, nextIs(UT_IS_EMPTY));
    }
}
