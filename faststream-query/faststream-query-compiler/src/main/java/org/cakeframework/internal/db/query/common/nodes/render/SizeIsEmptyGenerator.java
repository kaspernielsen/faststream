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

import org.cakeframework.internal.db.query.compiler.datasource.AnyStreaming;
import org.cakeframework.internal.db.query.compiler.datasource.ArrayOrListComposite;
import org.cakeframework.internal.db.query.compiler.datasource.StreamingComposite;
import org.cakeframework.internal.db.query.compiler.datasource.StreamingMapComposite;
import org.cakeframework.internal.db.query.compiler.render.util.AbstractOperationNode;

import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.statement.Statements;

/**
 * Generates code for isEmpty and size (stream.count).
 *
 * @author Kasper Nielsen
 */
public class SizeIsEmptyGenerator extends ModelGenerator {

    /** {@inheritDoc} */
    @Override
    void generate() {
        register(ArrayOrListComposite.class, SizeIsEmptyGenerator::size, SIZE);
        register(ArrayOrListComposite.class, SizeIsEmptyGenerator::isEmpty, IS_EMPTY);

        register(StreamingComposite.class, SizeIsEmptyGenerator::size, SIZE);
        register(StreamingComposite.class, SizeIsEmptyGenerator::isEmpty, IS_EMPTY);

        register(StreamingMapComposite.class, SizeIsEmptyGenerator::size, SIZE);
        register(StreamingMapComposite.class, SizeIsEmptyGenerator::isEmpty, IS_EMPTY);
    }

    static void isEmpty(ArrayOrListComposite c, AbstractOperationNode n) {
        n.b().addReturn(c.isEmpty().box(Boolean.class));
    }

    static void isEmpty(AnyStreaming c, AbstractOperationNode n) {
        n.b().add(staticField(Boolean.class, "FALSE").returnIt());
        // we are at the end of any stream and at least one element has passed all filters
        n.group().bReturnLast(staticField(Boolean.class, "TRUE"));
    }

    static void size(ArrayOrListComposite c, AbstractOperationNode n) {
        n.b().addReturn(c.size().box(Long.class));
    }

    static void size(AnyStreaming ignore, AbstractOperationNode n) {
        // We initialize a counter variable called size
        NameExpression size = new NameExpression("size");

        // define a size variable before the loop
        n.group().bAddFirst(Statements.newVar(size, long.class, literal(0L)));

        // Increment the size value by 1 in the loop
        n.b().add(size.postIncrement());

        // Adds a return as the last statement after the loop
        n.group().bReturnLast(size.box(Long.class));
    }
}
