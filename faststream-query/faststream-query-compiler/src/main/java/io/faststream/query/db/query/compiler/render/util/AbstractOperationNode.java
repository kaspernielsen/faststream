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
package io.faststream.query.db.query.compiler.render.util;

import java.util.HashMap;
import java.util.Map;

import io.faststream.query.db.query.plan.QueryNode;

/**
 *
 * @author Kasper Nielsen
 */
public abstract class AbstractOperationNode extends QueryNode {

    private Map<String, Object> cache;

    public final void addImport(Class<?> clazz) {
        getTree().imports().add(clazz);
    }

    public final Object getObject(String name) {
        if (cache != null) {
            return cache.get(name);
        }
        return null;
    }

    public final StreamingGroup group() {
        if (this instanceof StreamingGroup) {
            return (StreamingGroup) this;
        }
        return ((AbstractOperationNode) getParent()).group();
    }

    public final void putObject(String name, Object value) {
        if (cache == null) {
            cache = new HashMap<>();
        }
        cache.put(name, value);
    }
}
