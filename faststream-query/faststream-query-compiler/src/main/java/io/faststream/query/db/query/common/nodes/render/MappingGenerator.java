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
import static io.faststream.codegen.model.expression.Expressions.staticMethodInvoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.faststream.codegen.core.CodegenUtil;
import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Expressions;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.statement.ForStatement;
import io.faststream.codegen.model.statement.TryStatement;
import io.faststream.codegen.model.type.ClassOrInterfaceType;
import io.faststream.codegen.model.type.Type;
import io.faststream.codegen.model.util.ForAllI;
import io.faststream.codegen.model.util.Identifier;
import io.faststream.query.db.query.common.nodes.elements.ForAll;
import io.faststream.query.db.query.common.nodes.elements.SingleElement;
import io.faststream.query.db.query.compiler.datasource.StreamingComposite;
import io.faststream.query.db.query.compiler.datasource.StreamingMapComposite;
import io.faststream.query.db.query.compiler.render.util.AbstractOperationNode;
import io.faststream.query.db.query.compiler.render.util.FunctionalInterfaces;
import io.faststream.query.db.query.plan.QueryNode;
import io.faststream.query.db.query.runtime.StreamUtil;
import io.faststream.query.db.query.util.tree.WalkOrder;

/**
 *
 * @author Kasper Nielsen
 */
public class MappingGenerator extends ModelGenerator {

    /** {@inheritDoc} */
    @Override
    void generate() {
        register(StreamingComposite.class, MappingGenerator::map, C_MAP_TO);
        registerSingle(StreamingComposite.class, MappingGenerator::boxing, C_MAP_BOXING);
        registerSingle(StreamingComposite.class, MappingGenerator::mapToIndex, C_MAP_TO_INDEX);
        registerSingle(StreamingComposite.class, MappingGenerator::flatMap, C_FLAT_MAP);
    }

    static void boxing(StreamingComposite c, SingleElement n) {
        Class<?> toType = CodegenUtil.boxClass(n.getElement().getType());
        Identifier e = new Identifier(toType, "boxed");
        n.b().add(Expressions.newVar(e, Object.class, c.getAccessor().box(toType)));
        n.next().sources().setMain(new StreamingComposite(Object.class, e));
    }

    static void map(StreamingComposite c, AbstractOperationNode n) {
        Class<?> toType = FunctionalInterfaces.returnTypeOf(n.parameters().first().getType());
        Identifier e = new Identifier(toType, "mapped");
        n.b().add(Expressions.newVar(e, toType, n.parameters().first().invokeFunctional(c.getAccessor())));

        n.next().sources().setMain(new StreamingComposite(toType, e));
    }

    static void flatMap(StreamingComposite c, AbstractOperationNode n) {
        boolean isPrimitive = !n.is(C_FLAT_MAP_TO_OBJECT);
        if (isPrimitive) {
            n.addImport(PrimitiveIterator.class);
        } else {
            n.addImport(Iterator.class);
        }

        Class<?> streamType = Stream.class;
        Class<?> targetType = Object.class;
        Type iteratorType = Type.of(Iterator.class);
        if (n.is(C_FLAT_MAP_TO_DOUBLE)) {
            streamType = DoubleStream.class;
            targetType = double.class;
            iteratorType = new ClassOrInterfaceType(PrimitiveIterator.OfDouble.class)
                    .setScope(new ClassOrInterfaceType(PrimitiveIterator.class));
        } else if (n.is(C_FLAT_MAP_TO_INT)) {
            streamType = IntStream.class;
            targetType = int.class;
            iteratorType = new ClassOrInterfaceType(PrimitiveIterator.OfInt.class).setScope(new ClassOrInterfaceType(
                    PrimitiveIterator.class));
        } else if (n.is(C_FLAT_MAP_TO_LONG)) {
            streamType = LongStream.class;
            targetType = long.class;
            iteratorType = new ClassOrInterfaceType(PrimitiveIterator.OfLong.class).setScope(new ClassOrInterfaceType(
                    PrimitiveIterator.class));
        }

        n.addImport(streamType);
        Identifier s = new Identifier(streamType, "s");
        n.b().add(
                Expressions.newVar(s, streamType,
                        n.parameters().first().invokeFunctional(c.getAccessor()).cast(streamType)));
        BlockStatement b = new BlockStatement();

        n.b().addIf(s.notEquals(literal(null)), b);

        Identifier iter = new Identifier(Iterator.class, "iter");

        BlockStatement bb = new BlockStatement();
        boolean ignoreContents = false;
        if (n.children().size() > 0) {
            ArrayList<QueryNode> list = new ArrayList<>(n.children());
            list.removeIf(e -> e.is(CA_SIZE_CONSTANT));
            ignoreContents = list.size() == 1 && list.get(0).is(CT_SIZEABLE);
            // boolean ignoreContents = && n.firstChild().is(CT_SIZEABLE);
            // if (n.children().size() > 0) {
            // System.out.println(ignoreContents);
            // // System.out.println("AAAAA " + n.firstChild().getOperation());
            // }

        }

        List<Expression> update = ignoreContents ? Arrays.asList(iter.invoke("next")) : Collections.emptyList();
        ForStatement fs = new ForStatement(Arrays.asList(Expressions.newVar(iter, iteratorType, s.invoke("iterator"))),
                iter.invoke("hasNext"), update, bb);
        if (isPrimitive) {
            n.addImport(StreamUtil.class);
            fs = new ForStatement(Arrays.asList(Expressions.newVar(iter, iteratorType,
                    staticMethodInvoke(StreamUtil.class, "from" + streamType.getSimpleName(), s))),
                    iter.invoke("hasNext"), update, bb);
        }
        b.add(new TryStatement(new BlockStatement(Arrays.asList(fs)), Collections.emptyList(), new BlockStatement(
                Arrays.asList(s.invoke("close").statement()))));

        n.children().forEach(cc -> cc.setIn(bb));
        String next = targetType == Object.class ? "next" : "next"
                + CodegenUtil.capitalizeFirstLetter(targetType.getName());
        n.firstChild().sources().setMain(new StreamingComposite(targetType, iter.invoke(next)));
        n.renderChildren();
    }

    static void mapToIndex(StreamingComposite c, SingleElement n) {
        ForAllI fa = n.findFirst(WalkOrder.PREVIOUS_OR_PARENT, ForAll.class).getI();

        StreamingMapComposite co = new StreamingMapComposite(Object.class, fa.getI().cast(long.class).box(Long.class),
                n.getElement().getType(), c.getAccessor());

        n.next().sources().setMain(co);
    }

}
