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
package org.cakeframework.internal.db.query.common.nodes.render;

import static io.faststream.codegen.model.expression.Expressions.literal;
import static io.faststream.codegen.model.expression.Expressions.staticField;
import static io.faststream.codegen.model.expression.Expressions.staticMethodInvoke;
import static io.faststream.codegen.model.statement.Statements.ifs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.cakeframework.internal.db.query.common.nodes.elements.SingleElement;
import org.cakeframework.internal.db.query.compiler.datasource.ArrayOrListComposite;
import org.cakeframework.internal.db.query.compiler.datasource.StreamingComposite;
import org.cakeframework.internal.db.query.compiler.datasource.StreamingMapComposite;
import org.cakeframework.internal.db.query.compiler.render.util.AbstractOperationNode;
import org.cakeframework.internal.db.query.compiler.render.util.FunctionalInterfaces;
import org.cakeframework.internal.db.query.compiler.render.util.ReturnIteratorOrSpliterator;
import org.cakeframework.internal.db.query.runtime.ArrayUtil;
import org.cakeframework.internal.db.query.util.tree.WalkOrder;

import io.faststream.codegen.core.CodegenUtil;
import io.faststream.codegen.model.body.VariableDeclarator;
import io.faststream.codegen.model.expression.ConditionalExpression;
import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Expressions;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.expression.NewInstanceExpression;
import io.faststream.codegen.model.expression.VariableDeclarationExpression;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.statement.IfStatement;
import io.faststream.codegen.model.statement.ReturnStatement;
import io.faststream.codegen.model.type.ClassOrInterfaceType;
import io.faststream.codegen.model.type.ReferenceType;
import io.faststream.codegen.model.util.Identifier;

/**
 *
 * @author Kasper Nielsen
 */
public class CollectionIntermediate extends ModelGenerator {

    /** {@inheritDoc} */
    @Override
    void generate() {
        register(StreamingComposite.class, SingleElement.class, CollectionIntermediate::asStream, C_AS_STREAM);
        register(StreamingComposite.class, SingleElement.class, CollectionIntermediate::filter, C_FILTER);
        register(ArrayOrListComposite.class, SingleElement.class, CollectionIntermediate::distinct, C_DISTINCT);
        register(StreamingComposite.class, SingleElement.class, CollectionIntermediate::distinct, C_DISTINCT);
        register(StreamingComposite.class, SingleElement.class, CollectionIntermediate::groupBy, C_GROUP_BY);

        register(StreamingComposite.class, SingleElement.class, CollectionIntermediate::peek, C_PEEK);
        register(ArrayOrListComposite.class, SingleElement.class, CollectionIntermediate::limit, C_TRUNCATE_LIMIT);
        register(ArrayOrListComposite.class, SingleElement.class, CollectionIntermediate::skip, C_TRUNCATE_SKIP);
        register(ArrayOrListComposite.class, SingleElement.class, CollectionIntermediate::sort, C_SORTED);
        register(ArrayOrListComposite.class, SingleElement.class, CollectionIntermediate::take, C_TRUNCATE_TAKE);
        register(StreamingComposite.class, SingleElement.class, CollectionIntermediate::returnSpliterator,
                CT_TO_SPLITERATOR);
        register(StreamingComposite.class, SingleElement.class, CollectionIntermediate::returnIterator, CT_TO_ITERATOR);
    }

    static void asStream(StreamingComposite c, SingleElement n) {
        Class<?> fromType = n.getElement().getType();
        if (fromType == long.class) {
            if (n.is(C_AS_DOUBLE_STREAM)) {
                Identifier e = new Identifier(double.class, "mapped");
                n.b().add(Expressions.newVar(e, double.class, c.getAccessor().cast(double.class)));
                n.next().sources().setMain(new StreamingComposite(double.class, e));
            }
        } else {
            n.next().sources().setMain(c);
        }
    }

    static void boxing(StreamingComposite c, SingleElement n) {
        Class<?> toType = CodegenUtil.boxClass(n.getElement().getType());
        Identifier e = new Identifier(toType, "boxed");
        n.b().add(Expressions.newVar(e, Object.class, c.getAccessor().box(toType)));
        n.next().sources().setMain(new StreamingComposite(Object.class, e));
    }

    static void distinct(ArrayOrListComposite c, SingleElement n) {
        n.addImport(ArrayUtil.class);
        Expression e = c.getAccessor();
        if (c.isImmutable()) {
            e = new NameExpression("array");
            Expression init = staticMethodInvoke(ArrayUtil.class, "distinct", c.asExpressionIfNonDefault());
            VariableDeclarationExpression vde = new VariableDeclarationExpression(
                    ReferenceType.createArray(Object.class), Arrays.asList(new VariableDeclarator((NameExpression) e,
                            0, init)));
            // composite = ArrayOrListComposite.create(Array.newInstance(type, 1).getClass(), array);
            n.b().add(vde);
        } else {
            n.b().add(e.assign(staticMethodInvoke(ArrayUtil.class, "distinct", c.asExpressionIfNonDefault())));
        }
        ArrayOrListComposite next = ArrayOrListComposite.create(c.getType(), e);
        n.next().sources().setMain(next);
    }

    static void distinct(StreamingComposite c, SingleElement n) {
        // Add et HashSet Oeverst.
        // brug hashSet.add() som filter
        n.addImport(HashSet.class);
        NameExpression set = new Identifier(HashSet.class, "set");

        Expression init = new NewInstanceExpression(null, new ClassOrInterfaceType(HashSet.class), Arrays.asList());
        n.addImport(ArrayUtil.class);
        set = n.group().cache(HashSet.class, set, init);

        BlockStatement bs = n.b().addIfBlock(set.invoke("add", c.getAccessor()));
        n.children().forEach(cc -> cc.setIn(bs));

        // Set the main source to the list we just created
        n.firstChild().sources().setMain(c);
        n.renderChildren();
    }

    static void filter(StreamingComposite c, SingleElement n) {
        Expression e = c.getAccessor();
        final BlockStatement bs;
        if (n.is(C_FILTER_NULLS)) {
            bs = n.b().addIfBlock(e.equalsTo(literal(null)).encapsulate().not().simplify());
        } else if (n.is(C_FILTER_ON_TYPE)) {
            bs = n.b().addIfBlock(n.parameters().first().accessor().invoke("isInstance", e));
        } else {
            // Generates code both for CollectionView.filter() and *Stream.filter()
            bs = n.b().addIfBlock(n.parameters().first().invokeFunctional(e));
        }
        n.children().forEach(cc -> cc.setIn(bs));

        // Set the main source to the list we just created
        n.firstChild().sources().setMain(new StreamingComposite(c.getType(), e));
        n.renderChildren();
    }

    static void returnIterator(StreamingComposite c, SingleElement n) {
        ReturnIteratorOrSpliterator re = n.findFirst(WalkOrder.PREVIOUS_OR_PARENT, ReturnIteratorOrSpliterator.class);
        n.b().add(re.getNext().assign(c.getAccessor()));
        n.b().add(new ReturnStatement());
    }

    static void returnSpliterator(StreamingComposite c, SingleElement n) {
        ReturnIteratorOrSpliterator re = n.findFirst(WalkOrder.PREVIOUS_OR_PARENT, ReturnIteratorOrSpliterator.class);
        n.b().add(re.getNext().invoke("accept", c.getAccessor()));
        n.b().add(literal(true).returnIt());
    }

    static void limit(ArrayOrListComposite c, SingleElement n) {
        Identifier upperBound = new Identifier(int.class, "upperBound");

        Expression minInt = staticMethodInvoke(Math.class, "min", n.parameters().first().accessor(),
                staticField(Integer.class, "MAX_VALUE"));

        Expression min = staticMethodInvoke(Math.class, "min", minInt.cast(int.class), c.size());

        n.b().add(Expressions.newVar(upperBound, int.class, c.getLowerBound().plus(min).simplify()));

        n.next().sources().setMain(c.withUpperBounds(upperBound));
    }

    static void groupBy(StreamingComposite c, AbstractOperationNode n) {
        Class<?> toType = FunctionalInterfaces.returnTypeOf(n.parameters().first().getType());
        Identifier e = new Identifier(toType, "mapped");
        n.b().add(Expressions.newVar(e, toType, n.parameters().first().invokeFunctional(c.getAccessor())));

        n.next().sources().setMain(new StreamingMapComposite(toType, e, c.getType(), c.getAccessor()));
    }

    static void peek(StreamingComposite c, SingleElement n) {
        n.b().add(n.parameters().first().invokeFunctional(c.getAccessor()));
        n.next().sources().setMain(c);
    }

    static void skip(ArrayOrListComposite c, SingleElement n) {
        Identifier lowerBound = new Identifier(int.class, "lowerBound");

        Expression skip = n.parameters().first().accessor();
        Expression all = new ConditionalExpression(skip.greaterThenOrEquals(c.size()), c.getUpperBound(), c
                .getLowerBound().plus(skip).encapsulate().simplify().cast(int.class));
        n.b().add(Expressions.newVar(lowerBound, int.class, all));
        n.next().sources().setMain(c.withLowerBounds(lowerBound));
    }

    static void sort(ArrayOrListComposite c, SingleElement n) {
        c = c.cloneIfImmutable(n, n.b());

        // /** {@inheritDoc} */
        // @Override
        // public void create(ArrayOrListComposite composite) {
        if (Collection.class.isAssignableFrom(c.getType())) {
            n.addImport(Collections.class);
            if (n.is(C_SORTED_COMPARATOR)) {
                n.b().add(
                        staticMethodInvoke(Collections.class, "sort", c.getAccessor(), n.parameters().first()
                                .accessor()));
            } else {
                n.b().add(staticMethodInvoke(Collections.class, "sort", c.getAccessor()));
            }
        } else { // array

            n.addImport(Arrays.class);
            ArrayList<Expression> arguments = new ArrayList<>(Arrays.asList(c.asExpressionIfNonDefault()));
            if (n.is(C_SORTED_COMPARATOR)) {
                arguments.add(n.parameters().first().accessor());
            }
            n.b().add(staticMethodInvoke(Arrays.class, "sort", arguments.toArray(new Expression[arguments.size()])));

            // Check if we need to reverse it
            if (n.is(C_SORTED_NATURAL_REVERSE, C_SORTED_COMPARATOR_REVERSE)) {
                n.addImport(ArrayUtil.class);
                n.b().add(staticMethodInvoke(ArrayUtil.class, "reverse", c.asExpressionIfNonDefault()));
            }
        }
        n.next().sources().setMain(c);
    }

    static void take(ArrayOrListComposite c, SingleElement n) {
        Identifier start = new Identifier(int.class, "startIndex");
        Identifier stop = new Identifier(int.class, "stopIndex");

        n.b().add(Expressions.newVar(start, int.class, c.getLowerBound()));
        n.b().add(Expressions.newVar(stop, int.class, c.getUpperBound()));
        // System.err.println(composite.getUpperBound().toString());
        // Not that pretty
        BlockStatement bs = new BlockStatement();
        n.b().add(ifs(start.notEquals(stop.minus(literal(1))), bs));

        BlockStatement bs1 = new BlockStatement();
        bs1.add(ifs(n.parameters().first().accessor().lessThen(stop.minus(start)),
                stop.assign(start.plus(n.parameters().first().accessor().cast(int.class)))));

        IfStatement ifs = ifs(n.parameters().first().accessor().greaterThen(start.minus(stop)),
                start.assign(stop.plus(n.parameters().first().accessor().cast(int.class))));

        bs.add(ifs(n.parameters().first().accessor().greaterThen(literal(0)), bs1, ifs));

        n.next().sources().setMain(c.withBounds(start, stop));
    }
}
//
// public void to() {
// //
// // /** {@inheritDoc} */
// // @Override
// // public void setup(CodegenNode n, IndexBoundedComposite var, QueryNode next) {
// // Class<?> other = org.cakeframework.internal.view.node.collectionview.CR_To.class;
// // n.addImport(other);
// // if (Collection.class.isAssignableFrom(var.getJavaType())) {
// // n.addChild(newReturn(typeOf(other).invokeMethod(Object.class, "from", sources().getParameter(),
// // var.asList())));
// // // b.add("return ", other, ".from(", sources().getParameter(), ", ", var.asList(), ");");
// // } else {
// // n.addChild(newReturn(typeOf(other).invokeMethod(Object.class, "from", sources().getParameter(), var,
// // var.getLowerBound(), var.getUpperBound(), newConstant(false))));
// //
// // // b.add("return ", other, ".from(", sources().getParameter(), ", ", var.withBounds(), ", false);");
// // }
// // }
// }
//
// public void one() {
//
// // /** {@inheritDoc} */
// // public void setup(CodegenNode c, IndexBoundedComposite ibv, QueryNode next) {
// // Class<?> other = org.cakeframework.internal.view.node.collectionview.CR_One.class;
// // c.addImport(AbstractCollectionView.class, other);
// //
// // IfThanElseStatement ie = ifThenElse(ibv.isEmpty(), newReturn(typeOf(AbstractCollectionView.class)
// // .withFieldAccess(Object.class, "EMPTY_RESULT")));
// // ie.elseIfThan(biEquals(ibv.size(), 1)).a(newReturn(ibv.getFirstElement()));
// // ie.elseThan().a(
// // throwNew(IllegalStateException.class,
// // typeOf(other).withFieldAccess("MORE_THAN_ONE_ELEMENT_VIEW_ERROR_MESSAGE")));
// //
// // c.addChild(ie);
// // }
// // /** {@inheritDoc} */
// // public void render(CodegenBlock b, IndexBoundedComposite var) {
// // Class<?> other = org.cakeframework.internal.view.node.collectionview.CR_One.class;
// // b.addImport(AbstractCollectionView.class, other);
// //
// // // TODO take if else if else
// // b.add("if (", var.isEmpty(), ") {");
// // b.add("return ", AbstractCollectionView.class, ".EMPTY_RESULT;");
// // b.add("} else if (", biEquals(var.size(), 1), ") {");
// // b.add("return ", var.getFirstElement(), ";");
// // b.add("} else {");
// // b.add("throw new ", IllegalStateException.class, "(", other, ".MORE_THAN_ONE_ELEMENT_VIEW_ERROR_MESSAGE);");
// // b.add("}");
// // }
// }
//
// public void unique() {
// // /** {@inheritDoc} */
// // @Override
// // public void setupOld(CodegenNode c, IndexBoundedComposite var, QueryNode next) {
// // Identifier uniqueSet = newIdentifier(HashSet.class);
// // c.insertBeforeThis(uniqueSet.defineNew());
// // sources().set("unique", uniqueSet);
// //
// // // We work with sets normally, so copy to new set
// // IndexBoundedComposite unique = Composites.newIndexBoundedList();
// // c.insertAfterThis(unique.getIdentifier().defineNew(uniqueSet));
// // next.sources().setMain(unique);
// // }
// //
// // /** {@inheritDoc} */
// // @Override
// // public void renderIntIndexable(CodegenBlock b, IndexBoundedComposite var) {
// // Expression unique = sources().get("unique");
// // b.addImport(HashSet.class);
// // if (Collection.class.isAssignableFrom(var.getJavaType())) {
// // b.add(unique, ".addAll(", var.asList(), ");");
// // } else {
// // b.addImport(Arrays.class);
// // b.add(unique, ".addAll(Arrays.asList(", var, ").subList(", var.getLowerBound(), ", ", var.getUpperBound(),
// // "));");
// // }
// // }
// }
//
// public void gather() {
// // /** {@inheritDoc} */
// // @Override
// // public void setup(CodegenNode c, IndexBoundedComposite ibv, QueryNode next) {
// // Identifier v = newIdentifier(Procedure.class);
// // c.addImport(Collections.class);
// // c.addChild(v.define(cast(Procedure.class, sources().getParameter().invokeMethod(Object.class, "next"))));
// // IndexBoundedComposite insertIntoType = Composites.newIndexBoundedList();
// //
// // c.addChild(insertIntoType.defineNew(typeOf(Collections.class).invokeMethod(List.class, "singletonList", v)));
// //
// // CodegenNode forNode = forEach(c);
// //
// // forNode.addChild(v.invokeMethod(Object.class, "apply", getVariable()));
// //
// // sources().set("procedure", v);
// // sources().setMain(insertIntoType);
// // next.sources().setMain(insertIntoType);
// // }
// }
//
// public void count() {
// //
// // /** {@inheritDoc} */
// // @Override
// // public void setupOld(CodegenNode c, IndexBoundedComposite ibv, QueryNode next) {
// // MapComposite insertIntoType = Composites.newMapComposite();
// //
// // CodegenNode forNode = forEachOld(c);
// // forNode.insertBeforeThis(insertIntoType.defineNew());
// // sources().setMain(insertIntoType);
// // next.sources().setMain(insertIntoType);
// // }
// //
// // /** {@inheritDoc} */
// // @Override
// // public void render(CodegenBlock b) {
// // b.add("Long l = (Long) ", sources().getMain(), ".get(", getVariable(), ");");
// // b.add(sources().getMain(), ".put(", getVariable(), ", l == null ? 1L : l + 1L);");
// // }
// }
// // REVERSE
//
// // /** {@inheritDoc} */
// // public void renderIdntIndexable(CodegenBlock b, IndexBoundedComposite var) {
// // if (Collection.class.isAssignableFrom(var.getJavaType())) {
// // b.addImport(Collections.class);
// // b.add(Collections.class, ".reverse(", var.asList(), ");");
// // } else {
// // b.addImport(ArrayUtil.class);
// // b.add("ArrayUtil.reverse(", var, ", ", var.getLowerBound(), ", ", var.getUpperBound(), ");");
// // }
// // }
// //
// // /** {@inheritDoc} */
// // @Override
// // protected void doIt(CodegenNode c, IndexBoundedComposite ibv) {
// // if (Collection.class.isAssignableFrom(ibv.getJavaType())) {
// // c.addImport(Collections.class);
// // c.addChild(typeOf(Collections.class).invokeMethod(void.class, "reverse", ibv.asList()));
// // } else {
// // c.addImport(ArrayUtil.class);
// // c.addChild(typeOf(ArrayUtil.class).invokeMethod(void.class, "reverse", ibv.asList(), ibv.getLowerBound(),
// // ibv.getUpperBound()));
// // }
// // }
//
// // SHUFFLE
// // @Override
// // protected void doIt(CodegenNode c, IndexBoundedComposite ibv) {
// // if (Collection.class.isAssignableFrom(ibv.getJavaType())) {
// // c.addImport(Collections.class);
// // c.addChild(typeOf(Collections.class).invokeMethod(void.class, "shuffle", ibv.asList()));
// // } else {
// // c.addImport(ArrayUtil.class);
// // c.addImport(ThreadLocalRandom.class);
// // c.addChild(typeOf(ArrayUtil.class).invokeMethod(void.class, "shuffle", ibv.asList(), ibv.getLowerBound(),
// // ibv.getUpperBound(), typeOf(ThreadLocalRandom.class).invokeMethod(Random.class, "current")));
// // }
// // }
//
// // SORT */
// // @Override
// // public void setup(CodegenNode c, IndexBoundedComposite ibv, QueryNode next) {
// // if (ibv.isImmutable()) {
// // c.getTree().addImport(Arrays.class);
// // Identifier icopy = newIdentifier(Object[].class, "copy");
// // IndexBoundedComposite copy = IndexBoundedComposite.create(icopy);
// // LocalVariableDeclarationStatement ifff = newLocalVariableDeclaration(icopy, typeOf(Arrays.class)
// // .invokeMethod(Object[].class, "copyOfRange", ibv, ibv.getLowerBound(), ibv.getUpperBound()));
// // c.addChild(ifff);
// // sources().setMain(copy);
// // next.sources().setMain(copy);
// // } else {
// // next.sources().setMain(sources().getMain());
// // }
// // doIt(c, (IndexBoundedComposite) sources().getMain());
// // }

// protected abstract void doItf(CodegenNode c, IndexBoundedComposite ibv);
// /** {@inheritDoc} */
// @Override
// protected void doIt(CodegenNode c, IndexBoundedComposite ibv) {
// if (Collection.class.isAssignableFrom(ibv.getJavaType())) {
// c.addImport(Collections.class);
// if (is(C_ORDER_COMPARATOR)) {
// c.addChild(typeOf(Collections.class).invokeMethod(void.class, "sort", ibv.asList(),
// sources().getParameter()));
// } else {
// c.addChild(typeOf(Collections.class).invokeMethod(void.class, "sort", ibv.asList()));
// if (is(C_ORDER_DESCENDING)) {// We never sort collection, always arrays
// c.addChild(typeOf(Collections.class).invokeMethod(void.class, "reverse", ibv.asList()));
// }
// }
// } else {
// if (is(C_ORDER_ASCENDING)) {// We never sort collection, always arrays
// c.addImport(Arrays.class);
// c.addChild(typeOf(Arrays.class).invokeMethod(void.class, "sort", ibv.asList(), ibv.getLowerBound(),
// ibv.getUpperBound()));
//
// } else if (is(C_ORDER_COMPARATOR)) {
// c.addImport(Arrays.class);
// c.addChild(typeOf(Arrays.class).invokeMethod(void.class, "sort", ibv.asList(), ibv.getLowerBound(),
// ibv.getUpperBound(), sources().getParameter()));
// } else {
// c.addImport(ArrayUtil.class);
// c.addChild(typeOf(ArrayUtil.class).invokeMethod(void.class, "sortDescending", ibv.asList(),
// ibv.getLowerBound(), ibv.getUpperBound()));
// }
// }
// }
