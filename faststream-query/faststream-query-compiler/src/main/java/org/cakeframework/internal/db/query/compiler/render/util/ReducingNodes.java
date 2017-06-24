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

import org.cakeframework.internal.db.query.common.nodes.elements.ForAll;
import org.cakeframework.internal.db.query.common.nodes.elements.IterateContinue;
import org.cakeframework.internal.db.query.node.Operation;
import org.cakeframework.internal.db.query.node.defaults.AllQueryOperations;
import org.cakeframework.internal.db.query.plan.QueryNode;
import org.cakeframework.internal.db.query.plan.QueryNodeProcessor;
import org.cakeframework.internal.db.query.util.tree.WalkOrder;

/**
 *
 * @author Kasper Nielsen
 */
public class ReducingNodes extends QueryNodeProcessor implements AllQueryOperations {

    public static final Operation CT_REDUCE_LAST_STEP = Operation.of("CT_REDUCE_LAST_STEP", C);

    /** {@inheritDoc} */
    @Override
    public void processNode(QueryNode n) {
        for (QueryNode nn : n.depthFirstTraversal(e -> e.is(CA_REDUCING))) {
            ForAll fa = nn.findFirst(WalkOrder.PREVIOUS_OR_PARENT, ForAll.class);

            ForAll faNew = new IterateContinue();
            for (QueryNode c : fa.children()) {
                faNew.addChild(c.cloneIt());
            }
            nn.addChild(faNew);

            Iterable<QueryNode> depthFirstTraversal = nn.depthFirstTraversal(e -> e != nn && e.is(CA_REDUCING));
            // for (QueryNode nnnb : depthFirstTraversal) {
            // }
            QueryNode next = depthFirstTraversal.iterator().next();
            next.setOperation(CT_REDUCE_LAST_STEP);

            // ForAll fa = nn.findFirst(WalkOrder.PREVIOUS_OR_PARENT, ForAll.class);

            // r.setOperation(ReducingNodes.CT_REDUCE_LAST_STEP);
            // ArrayOrListComposite top = (ArrayOrListComposite) fa.sources().getMain();
            // requireNonNull(fa.getI().getI());
            // faNew.sources().setMain(top.withBounds(fa.getI().getI().plus(new Literal(1)), top.getUpperBound()));

            // ForAll p = (ForAll) nn.getParent();
            //
            // ForAll fa = new ForAll();
            // ArrayOrListComposite ibc = (ArrayOrListComposite) p.sources().getMain();
            // // Det skal jo ikke vaere lowerbound men i man skal bruge
            // ibc = ibc.withBounds(ibc.getLowerBound().plus(literal(1)).simplify(), ibc.getUpperBound());
            //
            // fa.sources().setMain(ibc);
            //
            // QueryNode clone = InstalledNodes.clone(nn);
            // clone.setSource(nn.sources());
            // // ((AbstractReducingOperation) clone).setCloned();
            // fa.addChild(clone);
            //
            // nn.addChild(fa);
        }

    }
    // Lav ogsaa den der tager en filter->shuffle->any og putter paa samme linje
}
