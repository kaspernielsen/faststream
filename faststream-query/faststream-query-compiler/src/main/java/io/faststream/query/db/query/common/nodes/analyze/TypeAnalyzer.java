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
package io.faststream.query.db.query.common.nodes.analyze;

import io.faststream.codegen.core.CodegenUtil;
import io.faststream.query.db.query.node.Operation;
import io.faststream.query.db.query.node.defaults.AllQueryOperations;
import io.faststream.query.db.query.plan.logical.LogicalElementProperties;
import io.faststream.query.db.query.plan.logical.LogicalReferenceTracker;

/**
 *
 * @author Kasper Nielsen
 */
public class TypeAnalyzer extends Analyzers implements AllQueryOperations {
    static LogicalElementProperties newVar(Class<?> type, boolean isNullable) {
        return new LogicalElementProperties(new LogicalReferenceTracker(), type, isNullable);
    }

    static LogicalElementProperties newVar(Class<?> type, boolean isNullable, LogicalElementProperties dependency) {
        return new LogicalElementProperties(new LogicalReferenceTracker(dependency.getReference()), type, isNullable);
    }

    static LogicalElementProperties newVar(Class<?> type, LogicalElementProperties dependency) {
        return newVar(type, type.isPrimitive(), dependency);
    }

    static LogicalElementProperties newVar(Class<?> type, boolean isNullable, LogicalElementProperties dependency1,
            LogicalElementProperties dependency2) {
        return new LogicalElementProperties(new LogicalReferenceTracker(dependency1.getReference(),
                dependency2.getReference()), type, isNullable);
    }

    public void col() {
        col((f, t) -> {
            t.setElement(f.getElement().clone());
        }, new Operation[] { null });

        // Mapping was
        // Class<?> toType = FunctionalInterfaces.returnTypeOf(sources().firstParameter().getType());
        // next.setElement(newVar(toType, toType.isPrimitive(), getElement()));

        col((f, t) -> t.setElement(newVar(long.class, true, f.getElement())), C_AS_LONG_STREAM);
        col((f, t) -> t.setElement(newVar(double.class, true, f.getElement())), C_AS_DOUBLE_STREAM);

        col((f, t) -> t.setElement(f.getElement().clone().setNullable(false)), C_FILTER_NULLS, C_FILTER_ON_TYPE);

        col((f, t) -> t.setElement(newVar(int.class, true, f.getElement())), C_MAP_TO_INT);
        col((f, t) -> t.setElement(newVar(long.class, true, f.getElement())), C_MAP_TO_LONG);
        col((f, t) -> t.setElement(newVar(double.class, true, f.getElement())), C_MAP_TO_DOUBLE);
        col((f, t) -> t.setElement(newVar(Object.class, true, f.getElement())), C_MAP_TO_OBJECT);
        col((f, t) -> t.setElement(newVar(CodegenUtil.boxClass(f.getElement().getType()), false, f.getElement())),
                C_MAP_BOXING);

        col((f, t) -> t.setElement(newVar(int.class, true, f.getElement())), C_FLAT_MAP_TO_INT);
        col((f, t) -> t.setElement(newVar(long.class, true, f.getElement())), C_FLAT_MAP_TO_LONG);
        col((f, t) -> t.setElement(newVar(double.class, true, f.getElement())), C_FLAT_MAP_TO_DOUBLE);
        col((f, t) -> t.setElement(newVar(Object.class, true, f.getElement())), C_FLAT_MAP_TO_OBJECT);

        col((f, t) -> t.setElement(newVar(Object.class, true, f.getElement())), C_GATHER);

        colToMap((f, t) -> {
            t.setKey(newVar(Object.class, false));
            t.setValue(f.getElement().clone());
        }, C_MAP_TO_INDEX);
        // colToMap((f, t) -> {
        // t.setKey(newVar(long.class, false));
        // t.setValue(f.getElement().clone());
        // }, C_MAP_TO_INDEX);
        //
        colToMap((f, t) -> {
            t.setKey(f.getElement().clone());
            t.setValue(newVar(long.class, false));
        }, C_FREQUENCY_COUNT);

        colToMulti((f, t) -> {
            t.setKey(newVar(Object.class, true, f.getElement()));
            t.setValue(f.getElement().clone());
        }, C_GROUP_BY);

    }

    public void map() {
        map((f, t) -> {
            t.setKey(f.getKey().clone());
            t.setValue(f.getValue().clone());
        }, new Operation[] { null });

        mapToCol((f, t) -> t.setElement(newVar(Object.class, false)), M_MAP_BI);
        mapToMulti((f, t) -> {
            t.setKey(newVar(Object.class, true));// /TODO do allow null keys???????
                t.setValue(f.getValue().clone());
        }, M_MAP_KEY);

        map((f, t) -> t.setValue(newVar(Object.class, true)), M_MAP_VALUE);
        mapToCol((f, t) -> t.setElement(f.getKey().clone()), M_VIEW_KEYS);
        mapToCol((f, t) -> t.setElement(f.getValue().clone()), M_VIEW_VALUES);
        mapToCol((f, t) -> t.setElement(newVar(Object.class, false)), M_VIEW_ENTRIES);
    }

    public void multi() {
        multi((f, t) -> {
            t.setKey(f.getKey().clone());
            t.setValue(f.getValue().clone());
        }, new Operation[] { null });

        multiToCol((f, t) -> t.setElement(newVar(Object.class, false)), U_MAP_BI);

        multiToCol((f, t) -> t.setElement(f.getKey().clone()), U_VIEW_KEYS);
        multiToCol((f, t) -> t.setElement(f.getValue().clone()), U_VIEW_VALUES);

        // public static final MultimapOperation U_MAP_ENTRY = of("U_MAP_ENTRY", U);
        // public static final MultimapOperation U_MAP_KEY = of("U_MAP_KEY", U);
        // public static final MultimapOperation U_MAP_VALUE = of("U_MAP_VALUE", U);
        //
        // public static final MultimapOperation U_KEYS = of("U_KEYS", U);
        // public static final MultimapOperation U_VALUES = of("U_VALUES", U);
        //
        // public static final MultimapOperation U_REDUCE = of("U_REDUCE", U);
        // public static final MultimapOperation U_COUNT = of("U_COUNT", U);
        // public static final MultimapOperation U_DISTINCT = of("U_DISTINCT", U);
        //
        // public static final MultimapOperation U_ANY = of("U_ANY", U);
        // public static final MultimapOperation U_FIRST = of("U_FIRST", U);
        // public static final MultimapOperation U_LAST = of("U_LAST", U);
        // public static final MultimapOperation U_ONE = of("U_ONE", U);
    }
}
