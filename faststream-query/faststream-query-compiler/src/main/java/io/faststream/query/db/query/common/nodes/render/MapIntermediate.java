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

import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Expressions;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.util.Identifier;
import io.faststream.query.db.query.common.nodes.elements.MapElement;
import io.faststream.query.db.query.common.nodes.elements.MapOrMultimapElement;
import io.faststream.query.db.query.common.nodes.elements.RealNode;
import io.faststream.query.db.query.compiler.datasource.StreamingComposite;
import io.faststream.query.db.query.compiler.datasource.StreamingMapComposite;
import io.faststream.query.db.query.compiler.render.util.FunctionalInterfaces;
import io.faststream.query.db.query.runtime.MapUtil;

/**
 *
 * @author Kasper Nielsen
 */
public class MapIntermediate extends ModelGenerator {
    /** {@inheritDoc} */
    @Override
    void generate() {
        register(StreamingMapComposite.class, MapOrMultimapElement.class, (c, n) -> mapValue(c, n), M_MAP_VALUE,
                U_MAP_VALUE);
        register(StreamingMapComposite.class, MapOrMultimapElement.class, (c, n) -> mapKey(c, n), M_MAP_KEY, U_MAP_KEY);
        register(StreamingMapComposite.class, MapOrMultimapElement.class, (c, n) -> mapEntry(c, n), M_MAP_BI, U_MAP_BI);
        register(StreamingMapComposite.class, MapOrMultimapElement.class, (c, n) -> mapViewKeys(c, n), M_VIEW_KEYS,
                U_VIEW_KEYS);
        register(StreamingMapComposite.class, MapOrMultimapElement.class, (c, n) -> mapViewValues(c, n), M_VIEW_VALUES,
                U_VIEW_VALUES);
        register(StreamingMapComposite.class, MapElement.class, (c, n) -> mapViewEntries(c, n), M_VIEW_ENTRIES);

        register(StreamingMapComposite.class, MapOrMultimapElement.class, (c, n) -> filter(c, n), M_FILTER, U_FILTER);
    }

    static void filter(StreamingMapComposite c, MapOrMultimapElement n) {
        final BlockStatement bs;
        if (n.is(M_FILTER_PREDICATE, U_FILTER_PREDICATE)) {
            bs = n.b().addIfBlock(n.parameters().first().invokeFunctional(c.getKeyAccessor(), c.getValueAccessor()));
        } else { // filter by value
            Expression accessor = n.is(M_FILTER_BY_KEY, U_FILTER_BY_KEY) ? c.getKeyAccessor() : c.getValueAccessor();
            if (n.is(M_FILTER_BY_VALUE_NULLS, U_FILTER_BY_VALUE_NULLS)) {
                bs = n.b().addIfBlock(accessor.equalsTo(literal(null)).encapsulate().not().simplify());
            } else if (n.is(M_FILTER_BY_KEY_TYPE, M_FILTER_BY_VALUE_TYPE, U_FILTER_BY_KEY_TYPE, U_FILTER_BY_VALUE_TYPE)) {
                bs = n.b().addIfBlock(n.parameters().first().accessor().invoke("isInstance", accessor));
            } else {
                bs = n.b().addIfBlock(n.parameters().first().invokeFunctional(accessor));
            }
        }
        n.children().forEach(cc -> cc.setIn(bs));

        // Set the main source to the list we just created
        n.firstChild().sources().setMain(c);
        n.renderChildren();

        // QueryOperationType M_MAP_KEY = of("M_MAP_KEY", M_MAP_FUNCTION);
        //
        // QueryOperationType M_ORDERING = of("M_ORDERING", M);
        // QueryOperationType M_SHUFFLE = of("M_SHUFFLE", M_ORDERING);
        // QueryOperationType M_REVERSE = of("M_REVERSE", M_ORDERING);
        // QueryOperationType M_SORT = of("M_SORT", M_ORDERING);
        // QueryOperationType M_SORT_COMPARATOR = of("M_SORT_COMPARATOR", M_SORT);
        // QueryOperationType M_SORT_BY_KEY_NATURAL = of("M_SORT_BY_KEY_NATURAL", M_SORT);
        // QueryOperationType M_SORT_BY_KEY_COMPARATOR = of("M_SORT_BY_KEY_COMPARATOR", M_SORT);
        // QueryOperationType M_SORT_BY_KEY_NATURAL_REVERSE = of("M_SORT_BY_KEY_NATURAL_REVERSE",
        // M_SORT);
        // QueryOperationType M_SORT_BY_KEY_COMPARATOR_REVERSE =
        // of("M_SORT_BY_KEY_COMPARATOR_REVERSE", M_SORT);
        // QueryOperationType M_SORT_BY_VALUE_NATURAL = of("M_SORT_BY_VALUE_NATURAL", M_SORT);
        // QueryOperationType M_SORT_BY_VALUE_COMPARATOR = of("M_SORT_BY_VALUE_COMPARATOR", M_SORT);
        // QueryOperationType M_SORT_BY_VALUE_NATURAL_REVERSE =
        // of("M_SORT_BY_VALUE_NATURAL_REVERSE", M_SORT);
        // QueryOperationType M_SORT_BY_VALUE_COMPARATOR_REVERSE =
        // of("M_SORT_BY_VALUE_COMPARATOR_REVERSE", M_SORT);
        //
        // QueryOperationType M_TRUNCATE_TAKE = of("M_TRUNCATE_TAKE", M);
    }

    private void mapViewKeys(StreamingMapComposite c, RealNode n) {
        n.next().sources().setMain(new StreamingComposite(c.getKeyType(), c.getKeyAccessor()));
    }

    private void mapViewValues(StreamingMapComposite c, RealNode n) {
        n.next().sources().setMain(new StreamingComposite(c.getValueType(), c.getValueAccessor()));
    }

    private void mapViewEntries(StreamingMapComposite c, RealNode n) {
        Class<?> toType = Object.class;
        Identifier e = new Identifier(Object.class, "entry");
        n.addImport(MapUtil.class);
        n.b().add(
                Expressions.newVar(e, toType,
                        staticMethodInvoke(MapUtil.class, "immutableEntry", c.getKeyAccessor(), c.getValueAccessor())));
        n.next().sources().setMain(new StreamingComposite(toType, e));

    }

    private void mapValue(StreamingMapComposite c, RealNode n) {
        Class<?> toType = FunctionalInterfaces.returnTypeOf(n.parameters().first().getType());
        Identifier e = new Identifier(toType, "mapped");
        n.b().add(Expressions.newVar(e, toType, n.parameters().first().invokeFunctional(c.getValueAccessor())));

        n.next().sources().setMain(new StreamingMapComposite(c.getKeyType(), c.getKeyAccessor(), toType, e));
    }

    private void mapKey(StreamingMapComposite c, RealNode n) {
        Class<?> toType = FunctionalInterfaces.returnTypeOf(n.parameters().first().getType());
        Identifier e = new Identifier(toType, "mapped");
        n.b().add(Expressions.newVar(e, toType, n.parameters().first().invokeFunctional(c.getKeyAccessor())));

        n.next().sources().setMain(new StreamingMapComposite(toType, e, c.getValueType(), c.getValueAccessor()));
    }

    private void mapEntry(StreamingMapComposite c, RealNode n) {
        Class<?> toType = FunctionalInterfaces.returnTypeOf(n.parameters().first().getType());
        Identifier e = new Identifier(toType, "mapped");
        n.b().add(
                Expressions.newVar(e, toType,
                        n.parameters().first().invokeFunctional(c.getKeyAccessor(), c.getValueAccessor())));

        n.next().sources().setMain(new StreamingComposite(toType, e));
    }

}
