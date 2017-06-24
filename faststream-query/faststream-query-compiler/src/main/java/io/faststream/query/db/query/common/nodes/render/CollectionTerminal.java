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
package io.faststream.query.db.query.common.nodes.render;

import static io.faststream.codegen.model.expression.Expressions.literal;
import static io.faststream.codegen.model.expression.Expressions.staticField;
import static io.faststream.codegen.model.expression.Expressions.staticMethodInvoke;
import static io.faststream.codegen.model.statement.Statements.blockOf;

import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.LongSummaryStatistics;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collector;

import io.faststream.codegen.core.CodegenUtil;
import io.faststream.codegen.model.expression.ConditionalExpression;
import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Expressions;
import io.faststream.codegen.model.expression.Literal;
import io.faststream.codegen.model.expression.MethodInvocation;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.expression.NewInstanceExpression;
import io.faststream.codegen.model.expression.VariableDeclarationExpression;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.statement.Statements;
import io.faststream.codegen.model.type.Type;
import io.faststream.codegen.model.util.Identifier;
import io.faststream.query.db.query.common.nodes.elements.CopyToArray;
import io.faststream.query.db.query.common.nodes.elements.SingleElement;
import io.faststream.query.db.query.compiler.datasource.ArrayOrListComposite;
import io.faststream.query.db.query.compiler.datasource.StreamingComposite;
import io.faststream.query.db.query.compiler.render.util.AbstractOperationNode;
import io.faststream.query.db.query.compiler.render.util.ReducerSimplifier;
import io.faststream.query.db.query.compiler.render.util.ReducingNodes;
import io.faststream.query.db.query.node.EmptyResult;
import io.faststream.query.db.query.plan.QueryNode;
import io.faststream.query.db.query.runtime.ArrayUtil;
import io.faststream.query.db.query.util.tree.WalkOrder;

/**
 *
 * @author Kasper Nielsen
 */
public class CollectionTerminal extends ModelGenerator {

    /** {@inheritDoc} */
    @Override
    void generate() {
        register(ArrayOrListComposite.class, SingleElement.class, (c, n) -> anyFirstLast(c, n), CT_ANYFIRSTLAST);
        register(StreamingComposite.class, SingleElement.class, (c, n) -> anyFirstLast(c, n), CT_ANYFIRSTLAST);

        register(StreamingComposite.class, AbstractOperationNode.class, (c, n) -> anyAllNoneMatch(c, n), CT_MATCH_ALL,
                CT_MATCH_NONE, CT_MATCH_ANY);
        register(StreamingComposite.class, SingleElement.class, (c, n) -> collectFused(c, n), CT_COLLECT_FUSED);
        register(StreamingComposite.class, SingleElement.class, (c, n) -> collectCollect(c, n), CT_COLLECT_COLLECTOR);
        register(StreamingComposite.class, AbstractOperationNode.class, (c, n) -> forEach(c, n), CT_FOR_EACH);
        register(StreamingComposite.class, SingleElement.class, (c, n) -> mathSum(c, n), CT_MATH_SUM);
        register(StreamingComposite.class, SingleElement.class, (c, n) -> mathAverage(c, n), CT_MATH_ARITHMETIC_MEAN);
        register(StreamingComposite.class, SingleElement.class, (c, n) -> mathSummary(c, n), CT_MATH_SUMMARY_STATISTICS);
        // reduce
        register(StreamingComposite.class, SingleElement.class, (c, n) -> reduceLastStep(c, n),
                ReducingNodes.CT_REDUCE_LAST_STEP);
        register(StreamingComposite.class, SingleElement.class, (c, n) -> reduce(c, n), CA_REDUCING);
        // to
        register(ArrayOrListComposite.class, SingleElement.class, (c, n) -> to(c, n), CT_TO);
    }

    static void anyAllNoneMatch(StreamingComposite c, AbstractOperationNode n) {
        // First we filter the element using the specified predicate (using ! for matchAll)
        MethodInvocation e = n.parameters().first().invokeFunctional(c.getAccessor());
        BlockStatement b = n.b().addIfBlock(n.is(CT_MATCH_ALL) ? e.not() : e);

        // return true for AnyMatch otherwise false
        b.add(staticField(Boolean.class, n.is(CT_MATCH_ANY) ? "TRUE" : "FALSE").returnIt());

        // return false for AnyMatch otherwise true
        n.group().bReturnLast(staticField(Boolean.class, n.is(CT_MATCH_ANY) ? "FALSE" : "TRUE"));
    }

    static void anyFirstLast(ArrayOrListComposite c, SingleElement n) {
        BlockStatement a = n.b().addIfBlock(c.isEmpty().encapsulate().not().simplify());
        a.addReturn(n.is(CT_LAST) ? c.getLastElement() : c.getFirstElement());

        n.addImport(EmptyResult.class);
        n.b().addReturn(staticField(EmptyResult.class, "EMPTY_RESULT"));
    }

    static void anyFirstLast(StreamingComposite c, SingleElement n) {
        n.b().add(c.getAccessor().lazyBox(n.getElement().getType()).returnIt());
        // a.addReturn(n.is(CT_LAST) ? c.getLastElement() : c.getFirstElement());

        n.addImport(EmptyResult.class);
        n.group().bReturnLast(staticField(EmptyResult.class, "EMPTY_RESULT"));
    }

    // @Override
    // @SuppressWarnings("unchecked")
    // public final <R, A> R collect(Collector<? super P_OUT, A, R> collector) {
    // A container;
    // if (isParallel()
    // && (collector.characteristics().contains(Collector.Characteristics.CONCURRENT))
    // && (!isOrdered() || collector.characteristics().contains(Collector.Characteristics.UNORDERED))) {
    // container = collector.supplier().get();
    // BiConsumer<A, ? super P_OUT> accumulator = collector.accumulator();
    // forEach(u -> accumulator.accept(container, u));
    // }
    // else {
    // container = evaluate(ReduceOps.makeRef(collector));
    // }
    // return collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)
    // ? (R) container
    // : collector.finisher().apply(container);
    // }
    static void collectCollect(StreamingComposite c, SingleElement n) {
        // Collector<T, A, R>
        NameExpression resultContainer = new Identifier(Object.class, "resultContainer");
        n.group().bAddFirst(
                Statements.newVar(resultContainer, Object.class, n.parameters().first().invoke("supplier")
                        .invoke("get")));
        NameExpression accumulator = new Identifier(Supplier.class, "accumulator");
        n.addImport(BiConsumer.class);
        n.group().bAddFirst(
                Statements.newVar(accumulator, BiConsumer.class, n.parameters().first().invoke("accumulator")));

        n.b().add(accumulator.invoke("accept", resultContainer, c.getAccessor()));

        // Collector<T, A, R>
        // n.parameters().first().accessor()
        // NameExpression container = new Identifier(Object.class, "container");
        // // n.group().bAddFirst(Statements.newVar(result, Object.class, n.parameters().first().invokeFunctional()));
        //
        // // BiConsumer<I, ? super T> accumulator = collector.accumulator();
        // // BinaryOperator<I> combiner = collector.combiner();
        // Stream<T>

        Expression ee = n.parameters().first().invoke("characteristics")
                .invoke("contains", staticField(Collector.class, "Characteristics").fieldAccess("IDENTITY_FINISH"));
        // collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)

        ConditionalExpression e = new ConditionalExpression(ee, resultContainer, n.parameters().first().accessor()
                .invoke("finisher").invoke("apply", resultContainer));
        //
        n.group().bReturnLast(e);
    }

    static void collectFused(StreamingComposite c, SingleElement n) {
        NameExpression result = new Identifier(Object.class, "result");
        n.group().bAddFirst(Statements.newVar(result, Object.class, n.parameters().first().invokeFunctional()));

        n.b().add(n.parameters().second().invokeFunctional(result, c.getAccessor()));
        n.group().bReturnLast(result);
    }

    static void forEach(StreamingComposite c, AbstractOperationNode n) {
        n.b().add(n.parameters().first().invokeFunctional(c.getAccessor()));

        // add a return as the last statement in the top block
        n.group().bReturnLast(Literal.nullLiteral());
    }

    static void mathAverage(StreamingComposite c, SingleElement n) {
        Class<?> elementType = n.getElement().getType(); // int, double or long
        Class<?> et = elementType == int.class ? long.class : elementType;

        NameExpression count = new NameExpression("count"); // We initialize a counter variable called sum
        NameExpression sum = new NameExpression("sum"); // We initialize a counter variable called sum

        // define a size variable before the loop
        n.group().bAddFirst(Statements.newVar(sum, et, Literal.identity(et)));
        n.group().bAddFirst(Statements.newVar(count, int.class, literal(0)));

        n.b().add(sum.plusAssign(c.getAccessor())); // sum += element
        n.b().add(count.postIncrement()); // sum += element

        // Adds a return as the last statement in the main block
        n.addImport(EmptyResult.class);
        BlockStatement bs = blockOf(staticField(EmptyResult.class, "EMPTY_RESULT").returnIt());
        n.group().b().addIf(count.equalsTo(literal(0)), bs);

        // LongSummaryStatistics
        // (double) getSum() / getCount()
        Expression s = elementType == double.class ? sum : sum.cast(double.class);
        n.group().bReturnLast(s.divide(count));
    }

    static void mathSum(StreamingComposite c, SingleElement n) {
        Class<?> elementType = n.getElement().getType(); // int, double or long
        NameExpression sum = new NameExpression("sum"); // We initialize a counter variable called sum

        // define a size variable before the loop
        n.group().bAddFirst(Statements.newVar(sum, elementType, Literal.identity(elementType)));

        n.b().add(sum.plusAssign(c.getAccessor())); // sum += element

        // Adds a return as the last statement in the main block
        n.group().bReturnLast(sum.box(CodegenUtil.boxClass(elementType)));
    }

    static void mathSummary(StreamingComposite c, SingleElement n) {
        Class<?> summaryType;
        if (n.getElement().getType() == double.class) {
            summaryType = DoubleSummaryStatistics.class;
        } else if (n.getElement().getType() == long.class) {
            summaryType = LongSummaryStatistics.class;
        } else {
            summaryType = IntSummaryStatistics.class;
        }
        n.addImport(summaryType);

        NameExpression summary = new NameExpression("summary"); // We initialize a counter variable called sum

        // define a size variable before the loop
        n.group().bAddFirst(
                Statements.newVar(summary, summaryType, new NewInstanceExpression(null, Type.of(summaryType),
                        Collections.emptyList())));
        // LongSummaryStatistics
        n.b().add(summary.invoke("accept", c.getAccessor()));

        // Adds a return as the last statement in the main block
        n.group().bReturnLast(summary);
    }

    static void reduce(StreamingComposite c, SingleElement n) {
        NameExpression hasResult = new NameExpression("hasResult");
        Identifier result = new Identifier(n.getElement().getType(), "result");
        VariableDeclarationExpression vde = Expressions.newVar(hasResult, boolean.class, literal(false));

        n.group().firstBlock().add(vde);

        VariableDeclarationExpression vdr = Expressions.newVar(result, n.getElement().getType().isPrimitive() ? n
                .getElement().getType() : Object.class, Literal.identity(n.getElement().getType()));
        n.group().firstBlock().add(vdr);

        BlockStatement first = new BlockStatement();
        first.add(result.assign(c.getAccessor()));
        first.add(hasResult.assign(literal(true)));

        BlockStatement last = new BlockStatement();
        if (n.is(CT_MAX_NATURAL_ORDERING, CT_MIN_NATURAL_ORDERING)) {
            String method = n.is(CT_MAX_NATURAL_ORDERING) ? "max" : "min";
            last.add(result.assign(staticMethodInvoke(Math.class, method, result, c.getAccessor())));
        } else {
            if (n.is(CT_REDUCE_OPERATOR)) {
                last.add(result.assign(n.parameters().first().invokeFunctional(result, c.getAccessor())));
            } else if (n.is(CT_REDUCE_FUSED)) {
                last.add(result.assign(n.parameters().first().invokeFunctional(result, c.getAccessor())));
            } else {
                Expression base = n.parameters().first().invokeFunctional(result, c.getAccessor());
                base = n.is(CT_MAX_COMPARATOR) ? base.lessThen(literal(0)) : base.greaterThen(literal(0));
                BlockStatement bs = last.addIfBlock(base);
                bs.add(result.assign(c.getAccessor()));
            }
        }
        n.b().addIf(hasResult, last, first);
        //
        // BlockStatement b = n.b();
        // QueryNode qn = n.find(WalkOrder.PARENT).last(f -> f.is(C_FLAT_MAP));
        // if (qn != null) {
        // // b = qn.b();
        // }
        // Identifier e = new Identifier(n.getElement().getType(), "reduced");
        // Expression el = c.getAccessor();
        // VariableDeclarationExpression vde = Expressions.newVar(e, c.getType(), el);
        // ReducerSimplifier.addTo(n.getTree().getPlan(), b, vde);
        // b.add(vde);
        //
        // n.putObject("e", e);

        // n.renderChildren();
        // Flatmap

        // b.add(e.lazyBox(n.getElement().getType()).returnIt());
        n.addImport(EmptyResult.class);
        // n.group().b().addIf(hasResult, Statements.blockOf(staticField(EmptyResult.class,
        // "EMPTY_RESULT").returnIt()));
        // n.group().bReturnLast( staticField(EmptyResult.class, "EMPTY_RESULT"));

        n.group().b().addIf(hasResult, Statements.blockOf(result.lazyBox(n.getElement().getType()).returnIt()));
        n.group().bReturnLast(staticField(EmptyResult.class, "EMPTY_RESULT"));
        // n.group().bReturnLast(literal(null));
    }

    static void oldReduce(StreamingComposite c, SingleElement n) {
        BlockStatement b = n.b();
        QueryNode qn = n.find(WalkOrder.PARENT).last(f -> f.is(C_FLAT_MAP));
        if (qn != null) {
            // b = qn.b();
        }
        Identifier e = new Identifier(n.getElement().getType(), "reduced");
        Expression el = c.getAccessor();
        VariableDeclarationExpression vde = Expressions.newVar(e, c.getType(), el);
        ReducerSimplifier.addTo(n.getTree().getPlan(), b, vde);
        b.add(vde);

        n.putObject("e", e);

        n.renderChildren();
        // Flatmap

        b.add(e.lazyBox(n.getElement().getType()).returnIt());
        n.addImport(EmptyResult.class);
        n.group().bReturnLast(staticField(EmptyResult.class, "EMPTY_RESULT"));
    }

    static void reduceLastStep(StreamingComposite c, SingleElement n) {
        BlockStatement b = n.b();
        AbstractOperationNode fa = (AbstractOperationNode) n.findFirst(WalkOrder.PARENT, e -> e.is(CA_REDUCING));
        Expression e = (Expression) fa.getObject("e");
        if (fa.is(CT_MAX_NATURAL_ORDERING, CT_MIN_NATURAL_ORDERING)) {
            String method = fa.is(CT_MAX_NATURAL_ORDERING) ? "max" : "min";
            n.b().add(e.assign(staticMethodInvoke(Math.class, method, e, c.getAccessor())));
        } else {
            if (fa.is(CT_REDUCE_OPERATOR)) {
                n.b().add(e.assign(n.parameters().first().invokeFunctional(e, c.getAccessor())));
            } else if (fa.is(CT_REDUCE_FUSED)) {
                n.b().add(e.assign(n.parameters().first().invokeFunctional(e, c.getAccessor())));
            } else {
                Expression base = n.parameters().first().invokeFunctional(e, c.getAccessor());
                base = fa.is(CT_MAX_COMPARATOR) ? base.lessThen(literal(0)) : base.greaterThen(literal(0));
                BlockStatement bs = b.addIfBlock(base);
                bs.add(e.assign(c.getAccessor()));
            }
        }
    }

    static void to(ArrayOrListComposite c, SingleElement n) {
        boolean needsCloning = !c.isImmutable() || !c.isDefaultBounds();
        if (c.getType().isArray()) {
            if (n.is(CT_TO_LIST)) {
                n.addImport(ArrayUtil.class);
                if (c.isImmutable()) {
                    n.b().add(staticMethodInvoke(ArrayUtil.class, "toList", c.cloneForReturn(n)).returnIt());
                } else {
                    n.b().add(staticMethodInvoke(ArrayUtil.class, "toList", c.asExpressionIfNonDefault()).returnIt());
                }
            } else if (!needsCloning) {
                n.b().add(c.getAccessor().returnIt());
            } else {
                boolean ok = false;
                if (n.previous() != null) {
                    QueryNode last = n.previous().find(WalkOrder.IN_AND_DOWN).last();
                    if (last instanceof CopyToArray) {
                        CopyToArray cta = (CopyToArray) last;
                        // / System.out.println("XXX " + cta.ar.getArraySize());
                        Expression test;
                        if (cta.ar.sizeConstant) {
                            if (cta.ar.getArray().fieldAccess("length").equals(c.getUpperBound())) {
                                n.b().add(c.getAccessor().returnIt());
                                return;
                            }
                            test = cta.ar.getArray().fieldAccess("length").equalsTo(c.getUpperBound());
                        } else {
                            test = cta.ar.getArraySize().equalsTo(c.size());
                        }

                        // We only want to copy up to the size of the array
                        n.b().add(new ConditionalExpression(test, c.getAccessor(), c.cloneForReturn(n)).returnIt());
                        ok = true;
                    }
                }
                if (!ok) {
                    n.b().add(c.cloneForReturn(n).returnIt());
                }
            }
        }

    }
}
