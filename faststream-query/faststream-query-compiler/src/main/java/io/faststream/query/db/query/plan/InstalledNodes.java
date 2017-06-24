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
package io.faststream.query.db.query.plan;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.faststream.query.db.query.common.nodes.elements.MapElement;
import io.faststream.query.db.query.common.nodes.elements.MultimapElement;
import io.faststream.query.db.query.common.nodes.elements.SingleElement;
import io.faststream.query.db.query.node.Operation;
import io.faststream.query.db.query.node.defaults.AllQueryOperations;

/**
 *
 * @author Kasper Nielsen
 */
public final class InstalledNodes implements AllQueryOperations {

    static final ConcurrentHashMap<Operation, Class<? extends QueryNode>> MAP = new ConcurrentHashMap<>();

    static {
        MAP.put(C, SingleElement.class);
        MAP.put(M, MapElement.class);
        MAP.put(U, MultimapElement.class);
    }

    public static QueryNode newInstance(Operation tag) {
        Class<? extends QueryNode> clz = MAP.get(tag);
        if (clz == null) {
            for (Map.Entry<Operation, Class<? extends QueryNode>> e : MAP.entrySet()) {
                if (tag.is(e.getKey())) {
                    MAP.put(tag, clz = e.getValue());
                }
            }
            if (clz == null) {
                throw new RuntimeException("Unknown tag " + tag);
            }
        }
        try {
            QueryNode aon = clz.newInstance();
            aon.setOperation(tag);
            return aon;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new Error("Could not instantiate " + clz, e);
        }
    }

    public static QueryNode clone(QueryNode qn) {
        return newInstance(qn.getOperation());
    }
}
