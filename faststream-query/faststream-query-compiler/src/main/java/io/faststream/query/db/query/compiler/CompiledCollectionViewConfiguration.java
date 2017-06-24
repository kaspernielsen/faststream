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

import io.faststream.codegen.core.Codegen;
import io.faststream.query.db.nodes.view.collectionview.AbstractCollectionView;
import io.faststream.query.db.query.common.nodes.elements.SingleElement;
import io.faststream.query.db.query.compiler.datasource.ArrayOrListComposite;
import io.faststream.query.db.query.compiler.render.util.GroupNodes;
import io.faststream.query.db.query.compiler.render.util.IntroduceArrays;
import io.faststream.query.db.query.compiler.render.util.IntroduceForAll;
import io.faststream.query.db.query.compiler.render.util.ReducingNodes;
import io.faststream.query.db.query.compiler.render.util.SetRealNode;
import io.faststream.query.db.query.plan.QueryEngine;
import io.faststream.query.db.query.plan.QueryEngineBuilder;
import io.faststream.query.db.query.plan.QueryPlan;
import io.faststream.query.db.query.plan.logical.LogicalElementProperties;
import io.faststream.query.db.query.plan.logical.LogicalReferenceTracker;
import io.faststream.query.util.view.CollectionView;

/**
 *
 * @author Kasper Nielsen
 */
public class CompiledCollectionViewConfiguration<T> extends AbstractCompiledEntityConfiguration {

    /** Whether or not elements can be null. */
    private boolean isNullable = true;

    ArrayOrListComposite main;

    /** The order of elements */
    private Order order = Order.NONE;

    /** The type of elements */
    private Class<?> type = Object.class;

    public ViewFactory<CollectionView<T>> compile(Codegen c) {
        QueryEngineBuilder m = new QueryEngineBuilder();
        m.add(new SetRealNode());
        m.add(plan -> plan.getPq().print(n -> "" + n.getOperation()));
        m.add(new GroupNodes());

        m.add(new IntroduceForAll().processAll());
        m.add(new ReducingNodes().processAll());

        // m.add(plan -> plan.print(n -> "" + n.getOperation()));
        m.add(new IntroduceArrays());
        m.add(plan -> plan.getPq().print(n -> "" + n.getOperation()));
        // m.add(new ReducingNodes().processAll());

        QueryEngine qo = new QueryEngine(m) {
            public void initRoot(QueryPlan plan) {
                SingleElement cd = new SingleElement();
                cd.setElement(new LogicalElementProperties(new LogicalReferenceTracker(), getType(), isNullable()));
                main.setImmutable(true);
                cd.sources().setMain(main);
                plan.getPq().setRoot(cd);
            }
        };
        return new ViewFactory<CollectionView<T>>() {

            @Override
            public CollectionView<T> create(Object... sourceInstances) {
                return AbstractCollectionView.from(cre(qo, c));
            }
        };
    }

    /** @return the order of elements as set by {@link #setOrder(Order)} */
    public Order getOrder() {
        return order;
    }

    /** @return the type of elements as set by {@link #setType(Class)} */
    public Class<?> getType() {
        return type;
    }

    /** @return whether or not elements are nullable as set by {@link #setNullable(boolean)} */
    public boolean isNullable() {
        return isNullable;
    }

    /**
     * Sets the main data structure.
     *
     * @param ibc
     */
    public CompiledCollectionViewConfiguration<T> setMain(ArrayOrListComposite ibc) {
        this.main = ibc;
        return this;
    }

    /**
     * Sets whether or not the underlying data structure can contain nulls.
     */
    public CompiledCollectionViewConfiguration<T> setNullable(boolean isNullable) {
        this.isNullable = isNullable;
        return this;
    }

    /**
     * Sets the order of elements. The default order is {@link Order#NONE}.
     *
     * @param order
     *            the order of elements
     */
    public CompiledCollectionViewConfiguration<T> setOrder(Order order) {
        this.order = requireNonNull(order, "order is null");
        return this;
    }

    /**
     * Sets the type of elements in this data structure.
     *
     * @param type
     */
    public CompiledCollectionViewConfiguration<T> setType(Class<?> type) {
        this.type = requireNonNull(type, "type is null");
        return this;
    }
}
