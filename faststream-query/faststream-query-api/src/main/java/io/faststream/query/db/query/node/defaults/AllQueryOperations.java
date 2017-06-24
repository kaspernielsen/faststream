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
package io.faststream.query.db.query.node.defaults;

import static io.faststream.query.db.query.node.Operation.aggregate;

import io.faststream.query.db.query.node.Operation;

/**
 * An interface that be implemented to get hold of all the operations available.
 *
 * @author Kasper Nielsen
 */
public interface AllQueryOperations extends CollectionQueryOperations, MapQueryOperations, MultimapOperations,
TableOperations {

    /** All operations. */
    public static final Operation ALL = aggregate(C, M, U, T);

    /** All filter operations. */
    public static final Operation FILTER = aggregate(C_FILTER, M_FILTER, U_FILTER);

    /** All filter operations. */
    public static final Operation SIZE = aggregate(CT_SIZE, MT_SIZE, UT_SIZE);

    /** All filter operations. */
    public static final Operation IS_EMPTY = aggregate(CT_IS_EMPTY, MT_IS_EMPTY, UT_IS_EMPTY);

    /** All iterable operations. */
    public static final Operation ITERABLE = aggregate(CA_ITERABLE, MA_ITERABLE, UA_ITERABLE);

    /** All operations where the size does not change. */
    public static final Operation SIZE_CONSTANT = aggregate(CA_SIZE_CONSTANT, MA_SIZE_CONSTANT, UA_SIZE_CONSTANT);

    /** All operations where the size might decrease. */
    public static final Operation SIZE_SHRINKABLE = aggregate(CA_SIZE_SHRINKABLE, MA_SIZE_SHRINKABLE,
            UA_SIZE_SHRINKABLE);

    /** All operations where the size might decrease but never to zero (isEmpty). */
    public static final Operation SIZE_SHRINKABLE_NOT_EMPTY = aggregate(CA_SIZE_SHRINKABLE_NOT_EMPTY,
            MA_SIZE_SHRINKABLE_NOT_EMPTY, UA_SIZE_SHRINKABLE_NOT_EMPTY);

}
