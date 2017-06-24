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
package io.faststream.query.db.query.compiler;

import static java.util.Objects.requireNonNull;

import io.faststream.codegen.model.expression.Expression;
import io.faststream.query.db.query.common.nodes.elements.MapElement;
import io.faststream.query.db.query.compiler.render.util.AbstractOperationNode;
import io.faststream.query.db.query.plan.logical.LogicalElementProperties;
import io.faststream.query.db.query.plan.logical.LogicalReferenceTracker;

/**
 *
 * @author Kasper Nielsen
 */
public class CompiledMapViewConfiguration {
    /** <tt>True</tt> if keys can be null. */
    private boolean isKeyNullable = true;

    /** <tt>True</tt> if values can be null. */
    private boolean isValueNullable = true;

    /** Type of keys. */
    private Class<?> keyType = Object.class;

    /** The order of elements. */
    private Order order = Order.NONE;

    Expression size;

    /** Type of values. */
    private Class<?> valueType = Object.class;

    /**
     * Returns the type of keys.
     *
     * @return the type of keys
     */
    public Class<?> getKeyType() {
        return keyType;
    }

    /**
     * Returns the order of elements.
     *
     * @return the order of elements
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Returns the type of values.
     *
     * @return the type of values
     */
    public Class<?> getValueType() {
        return valueType;
    }

    // /**
    // * Creates a new MapView from the specified {@link QueryResultOperationProcessor}.
    // *
    // * @param processor
    // * the view processor
    // * @return the new map view
    // */
    // public <T> MapView<K, V> create(QueryResultOperationProcessor processor) {
    // return AbstractMapView.from(wrapInDebuggingProcecure(processor));
    // }

    // /**
    // * Creates a new map view from the specified map
    // *
    // * @param map
    // * the map with data
    // * @return the new map view
    // */
    // @Deprecated
    // public MapView<K, V> createFromMap(Map<? extends K, ? extends V> map) {
    // throw new UnsupportedOperationException();
    // // return create(SerialMapViewProcessor.newProcessorFromMap(map));
    // }

    public AbstractOperationNode init() {
        MapElement cd = new MapElement();
        cd.setKey(new LogicalElementProperties(new LogicalReferenceTracker(), Object.class, false));
        cd.setValue(new LogicalElementProperties(new LogicalReferenceTracker(), Object.class, false));

        // Identifier id = new Identifier((Class<?>) null, "source");
        //
        // Expression table = id.fieldAccess("entries");
        // Type entry = typeOf(Data.MapEntry.class);
        // ChainedMapComposite mc = ChainedMapComposite.create(id, table, entry, entry.withFieldAccess("key"),
        // entry.withFieldAccess("value"), entry.withFieldAccess("next"));
        // mc.setImmutable(true);
        // cd.sources().setMain(mc);
        return cd;
    }

    // som getSize(), hvis isEmpty==null, prov getSize, hvis ogsaa null, itererer
    public Object isEmpty() {
        return null;
    }

    /**
     * Returns whether or not keys can be {@code null} (nullable).
     *
     * @return whether or not keys are nullable
     */
    public boolean isKeyNullable() {
        return isKeyNullable;
    }

    /**
     * Returns whether or not values can be {@code null} (nullable).
     *
     * @return whether or not values are nullable
     */
    public boolean isValueNullable() {
        return isValueNullable;
    }

    public void setKeyNullable(boolean isKeyNullable) {
        this.isKeyNullable = isKeyNullable;
    }

    public void setKeyType(Class<?> keyType) {
        this.keyType = requireNonNull(keyType, "keyType is null");
    }

    public void setOrder(Order order) {
        this.order = requireNonNull(order, "order is null");
    }

    /**
     * Sets an expression that can calculate the size fast.
     *
     */
    public void setSizeExpression(Expression size) {
        this.size = size;
    }

    public void setValueNullable(boolean isValueNullable) {
        this.isValueNullable = isValueNullable;
    }

    public void setValueType(Class<?> valueType) {
        this.valueType = requireNonNull(valueType, "valueType is null");
    }
}
