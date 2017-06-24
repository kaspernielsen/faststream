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
package io.faststream.query.db.queryold.node;

import io.faststream.query.db.query.node.QueryOperationNodeDefinition;
import io.faststream.query.db.query.node.defaults.AllQueryOperations;

/**
 *
 * @author Kasper Nielsen
 */
class QueryOperationTestDefinitions implements AllQueryOperations {
    static final QueryOperationNodeDefinition DEF1 = QueryOperationNodeDefinition.create(C_FILTER);

    static final QueryOperationNodeDefinition DEF2 = QueryOperationNodeDefinition.create(C_FILTER);

    static final QueryOperationNodeDefinition DEF3 = QueryOperationNodeDefinition.create(C_FILTER);

}
