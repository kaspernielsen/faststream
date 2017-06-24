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

import static io.faststream.codegen.model.expression.Expressions.newVar;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cakeframework.internal.db.query.common.nodes.elements.ForAll;
import org.cakeframework.internal.db.query.common.nodes.elements.SingleElement;
import org.cakeframework.internal.db.query.compiler.datasource.ArrayOrListComposite;
import org.cakeframework.internal.db.query.compiler.datasource.Composite;
import org.cakeframework.internal.db.query.compiler.datasource.StreamingComposite;
import org.cakeframework.internal.db.query.compiler.render.PartialQuery;
import org.cakeframework.internal.db.query.compiler.render.ViewRender;
import org.cakeframework.internal.db.query.node.defaults.AllQueryOperations;
import org.cakeframework.internal.db.query.plan.QueryNode;
import org.cakeframework.internal.db.query.plan.QueryOperationParameterList.QueryOperationParameter;
import org.cakeframework.internal.db.query.plan.QueryPlan;
import org.cakeframework.internal.db.query.util.tree.WalkOrder;

import io.faststream.codegen.core.CodegenClass;
import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Expressions;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.expression.NewInstanceExpression;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.statement.WhileStatement;
import io.faststream.codegen.model.type.ClassOrInterfaceType;
import io.faststream.codegen.model.visitor.JavaRenderer;
import io.faststream.codegen.model.visitor.Visitors;

/**
 *
 * @author Kasper Nielsen
 */
public class ReturnIteratorOrSpliterator extends StreamingGroup implements AllQueryOperations {

    final List<QueryOperationParameter> l;

    final NameExpression next;
    final PartialQuery pq;

    final SingleElement toIterator;

    final ViewRender vr;
    BlockStatement bs = new BlockStatement();

    Class<?> arrayType;
    NameExpression arrayName = new NameExpression("a");
    final CodegenClass cc;

    final CountableSet<String> name = new CountableSet<>();

    final boolean isIterator;

    final String className;

    /**
     *
     */
    public ReturnIteratorOrSpliterator(ViewRender vr, List<QueryOperationParameter> l, QueryPlan plan,
            SingleElement toIterator) {
        this.vr = vr;
        this.l = l;
        this.pq = new PartialQuery(plan);
        this.toIterator = toIterator;
        cc = vr.newChild().cc();
        isIterator = toIterator.is(CT_TO_ITERATOR);
        className = isIterator ? "ResultIterator" : "ResultSpliterator";
        next = isIterator ? new NameExpression("next") : new NameExpression("consumer");
    }

    void build(ArrayOrListComposite existing) {
        ArrayOrListComposite newC = ArrayOrListComposite.create(existing.getType(), arrayName);
        sources().setMain(newC);
        setIn(bs);
        render();

    }

    /** {@inheritDoc} */
    @Override
    public NameExpression cache(Class<?> type, NameExpression name, Expression init) {
        int i = this.name.add(name.getName());
        if (i > 0) {
            name.setName(name.getName() + i);
        }
        this.cc.addField("final ", type, " ", name, " = ", JavaRenderer.toString(init), ";");
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void buildModel() {
        Composite v = sources().getMain();
        requireNonNull(v);
        if (v instanceof ArrayOrListComposite) {
            ArrayOrListComposite ac = (ArrayOrListComposite) v;

            NameExpression o = new NameExpression("o");
            pq.main = bs;
            if (isIterator) {
                NameExpression cursor = new NameExpression("cursor");
                NameExpression end = new NameExpression("end");

                BlockStatement body = new BlockStatement();
                bs.add(new WhileStatement(cursor.lessThen(end), body));
                body.add(newVar(o, ac.getComponentType(), arrayName.arrayAccess(cursor.preIncrement())));

                bs.add(cursor.assign(end).plus(Expressions.literal(1)));
                pq.getRoot().children().forEach(d -> d.setIn(body));
                StreamingComposite sc = new StreamingComposite(Object.class, o);

                pq.getRoot().children().forEach(d -> d.sources().setMain(sc));

            } else {
                NameExpression cursor = new NameExpression("cursor");
                NameExpression end = new NameExpression("end");

                BlockStatement body = new BlockStatement();

                bs.add(new WhileStatement(cursor.lessThen(end), body));
                body.add(newVar(o, ac.getComponentType(), arrayName.arrayAccess(cursor.postIncrement())));

                pq.getRoot().children().forEach(d -> d.setIn(body));
                StreamingComposite sc = new StreamingComposite(Object.class, o);

                pq.getRoot().children().forEach(d -> d.sources().setMain(sc));
            }

            pq.getRoot().renderChildren();

            vr.addRender(() -> new ReturnIteratorOrSpliteratorGenerator(cc, toIterator.getElement().getType(), this)
            .generate(ac));

            TemporaryBlockStatement.cleanup(pq); // must be before renaming, as we remove some blocks
            // Takes all name expressions (identifiers) and make sure they have different names
            Visitors.renameDuplicateNameExpressions(bs);

        } else {
            throw new RuntimeException("" + v.getClass());
        }

    }

    /**
     * @return the next
     */
    public NameExpression getNext() {
        return next;
    }

    public static void foo(ViewRender vr, PartialQuery plan) {
        QueryNode qn = plan.getRoot().lastChild();
        SingleElement se = qn.findFirst(WalkOrder.DOWN_AND_IN, SingleElement.class,
                e -> e.is(CT_TO_ITERATOR, CT_TO_SPLITERATOR));
        if (se == null || !se.is(CT_TO_ITERATOR, CT_TO_SPLITERATOR)) {
            return;
        }

        List<QueryOperationParameter> l = new ArrayList<>();
        for (QueryNode q : qn.depthFirstTraversal(f -> f.parameters().getSize() > 0)) {
            for (QueryOperationParameter p : q.parameters()) {
                l.add(p);
            }
        }

        ReturnIteratorOrSpliterator ri = new ReturnIteratorOrSpliterator(vr, l, plan.getPlan(), se);

        ri.pq.setRoot(ri);
        StreamingGroup g = se.group();
        ri.addChild(g);
        if (!g.isLeaf() && ri.firstChild() instanceof ForAll) {
            ri.firstChild().removePullupChildren();
        }

        ReturnNewIteratorOrSpliterator rni = new ReturnNewIteratorOrSpliterator(ri);
        plan.getRoot().addChild(rni);
        rni.setSource(g.sources());
        // System.out.println("PLAN");
        // plan.print(n -> "" + n.getOperation());
        // System.out.println("PQ");
        // ri.pq.print(n -> "" + n.getOperation());
    }

    static class ReturnNewIteratorOrSpliterator extends AbstractOperationNode {

        final ReturnIteratorOrSpliterator i;

        ReturnNewIteratorOrSpliterator(ReturnIteratorOrSpliterator i) {
            this.i = i;
        }

        /** {@inheritDoc} */
        @Override
        protected void buildModel() {
            Composite v = sources().getMain();
            if (v instanceof ArrayOrListComposite) {
                ArrayOrListComposite c = (ArrayOrListComposite) v;

                ArrayList<Expression> arguments = new ArrayList<>();
                arguments.addAll(Arrays.asList(c.asExpression()));
                for (QueryOperationParameter p : i.l) {
                    arguments.add(p.accessor());
                }
                Expression newIter = new NewInstanceExpression(null, new ClassOrInterfaceType(i.className), arguments);
                getTree().main.add(newIter.returnIt());

                // Build Iterator/Spliterator implementing class
                i.build(c);
            } else {
                throw new RuntimeException();
            }
        }
    }
}
