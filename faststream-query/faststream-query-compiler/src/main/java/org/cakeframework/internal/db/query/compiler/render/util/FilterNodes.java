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
package org.cakeframework.internal.db.query.compiler.render.util;

import org.cakeframework.internal.db.query.node.defaults.AllQueryOperations;
import org.cakeframework.internal.db.query.plan.QueryNode;
import org.cakeframework.internal.db.query.plan.QueryNodeProcessor;

/**
 *
 * @author Kasper Nielsen
 */
public class FilterNodes extends QueryNodeProcessor implements AllQueryOperations {

    /** {@inheritDoc} */
    @Override
    public void processNode(QueryNode node) {
        if (node.is(C_PARALLEL, C_SEQUENTIAL, C_UNORDERED)) {
            if (node.hasNext()) {
                node.next().setSource(node.sources());
            }
            node.remove();
        }
    }
}
