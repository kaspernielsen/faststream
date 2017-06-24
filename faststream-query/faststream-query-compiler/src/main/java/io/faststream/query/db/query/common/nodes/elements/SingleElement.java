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
package io.faststream.query.db.query.common.nodes.elements;

import static java.util.Objects.requireNonNull;

import io.faststream.query.db.query.common.nodes.render.ModelGenerator;
import io.faststream.query.db.query.compiler.Order;
import io.faststream.query.db.query.compiler.datasource.Composite;
import io.faststream.query.db.query.compiler.datasource.StreamingComposite;
import io.faststream.query.db.query.compiler.render.util.CacheStatement;
import io.faststream.query.db.query.plan.logical.LogicalElementProperties;

/**
 *
 * @author Kasper Nielsen
 */
public final class SingleElement extends RealNode {

    /** Logical information about the element. */
    private LogicalElementProperties element;

    /** The order of elements. The default is no order */
    private Order order = Order.NONE;

    /** {@inheritDoc} */
    @Override
    public void buildModel() {
        Composite v = sources().getMain();
        if (v instanceof StreamingComposite) {
            CacheStatement.addTo(this, (StreamingComposite) v);
        }
        if (!ModelGenerator.tryGenerate(v, this)) {
            throw new RuntimeException("Oops count not generate code for " + getOperation() + " for "
                    + v.getClass().getSimpleName());
        }
    }

    public SingleElement cloneIt() {
        SingleElement e = (SingleElement) super.cloneIt();
        e.order = order;
        e.element = element;
        return e;
    }

    public LogicalElementProperties getElement() {
        return element;
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
     * Returns whether or not null elements exist.
     *
     * @return whether or not null elements exist
     */
    public boolean isNullable() {
        return element.isNullable();
    }

    /**
     * @param element
     *            the element to set
     */
    public void setElement(LogicalElementProperties element) {
        this.element = element;
    }

    /**
     * Sets the order of elements.
     *
     * @param order
     *            the order of elements
     */
    public void setOrder(Order order) {
        this.order = requireNonNull(order);
    }

    /** {@inheritDoc} */
    public String toString() {
        return super.toString() + ", e = " + element;
    }
}
