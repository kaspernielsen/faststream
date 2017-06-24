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
package org.cakeframework.internal.db.query.common.nodes.analyze;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.cakeframework.internal.db.query.common.nodes.elements.MapElement;
import org.cakeframework.internal.db.query.common.nodes.elements.MultimapElement;
import org.cakeframework.internal.db.query.common.nodes.elements.SingleElement;
import org.cakeframework.internal.db.query.compiler.render.util.AbstractOperationNode;
import org.cakeframework.internal.db.query.node.Operation;
import org.cakeframework.internal.db.query.plan.QueryNode;

/**
 *
 * @author Kasper Nielsen
 */
public abstract class Analyzers {

    public void col(BiConsumer<SingleElement, SingleElement> con, Operation... matchingElements) {
        register(SingleElement.class, SingleElement.class, con, matchingElements);
    }

    public void colToMap(BiConsumer<SingleElement, MapElement> con, Operation... matchingElements) {
        register(SingleElement.class, MapElement.class, con, matchingElements);
    }

    public void colToMulti(BiConsumer<SingleElement, MultimapElement> con, Operation... matchingElements) {
        register(SingleElement.class, MultimapElement.class, con, matchingElements);
    }

    public void map(BiConsumer<MapElement, MapElement> con, Operation... matchingElements) {
        register(MapElement.class, MapElement.class, con, matchingElements);
    }

    public void mapToCol(BiConsumer<MapElement, SingleElement> con, Operation... matchingElements) {
        register(MapElement.class, SingleElement.class, con, matchingElements);
    }

    public void mapToMulti(BiConsumer<MapElement, MultimapElement> con, Operation... matchingElements) {
        register(MapElement.class, MultimapElement.class, con, matchingElements);
    }

    public void multi(BiConsumer<MultimapElement, MultimapElement> con, Operation... matchingElements) {
        register(MultimapElement.class, MultimapElement.class, con, matchingElements);
    }

    public void multiToCol(BiConsumer<MultimapElement, SingleElement> con, Operation... matchingElements) {
        register(MultimapElement.class, SingleElement.class, con, matchingElements);
    }

    public void multiToMap(BiConsumer<MultimapElement, MapElement> con, Operation... matchingElements) {
        register(MultimapElement.class, MapElement.class, con, matchingElements);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static <S extends AbstractOperationNode, T extends AbstractOperationNode> void register(Class<S> from, Class<T> to,
            BiConsumer<S, T> con, Operation... matchingElements) {
        for (Operation e : matchingElements) {
            m.add(new Reg(e, from, to, (BiConsumer) con));
        }
    }

    static {
        m = new ArrayList<>();
        new TypeAnalyzer().foo();
        new OrderAnalyzers().foo();
    }

    void foo() {
        col();
        map();
        multi();
    }

    public abstract void col();

    public abstract void map();

    public abstract void multi();

    private static final ArrayList<Reg> m;

    // private static final Map<Element, List<BiConsumer<Object, Object>>> cache = new ConcurrentHashMap<>();

    public static void analyze(QueryNode from, QueryNode to) {
        // List<BiConsumer<Object, Object>> consumers = cache.computeIfAbsent(from.getOperation(), e -> find(e, from,
        // to));
        List<BiConsumer<Object, Object>> consumers = find(from.getOperation(), from, to);
        consumers.forEach(c -> c.accept(from, to));
    }

    static List<BiConsumer<Object, Object>> find(Operation e, QueryNode from, QueryNode to) {
        List<BiConsumer<Object, Object>> result = new ArrayList<>();
        for (Reg r : m) {
            if (r.element == null || e.is(r.element)) {
                if (r.from.isAssignableFrom(from.getClass()) && r.to.isAssignableFrom(to.getClass())) {
                    result.add(r.consumer);
                }
            }
        }
        return result;
    }

    static class Reg {
        final Operation element;
        final Class<? extends QueryNode> from;
        final Class<? extends QueryNode> to;
        final BiConsumer<Object, Object> consumer;

        Reg(Operation element, Class<? extends QueryNode> from, Class<? extends QueryNode> to,
                BiConsumer<Object, Object> consumer) {
            this.element = element;
            this.from = from;
            this.to = to;
            this.consumer = consumer;
        }
    }
}
