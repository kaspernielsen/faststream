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
package org.cakeframework.internal.db.query.common.nodes.analyze;

import static org.cakeframework.internal.db.query.node.defaults.CollectionQueryOperations.C_FREQUENCY_COUNT;
import static org.cakeframework.internal.db.query.node.defaults.CollectionQueryOperations.C_GROUP_BY;
import static org.cakeframework.internal.db.query.node.defaults.CollectionQueryOperations.C_MAP_TO_INDEX;

import org.cakeframework.internal.db.query.node.Operation;

/**
 *
 * @author Kasper Nielsen
 */
public class OrderAnalyzers extends Analyzers {

    /** {@inheritDoc} */
    @Override
    public void col() {
        col((f, t) -> t.setOrder(f.getOrder()), new Operation[] { null });

        colToMap((f, t) -> t.setOrder(f.getOrder()), C_MAP_TO_INDEX);
        colToMap((f, t) -> t.setOrder(f.getOrder().removeOrdering()), C_FREQUENCY_COUNT);
        colToMulti((f, t) -> {
            t.setKeyOrder(f.getOrder().removeOrdering());
            t.setValueOrder(f.getOrder().removeOrdering());
        }, C_GROUP_BY);
    }

    /** {@inheritDoc} */
    @Override
    public void map() {}

    /** {@inheritDoc} */
    @Override
    public void multi() {}

}
