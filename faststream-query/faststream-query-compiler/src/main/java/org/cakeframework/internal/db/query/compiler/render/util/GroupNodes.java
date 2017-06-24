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

import java.util.ArrayList;

import org.cakeframework.internal.db.query.node.Operation;
import org.cakeframework.internal.db.query.node.defaults.AllQueryOperations;
import org.cakeframework.internal.db.query.plan.QueryNode;
import org.cakeframework.internal.db.query.plan.QueryPlan;

/**
 *
 * @author Kasper Nielsen
 */
public class GroupNodes implements QueryPlan.Processor, AllQueryOperations {

    public static final Operation FILTER = Operation.aggregate(C_FLAT_MAP, C_FILTER, M_FILTER, U_FILTER, C_DISTINCT);

    // Streng taget skal vi vaere lidt mere forsigte med det her.
    // f.eks. skal
    // map().map() ligge som sieblings
    // map()
    // map()
    // og ikke som parent-child
    // map()
    // --map()
    // i modsaetning til filter() som jo er i sin egen block.

    // Man skal kun breake naar det er den sidste flatmap inde en spliterator/iterator. Ellers skal alt vaere som det
    // plejer

    @Override
    public void process(QueryPlan plan) {

        QueryNode root = plan.getPq().getRoot();
        boolean specielSpliterator = root.lastChild().is(CT_TO_ITERATOR, CT_TO_SPLITERATOR);

        QueryNode lastFlatMap = null;
        if (specielSpliterator) {
            for (QueryNode n : new ArrayList<>(root.children())) {
                if (n.is(C_FLAT_MAP)) {
                    lastFlatMap = n;
                }
            }
        }
        // We make a copy in case the processor changes the layout
        for (QueryNode n : new ArrayList<>(root.children())) {
            if (root == n.getParent()) {
                processNode(n, lastFlatMap);
            }
        }
    }

    // Det er vigtigt naar vi beregner navne
    public void processNode(QueryNode n, QueryNode lastFlatMap) {

        if (!n.is(ITERABLE) || n.is(CA_ITERABLE_NON_STARTING)) {
            return;
        }
        StreamingGroup fa = new StreamingGroup();
        fa.setSource(n.sources());
        n.insertBeforeThis(fa);
        QueryNode right = fa;
        if (n.is(FILTER)) {
            right = n;
        }
        QueryNode next = n.next();

        fa.addChild(n);
        if (!n.is(ITERABLE) || n == lastFlatMap) {
            return;
        }
        // Man skal den som man plejer men skubber iterator ud paa deres egen linje
        while (next != null && next.is(ITERABLE) && next != lastFlatMap) {
            QueryNode nextnext = next.next();
            if (right instanceof StreamingGroup || right.is(FILTER)) {
                right.addChild(next);
            } else {
                right.insertBeforeThis(next);
            }
            if (next.is(FILTER)) {
                right = next;
            }

            next = nextnext;
        }

        // if (next != null && prev != null) {
        // CopyToArray cta = new CopyToArray();
        // // cta.sets
        // if (right == prev) {
        // right.addChild(cta);
        // } else {
        // prev.insertAfterThis(cta);
        // }
        // }
    }
    // /** {@inheritDoc} */
    // public void processNod3e(QueryNode n) {
    // if (n.is(CA_ITERABLE, MA_ITERABLE) && !n.is(CT_ANYFIRSTLAST, CT_SIZEABLE)) {
    // ForAll fa = new ForAll();
    // fa.setSource(n.sources());
    // n.insertBeforeThis(fa);
    // QueryNode right = fa;
    //
    // QueryNode next = n.next();
    // fa.addChild(n);
    // if (n.is(C_FILTER)) {
    // right = n;
    // }
    //
    // while (next != null && next.is(CA_ITERABLE, MA_ITERABLE)) {
    // QueryNode nextnext = next.next();
    //
    // if (right instanceof ForAll || right.is(C_FILTER)) {
    // right.addChild(next);
    // } else {
    // right.insertBeforeThis(next);
    // }
    // if (next.is(C_FILTER)) {
    // right = next;
    // }
    // next = nextnext;
    // }
    // }
    //
    // }
    //
    // public void processNode3(QueryNode n) {
    // if (n.is(CA_ITERABLE, MA_ITERABLE) && !n.is(CT_ANYFIRSTLAST, CT_SIZEABLE)) {
    // ForAll fa = new ForAll();
    // fa.setSource(n.sources());
    // n.insertBeforeThis(fa);
    // QueryNode right = fa;
    //
    // QueryNode next = n.next();
    // fa.addChild(n);
    // if (n.is(C_FILTER)) {
    // right = n;
    // }
    //
    // while (next != null && next.is(CA_ITERABLE, MA_ITERABLE)) {
    // QueryNode nextnext = next.next();
    //
    // if (right instanceof ForAll || right.is(C_FILTER)) {
    // right.addChild(next);
    // } else {
    // right.insertBeforeThis(next);
    // }
    // if (next.is(C_FILTER)) {
    // right = next;
    // }
    // next = nextnext;
    // }
    // }
    //
    // }

    // Lav ogsaa den der tager en filter->shuffle->any og putter paa samme linje
}
