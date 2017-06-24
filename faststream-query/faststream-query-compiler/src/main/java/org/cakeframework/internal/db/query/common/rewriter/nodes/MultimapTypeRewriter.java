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

import static org.cakeframework.internal.db.query.node.defaults.MapQueryOperations.M_VIEW_KEYS;
import static org.cakeframework.internal.db.query.node.defaults.MultimapOperations.U;
import static org.cakeframework.internal.db.query.node.defaults.MultimapOperations.U_ANY;
import static org.cakeframework.internal.db.query.node.defaults.MultimapOperations.U_COUNT;
import static org.cakeframework.internal.db.query.node.defaults.MultimapOperations.U_FIRST;
import static org.cakeframework.internal.db.query.node.defaults.MultimapOperations.U_LAST;
import static org.cakeframework.internal.db.query.node.defaults.MultimapOperations.U_VIEW_KEYS;

import org.cakeframework.internal.db.query.common.rewriter.AbstractQueryNodeRewriter;

/**
 * * Simplifies multimap view operations.
 *
 * @author Kasper Nielsen
 */
public class MultimapTypeRewriter extends AbstractQueryNodeRewriter {
    public static final MultimapTypeRewriter INSTANCE = new MultimapTypeRewriter();

    public MultimapTypeRewriter() {
        super(U);

        replace2With1(U_ANY, M_VIEW_KEYS, U_VIEW_KEYS);
        replace2With1(U_FIRST, M_VIEW_KEYS, U_VIEW_KEYS);
        replace2With1(U_LAST, M_VIEW_KEYS, U_VIEW_KEYS);

        replace2With1(U_COUNT, M_VIEW_KEYS, U_VIEW_KEYS);

        // eliminate(U_TRUNCATE_TAKE, nextIs(UT_IS_EMPTY));

        // replace2With1(U_COUNT, MT_IS_EMPTY, UT_IS_EMPTY);

        // replace2With1(U_MAP_TO_VALUE, CT_IS_EMPTY, UT_IS_EMPTY);
        // eliminate(U_DISTINCT, nextIs(UT_IS_EMPTY));
    }
}
