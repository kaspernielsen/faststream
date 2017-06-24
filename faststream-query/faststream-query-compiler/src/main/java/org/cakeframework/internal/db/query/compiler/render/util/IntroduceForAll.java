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

import org.cakeframework.internal.db.query.common.nodes.elements.CopyToArray;
import org.cakeframework.internal.db.query.common.nodes.elements.ForAll;
import org.cakeframework.internal.db.query.node.defaults.AllQueryOperations;
import org.cakeframework.internal.db.query.plan.QueryNode;
import org.cakeframework.internal.db.query.plan.QueryNodeProcessor;
import org.cakeframework.internal.db.query.util.tree.WalkOrder;

/**
 *
 * @author Kasper Nielsen
 */
public class IntroduceForAll extends QueryNodeProcessor implements AllQueryOperations {

    /** {@inheritDoc} */
    @Override
    public void processNode(QueryNode node) {
        if (node instanceof StreamingGroup) {
            ForAll fa = new ForAll();
            fa.setSource(node.sources());

            node.replace(fa);
            // for (QueryNode n : new ArrayList<>(node.children())) {
            // fa.addChild(n);
            // }
            // node.addChild(fa);

            if (fa.next() != null) {
                CopyToArray cta = new CopyToArray();
                // cta.sets
                QueryNode qn = fa.firstChild().find(WalkOrder.DOWN_AND_IN).last();
                if (qn.is(FILTER, C_FLAT_MAP, C_DISTINCT)) {
                    qn.addChild(cta);
                } else {
                    qn.insertAfterThis(cta);
                }
            }
        }
    }
}
