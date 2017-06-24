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
public final class MultimapElement extends MapOrMultimapElement {

    /** The order of keys. The default is no order */
    private Order keyOrder = Order.NONE;

    Object keyVariable;

    /** The order of values. The default is no order */
    private Order valueOrder = Order.NONE;

    Object valueVariable;

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

    public Order getKeyOrder() {
        return keyOrder;
    }

    public Order getValueOrder() {
        return valueOrder;
    }

    public void setKeyOrder(Order keyOrder) {
        this.keyOrder = keyOrder;
    }

    public void setOrder(Order keyOrder, Order valueOrder) {
        setKeyOrder(keyOrder);
        setValueOrder(valueOrder);
    }

    public void setValueOrder(Order valueOrder) {
        this.valueOrder = valueOrder;
    }

    public String toString() {
        return super.toString() + ", key = [" + getKey() + "], value = [" + getValue() + "], keyOrder = " + keyOrder
                + ", valueOrder = " + valueOrder;
    }
}
