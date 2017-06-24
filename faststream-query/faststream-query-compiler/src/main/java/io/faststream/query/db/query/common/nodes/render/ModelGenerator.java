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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import io.faststream.query.db.query.common.nodes.elements.SingleElement;
import io.faststream.query.db.query.compiler.datasource.Composite;
import io.faststream.query.db.query.compiler.render.util.AbstractOperationNode;
import io.faststream.query.db.query.node.Operation;
import io.faststream.query.db.query.node.defaults.AllQueryOperations;
import io.faststream.query.db.query.plan.QueryNode;

/**
 *
 * @author Kasper Nielsen
 */
public abstract class ModelGenerator implements AllQueryOperations {

    ConcurrentHashMap<Class<?>, PredicateContainer<QueryNode, BiConsumer<Composite, AbstractOperationNode>>> map = new ConcurrentHashMap<>();

    static final ConcurrentHashMap<Class<?>, PredicateContainer<QueryNode, BiConsumer<Composite, AbstractOperationNode>>> ALL;

    static {
        ALL = new ConcurrentHashMap<>();
        List<ModelGenerator> l = Arrays.asList(new CollectionIntermediate(), new CollectionTerminal(),
                new SizeIsEmptyGenerator(), new MapIntermediate(), new MapTerminal(),
                new MappingGenerator());
        for (ModelGenerator c : l) {
            c.map = ALL;
            c.generate();
        }
    }

    public static boolean tryGenerate(Composite composite, AbstractOperationNode node) {
        PredicateContainer<QueryNode, BiConsumer<Composite, AbstractOperationNode>> pc = ALL.get(composite.getClass());
        if (pc == null) {
            for (Map.Entry<Class<?>, PredicateContainer<QueryNode, BiConsumer<Composite, AbstractOperationNode>>> e : ALL
                    .entrySet()) {
                if (e.getKey().isAssignableFrom(composite.getClass())) {
                    if (e.getValue().findFirst(node) != null) {
                        ALL.put(composite.getClass(), pc = e.getValue());
                    }
                }
            }
        }
        if (pc != null) {
            BiConsumer<Composite, AbstractOperationNode> f = pc.findFirst(node);
            if (f != null) {
                f.accept(composite, node);
                return true;
            }
        }
        return false;
    }

    <T> void registerSingle(Class<T> composite, BiConsumer<T, SingleElement> c, Operation... elements) {
        register(composite, SingleElement.class, c, elements);
    }

    <T> void register(Class<T> composite, BiConsumer<T, AbstractOperationNode> c, Operation... elements) {
        register(composite, AbstractOperationNode.class, c, elements);
    }

    @SuppressWarnings("unchecked")
    <T, S extends AbstractOperationNode> void register(Class<T> composite, Class<S> nodeType, BiConsumer<T, S> c,
            Operation... elements) {
        PredicateContainer<QueryNode, BiConsumer<Composite, AbstractOperationNode>> pc = map.computeIfAbsent(composite,
                e -> new PredicateContainer<>());
        pc.add(p -> p.getOperation().isAnyOf(elements), (BiConsumer<Composite, AbstractOperationNode>) c);
    }

    abstract void generate();
}
