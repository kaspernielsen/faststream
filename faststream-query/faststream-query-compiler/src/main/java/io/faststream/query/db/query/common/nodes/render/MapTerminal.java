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

import io.faststream.codegen.model.expression.Literal;
import io.faststream.query.db.query.common.nodes.elements.MapElement;
import io.faststream.query.db.query.compiler.datasource.StreamingMapComposite;

/**
 *
 * @author Kasper Nielsen
 */
public class MapTerminal extends ModelGenerator {

    /** {@inheritDoc} */
    @Override
    void generate() {
        register(StreamingMapComposite.class, MapElement.class, (c, n) -> forEach(c, n), MT_FOR_EACH);
        register(StreamingMapComposite.class, MapElement.class, (c, n) -> to(c, n), MT_TO);
    }

    // public static final MapOperation MT_TO = of("MT_TO", M);
    // public static final MapOperation MT_TO_TYPE = of("MT_TO_TYPE", MT_TO);
    // public static final MapOperation MT_TO_MAP = of("MT_TO_MAP", MT_TO);
    //
    // public static final MapOperation MT_FOR_EACH = of("MT_FOR_EACH", M);
    // public static final MapOperation MT_FOR_EACH_ANYORDER = of("MT_FOR_EACH_ANYORDER", MT_FOR_EACH);
    // public static final MapOperation MT_FOR_EACH_ORDERED = of("MT_FOR_EACH_ORDERED", MT_FOR_EACH);
    //

    private void to(StreamingMapComposite c, MapElement n) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param c
     * @param n
     * @return
     */
    private void forEach(StreamingMapComposite c, MapElement n) {
        n.b().add(n.parameters().first().invokeFunctional(c.getKeyAccessor(), c.getValueAccessor()));

        // add a return as the last statement in the top block
        n.group().bReturnLast(Literal.nullLiteral());
    }

    // /** {@inheritDoc} */
    // @Override
    // public void create(BlockStatement b, MapComposite composite) {
    // b.addReturn(composite.isEmpty());
    // }

    // /** {@inheritDoc} */
    // @Override
    // public void render(CodegenBlock block) {
    // block.add(sources().getParameter(), ".apply(", getKeyVariable(), ", ", getValueVariable(), ");");
    // }

    // /** {@inheritDoc} */
    // @Override
    // void create(StreamingMapComposite v) {
    // b().add(sources().getParameter().invoke(methodNameOf(BiConsumer.class), v.getKeyAccessor(),
    // v.getValueAccessor()));
    //
    // // add a return as the last statement in the top block
    // group().bReturnLast(Literal.nullLiteral());
    // }

    //
    // // IterateMap iterateAll = n.insertBeforeThis(new IterateMap(mv));
    // // n.setKeyVariable(iterateAll.entry().withMethodInvocation("getKey"));
    // // n.setValueVariable(iterateAll.entry().withMethodInvocation("getValue"));
    // //
    // // iterateAll.addChild(n);
    // // iterateAll.insertAfterThis(new ReturnStatement("null"));
}
