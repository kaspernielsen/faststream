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
package org.cakeframework.internal.db.query.common.nodes.elements;

import org.cakeframework.internal.db.query.common.nodes.render.ModelGenerator;
import org.cakeframework.internal.db.query.compiler.Order;
import org.cakeframework.internal.db.query.compiler.datasource.Composite;
import org.cakeframework.internal.db.query.compiler.datasource.StreamingMapComposite;
import org.cakeframework.internal.db.query.compiler.render.util.CacheStatement;

/**
 *
 * @author Kasper Nielsen
 */
public final class MapElement extends MapOrMultimapElement {

    private Object keyVariable;

    /** The order of elements. The default is no order */
    private Order order = Order.NONE;

    private Object valueVariable;

    /** {@inheritDoc} */
    @Override
    public void buildModel() {
        Composite v = sources().getMain();
        if (v instanceof StreamingMapComposite) {
            CacheStatement.addTo(this, (StreamingMapComposite) v);
        }
        if (!ModelGenerator.tryGenerate(v, this)) {
            throw new RuntimeException("Oops count not generate code for " + getOperation() + " for "
                    + v.getClass().getSimpleName());
        }
    }

    /**
     * @return the keyVariable
     */
    public Object getKeyVariable() {
        return keyVariable;
    }

    /**
     * @return the order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * @return the valueVariable
     */
    public Object getValueVariable() {
        return valueVariable;
    }

    /**
     * @param keyVariable
     *            the keyVariable to set
     */
    public void setKeyVariable(Object keyVariable) {
        this.keyVariable = keyVariable;
    }

    /**
     * @param order
     *            the order to set
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * @param valueVariable
     *            the valueVariable to set
     */
    public void setValueVariable(Object valueVariable) {
        this.valueVariable = valueVariable;
    }

    public String toString() {
        return super.toString() + ", key = [" + getKey() + "], value = [" + getValue() + "], order = " + order;
    }
}
