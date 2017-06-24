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

import static io.faststream.codegen.model.expression.Expressions.staticMethodInvoke;

import org.cakeframework.internal.db.query.common.nodes.elements.MapElement;
import org.cakeframework.internal.db.query.compiler.datasource.StreamingComposite;
import org.cakeframework.internal.db.query.compiler.datasource.StreamingMapComposite;
import org.cakeframework.internal.db.query.compiler.render.util.FunctionalInterfaces;
import org.cakeframework.internal.db.query.runtime.MapUtil;

import io.faststream.codegen.model.expression.Expressions;
import io.faststream.codegen.model.util.Identifier;

/**
 *
 * @author Kasper Nielsen
 */
public class MultimapIntermediate extends ModelGenerator {

    /** {@inheritDoc} */
    @Override
    void generate() {
        register(StreamingMapComposite.class, MapElement.class, (c, n) -> mapValue(c, n), U_MAP);
        register(StreamingMapComposite.class, MapElement.class, (c, n) -> mapEntry(c, n), M_MAP_BI);
        register(StreamingMapComposite.class, MapElement.class, (c, n) -> mapViewKeys(c, n), M_VIEW_KEYS);
        register(StreamingMapComposite.class, MapElement.class, (c, n) -> mapViewValues(c, n), M_VIEW_VALUES);
        register(StreamingMapComposite.class, MapElement.class, (c, n) -> mapViewEntries(c, n), M_VIEW_ENTRIES);
    }

    private void mapViewKeys(StreamingMapComposite c, MapElement n) {
        n.next().sources().setMain(new StreamingComposite(c.getKeyType(), c.getKeyAccessor()));
    }

    private void mapViewValues(StreamingMapComposite c, MapElement n) {
        n.next().sources().setMain(new StreamingComposite(c.getValueType(), c.getValueAccessor()));
    }

    private void mapViewEntries(StreamingMapComposite c, MapElement n) {
        Class<?> toType = Object.class;
        Identifier e = new Identifier(Object.class, "entry");
        n.addImport(MapUtil.class);
        n.b().add(
                Expressions.newVar(e, toType,
                        staticMethodInvoke(MapUtil.class, "immutableEntry", c.getKeyAccessor(), c.getValueAccessor())));
        n.next().sources().setMain(new StreamingComposite(toType, e));

    }

    private void mapValue(StreamingMapComposite c, MapElement n) {
        Class<?> toType = FunctionalInterfaces.returnTypeOf(n.parameters().first().getType());
        Identifier e = new Identifier(toType, "mapped");
        n.b().add(Expressions.newVar(e, toType, n.parameters().first().invokeFunctional(c.getAccessor())));

        n.next().sources().setMain(new StreamingMapComposite(c.getKeyType(), c.getKeyAccessor(), toType, e));
    }

    private void mapEntry(StreamingMapComposite c, MapElement n) {
        Class<?> toType = FunctionalInterfaces.returnTypeOf(n.parameters().first().getType());
        Identifier e = new Identifier(toType, "mapped");
        n.b().add(
                Expressions.newVar(e, toType,
                        n.parameters().first().invokeFunctional(c.getKeyAccessor(), c.getValueAccessor())));

        n.next().sources().setMain(new StreamingComposite(toType, e));
    }

    // public static final MapOperation M_MAP_KEY = of("M_MAP_KEY", M_MAP_FUNCTION);

    // public static final MapOperation M_FILTER = of("M_FILTER", M);
    // public static final MapOperation M_FILTER_PREDICATE = of("M_FILTER_PREDICATE", M_FILTER);
    // public static final MapOperation M_FILTER_BY_KEY = of("M_FILTER_BY_KEY", M_FILTER );
    // public static final MapOperation M_FILTER_BY_KEY_PREDICATE = of("M_FILTER_BY_KEY_PREDICATE", M_FILTER_BY_KEY);
    // public static final MapOperation M_FILTER_BY_KEY_TYPE = of("M_FILTER_BY_KEY_TYPE", M_FILTER_BY_KEY);
    // public static final MapOperation M_FILTER_BY_VALUE = of("M_FILTER_BY_VALUE", M_FILTER );
    // public static final MapOperation M_FILTER_BY_VALUE_NULLS = of("M_FILTER_BY_VALUE_NULLS", M_FILTER_BY_VALUE);
    // public static final MapOperation M_FILTER_BY_VALUE_PREDICATE = of("M_FILTER_BY_VALUE_PREDICATE",
    // M_FILTER_BY_VALUE);
    // public static final MapOperation M_FILTER_BY_VALUE_TYPE = of("M_FILTER_BY_VALUE_TYPE", M_FILTER_BY_VALUE);
    //
    // public static final MapOperation M_ORDERING = of("M_ORDERING", M);
    // public static final MapOperation M_SHUFFLE = of("M_SHUFFLE", M_ORDERING);
    // public static final MapOperation M_REVERSE = of("M_REVERSE", M_ORDERING);
    // public static final MapOperation M_SORT = of("M_SORT", M_ORDERING);
    // public static final MapOperation M_SORT_COMPARATOR = of("M_SORT_COMPARATOR", M_SORT);
    // public static final MapOperation M_SORT_BY_KEY_NATURAL = of("M_SORT_BY_KEY_NATURAL", M_SORT);
    // public static final MapOperation M_SORT_BY_KEY_COMPARATOR = of("M_SORT_BY_KEY_COMPARATOR", M_SORT);
    // public static final MapOperation M_SORT_BY_KEY_NATURAL_REVERSE = of("M_SORT_BY_KEY_NATURAL_REVERSE", M_SORT);
    // public static final MapOperation M_SORT_BY_KEY_COMPARATOR_REVERSE = of("M_SORT_BY_KEY_COMPARATOR_REVERSE",
    // M_SORT);
    // public static final MapOperation M_SORT_BY_VALUE_NATURAL = of("M_SORT_BY_VALUE_NATURAL", M_SORT);
    // public static final MapOperation M_SORT_BY_VALUE_COMPARATOR = of("M_SORT_BY_VALUE_COMPARATOR", M_SORT);
    // public static final MapOperation M_SORT_BY_VALUE_NATURAL_REVERSE = of("M_SORT_BY_VALUE_NATURAL_REVERSE", M_SORT);
    // public static final MapOperation M_SORT_BY_VALUE_COMPARATOR_REVERSE = of("M_SORT_BY_VALUE_COMPARATOR_REVERSE",
    // M_SORT);
    //
    // public static final MapOperation M_TRUNCATE_TAKE = of("M_TRUNCATE_TAKE", M);

    // /** {@inheritDoc} */
    // @Override
    // void create(StreamingMapComposite v) {
    // StreamingComposite c = new StreamingComposite(v.getKeyType(), v.getKeyAccessor());
    // next().sources().setMain(c);
    // }
}
