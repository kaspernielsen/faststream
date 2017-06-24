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
package io.faststream.query.db.query.compiler.render.util;

import static io.faststream.codegen.model.expression.Expressions.staticMethodInvoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.query.db.query.common.nodes.elements.CopyToArray;
import io.faststream.query.db.query.common.nodes.elements.ForAll;
import io.faststream.query.db.query.common.nodes.elements.RealNode;
import io.faststream.query.db.query.common.nodes.elements.SingleElement;
import io.faststream.query.db.query.node.defaults.AllQueryOperations;
import io.faststream.query.db.query.plan.ModelProcessor;
import io.faststream.query.db.query.plan.QueryNode;
import io.faststream.query.db.query.plan.QueryPlan;
import io.faststream.query.db.query.runtime.ArrayUtil;
import io.faststream.query.db.query.util.tree.WalkOrder;

/**
 *
 * @author Kasper Nielsen
 */
public class IntroduceArrays implements AllQueryOperations, ModelProcessor {

    /** {@inheritDoc} */
    @Override
    public void afterAnalyze(QueryPlan plan) {
        final List<Array> last = new ArrayList<>();

        final List<Array> resusable = new ArrayList<>();

        List<QueryNode> c = plan.getPq().getRoot().children();
        boolean first = true;

        // System.out.println("------");
        // plan.print(e -> e.toString());

        for (int i = 0; i < c.size(); i++) {
            QueryNode n = c.get(i);
            CopyToArray cpa = null;
            if (n.children().size() > 0) {
                cpa = (CopyToArray) n.firstChild().find(WalkOrder.DOWN_AND_IN).first(e -> e instanceof CopyToArray);
            }
            // System.out.println("A " + n + "  " + cpa);
            if (cpa != null) {
                RealNode prev = (RealNode) cpa.find(WalkOrder.PREVIOUS_OR_PARENT).first();
                Class<?> tmp = ((SingleElement) prev.getRealNext()).getElement().getType();
                Class<?> t = tmp.isPrimitive() ? tmp : Object.class;
                if (first) {
                    cpa.ar = new Array(t);
                } else {
                    boolean allSizeable = allSizeable(cpa);
                    // if allSizeable we can use last one, else we must look longer back
                    if (allSizeable) {
                        Optional<Array> o = last.stream().filter(e -> e.componentType == t).findAny();
                        if (o.isPresent()) {
                            cpa.ar = o.get();
                            cpa.reused = true;
                            last.remove(o.get());
                        }
                    }
                    if (cpa.ar == null) {
                        Optional<Array> o = resusable.stream().filter(e -> e.componentType == t).findAny();
                        if (o.isPresent()) {
                            cpa.ar = o.get();
                            cpa.reused = true;
                            resusable.remove(o.get());
                        }
                    }
                    if (cpa.ar == null) {
                        cpa.ar = new Array(t);
                    }
                }

                resusable.addAll(last);
                last.clear();
                last.add(cpa.ar);

                first = false;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void afterBuild(QueryPlan plan) {
        // for (QueryNode g : plan.getRoot().depthFirstTraversal(e -> e instanceof StreamingGroup)) {
        // StreamingGroup grp = (StreamingGroup) g;
        // // Array ar = define.get(grp);
        // // if (ar != null) {
        // // grp.bAddFirst(new VariableDeclarationExpression(ReferenceType.createArray(ar.getComponentType()),
        // // Arrays.asList(new VariableDeclarator(ar.getArray(), 0, nie))));
        // // composite = ArrayOrListComposite.create(ar.getType(), ar.getArray());
        // // }
        //
        // grp.first().addFirst(new TextLineStatement(""));
        // }
        // plan.getRoot().depthFirstTraversal(e -> e instanceof Group).forEach(e -> System.out.println(e));
    }

    static boolean allSizeable(QueryNode node) {
        return node.find(WalkOrder.PREVIOUS_OR_PARENT).pathBetween(n -> n instanceof ForAll)
                .allMatch(n -> n.is(CA_SIZE_NOT_INCREASING));
    }

    // Array vs Composite

    // Composite <- logical (
    // Array -> physical rendered datastructure

    public static class Array {

        final NameExpression array;

        final NameExpression arraySize;

        final Class<?> componentType;

        public boolean sizeConstant;

        Array(Class<?> componentType) {
            this(componentType, new NameExpression("array"), new NameExpression("aSz"));
        }

        Array(Class<?> componentType, NameExpression array, NameExpression arraySize) {
            this.componentType = componentType;
            this.array = array;
            this.arraySize = arraySize;
        }

        /**
         * @return the array
         */
        public NameExpression getArray() {
            return array;
        }

        /**
         * @return the arraySize
         */
        public NameExpression getArraySize() {
            return arraySize;
        }

        /**
         * @return the componentType
         */
        public Class<?> getComponentType() {
            return componentType;
        }

        public Class<?> getType() {
            return java.lang.reflect.Array.newInstance(getComponentType(), 1).getClass();
        }

        public void enlargeArrayIfNeeded(AbstractOperationNode n, BlockStatement b, Expression currentArraySize) {
            n.addImport(ArrayUtil.class);
            n.addImport(Arrays.class);
            BlockStatement bs = b.addIfBlock(currentArraySize.equalsTo(getArraySize()));
            Expression nextSize = getArraySize().assign(
                    staticMethodInvoke(ArrayUtil.class, "nextArraySize", getArraySize()));
            bs.add(getArray().assign(staticMethodInvoke(Arrays.class, "copyOf", getArray(), nextSize)));
        }
    }
}
