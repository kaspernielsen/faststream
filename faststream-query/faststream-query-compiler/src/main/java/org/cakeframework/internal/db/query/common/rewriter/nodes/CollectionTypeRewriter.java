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
import static org.cakeframework.internal.db.query.common.rewriter.QueryNodePredicate.nextIsZeroOrMoreOf;
import static org.cakeframework.internal.db.query.common.rewriter.QueryNodePredicate.previousIsOneOf;
import static org.cakeframework.internal.db.query.node.Operation.aggregate;

import org.cakeframework.internal.db.query.common.rewriter.AbstractQueryNodeRewriter;
import org.cakeframework.internal.db.query.node.Operation;
import org.cakeframework.internal.db.query.node.defaults.AllQueryOperations;

/**
 * Simplifies collection view operations.
 *
 * @author Kasper Nielsen
 */
public class CollectionTypeRewriter extends AbstractQueryNodeRewriter implements AllQueryOperations {

    /** The default instance of this query rewriter. */
    public static final CollectionTypeRewriter INSTANCE = new CollectionTypeRewriter();

    public CollectionTypeRewriter() {
        super(C);

        Operation orderShuffle = aggregate(C_FREQUENCY_COUNT, C_GATHER, C_GROUP_BY, C_SORTED, C_SHUFFLE, C_DISTINCT,
                CT_ANY, /* CT_FOR_EACH, C_FILTER, */CT_IS_EMPTY, CT_REDUCE, CT_SIZE, CT_ONE);

        replace2With1(C_FREQUENCY_COUNT, M_VIEW_KEYS, C_DISTINCT);

        // or if the next is a filterType
        // The above one would not catch filterNulls().filterOnType().

        // replace2AndSwapParameters(C_FILTER_NULLS, C_MAP_TO_INDEX_M, C_MAP_TO_INDEX_M, M_FILTER_NULL_VALUE);
        // replace2AndSwapParameters(C_FILTER_ON_TYPE, C_MAP_TO_INDEX_M, C_MAP_TO_INDEX_M, M_FILTER_ON_VALUE_TYPE);

        // eliminate(C_MAP, nextIsOneOf(CT_SIZE, CT_IS_EMPTY));

        eliminate(C_SORTED, nextIsOneOf(CT_MATH_SUM));

        eliminate(C_SORTED, nextIsOneOf(CT_MATH_SUM, CT_MINMAX));

        eliminate(C_AS_OBJECT_STREAM, nextIsOneOf(C));
        // TODO comment in again
        // swapRightAndStealAnyParametersFromFirst(C_MAP, CR_ANY, CT_FIRST, CR_ONE, CT_LAST, C_SHUFFLE, C_TAKE,
        // C_REVERSE);

        // replace2With2AndSwapParameters(C_MAP, C_MAP_TO_INDEX, C_MAP_TO_INDEX, M_MAP_VALUE);

        eliminate2(C_MAP_TO_INDEX, M_VIEW_VALUES);

        // think we already have c_map on the right of ordershuffle
        eliminate(C_SORTED, nextIsZeroOrMoreOf(C_MAP, C_REVERSE).followedByOneOf(orderShuffle));

        replace2With1(C_SORTED_NATURAL, C_REVERSE, C_SORTED_NATURAL_REVERSE);
        replace2With1(C_SORTED_NATURAL_REVERSE, C_REVERSE, C_SORTED_NATURAL);

        replace2With2(C_SORTED_NATURAL_REVERSE, CT_FIRST, C_SORTED_NATURAL, CT_LAST);
        replace2With2(C_SORTED_NATURAL_REVERSE, CT_LAST, C_SORTED_NATURAL, CT_FIRST);

        eliminate(C_REVERSE, nextIsZeroOrMoreOf(C_MAP).followedByOneOf(orderShuffle));
        // nextIsZeroOrMoreOf(C_MAP).followedByOneOf(orderShuffle)
        // m().next().zeroOrMore(C_MAP).next().is(orderShuffle);
        eliminate2(C_REVERSE, C_REVERSE);
        replace2With1(C_REVERSE, CT_LAST, CT_FIRST);
        replace2With1(C_REVERSE, CT_FIRST, CT_LAST);

        eliminate(C_SHUFFLE, nextIsZeroOrMoreOf(C_MAP, M_VIEW_VALUES).followedByOneOf(C_SORTED, C_SHUFFLE, CT_ONE));
        eliminate(C_SHUFFLE, nextIsZeroOrMoreOf(C_FILTER).followedByOneOf(C_SORTED, C_SHUFFLE));

        eliminate(C_DISTINCT, nextIsZeroOrMoreOf(C_MAP, C_ORDERING).followedByOneOf(CT_ANY, CT_FIRST, CT_LAST));

        eliminate(C_DISTINCT, nextIs(C_DISTINCT));

        eliminate(C_DISTINCT, previousIsOneOf(M_VIEW_KEYS, U_VIEW_KEYS));// keys are always unique

        // eliminate(C_ORDER).ifFollowedBy(zeroOrMore(C_FILTER), anyOf(C_ORDER, C_SHUFFLE));// order already eliminated
        // eliminate(anyOf(C_SHUFFLE, C_ORDER)).ifFollowedBy(zeroOrMore(C_FILTER), anyOf(C_ORDER, C_SHUFFLE));
    }
}
