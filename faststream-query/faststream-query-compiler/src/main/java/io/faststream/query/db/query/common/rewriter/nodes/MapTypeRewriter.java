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

import io.faststream.query.db.query.common.rewriter.AbstractQueryNodeRewriter;
import io.faststream.query.db.query.node.defaults.AllQueryOperations;

/**
 * Simplifies map view operations.
 *
 * @author Kasper Nielsen
 */
public class MapTypeRewriter extends AbstractQueryNodeRewriter implements AllQueryOperations {
    public static final MapTypeRewriter INSTANCE = new MapTypeRewriter();

    public MapTypeRewriter() {
        super(M);

        replace2With2AndSwapParameters(M_FILTER_BY_KEY_PREDICATE, M_VIEW_KEYS, M_VIEW_KEYS, C_FILTER_PREDICATE);
        replace2With2AndSwapParameters(M_FILTER_BY_KEY_TYPE, M_VIEW_KEYS, M_VIEW_KEYS, C_FILTER_ON_TYPE);
        replace2With2AndSwapParameters(M_FILTER_BY_VALUE_PREDICATE, M_VIEW_VALUES, M_VIEW_VALUES, C_FILTER_PREDICATE);
        replace2With2AndSwapParameters(M_FILTER_BY_VALUE_TYPE, M_VIEW_VALUES, M_VIEW_VALUES, C_FILTER_ON_TYPE);
        replace2With2AndSwapParameters(M_FILTER_BY_VALUE_NULLS, M_VIEW_VALUES, M_VIEW_VALUES, C_FILTER_NULLS);

        // cannot swap with head/tail, consider orderDescending().mapToIndex().head() -> order().tail().mapToIndex()
        // swapRight(C_MAP_TO_INDEX_M).ifFollowedBy(CR_ANY);
        // replaceAndStealAnyParametersFromFirst(M_MAP_C, C_SHUFFLE, M_SHUFFLE, M_MAP_C);
        // replace(M_VALUES, C_SHUFFLE, M_SHUFFLE, M_VALUES);
        // replace(M_ENTRIES_C, C_SHUFFLE, M_SHUFFLE, M_ENTRIES_C);

        // swapRightAndStealAnyParametersFromFirst(M_MAP_VALUE, MR_ANY, MR_HEAD, MR_ONE, M_SHUFFLE, MR_TAIL);

        replace2With1(M_MAP_VALUE, M_VIEW_KEYS, M_VIEW_KEYS);
        replace2With2(M_SORTED_BY_KEY_NATURAL, M_VIEW_KEYS, M_VIEW_KEYS, C_SORTED_NATURAL);
        replace2With2AndSwapParameters(M_SORTED_BY_KEY_COMPARATOR, M_VIEW_KEYS, M_VIEW_KEYS, C_SORTED_COMPARATOR);
        replace2With2(M_SORTED_BY_KEY_NATURAL_REVERSE, M_VIEW_KEYS, M_VIEW_KEYS, C_SORTED_NATURAL_REVERSE);

        replace2With2AndSwapParameters(M_MAP_VALUE, M_VIEW_VALUES, M_VIEW_VALUES, C_MAP_TO_OBJECT);

        replace2With2(M_SORTED_BY_VALUE_NATURAL, M_VIEW_VALUES, M_VIEW_VALUES, C_SORTED_NATURAL);
        replace2With2AndSwapParameters(M_SORTED_BY_VALUE_COMPARATOR, M_VIEW_VALUES, M_VIEW_VALUES, C_SORTED_COMPARATOR);
        replace2With2(M_SORTED_BY_VALUE_NATURAL_REVERSE, M_VIEW_VALUES, M_VIEW_VALUES, C_SORTED_NATURAL_REVERSE);
    }
}
