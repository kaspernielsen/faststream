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
package org.cakeframework.internal.db.nodes.simplecaching;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import org.cakeframework.internal.db.query.node.AbstractTerminalQueryOperationProcessor;
import org.cakeframework.internal.db.query.node.TerminalQueryOperationNode;
import org.cakeframework.internal.db.query.node.TerminalQueryOperationNodeProcessor;

/**
 * A view root that caches {@link TerminalQueryOperationNodeProcessor view processors}.
 *
 * @author Kasper Nielsen
 */
class QueryCacheProcessor extends AbstractTerminalQueryOperationProcessor {

    private final QueryCache cache = new QueryCache();

    private final AtomicLong cacheMisses = new AtomicLong();

    private final QueryCacheFactory factory;

    private final TerminalQueryOperationNodeProcessor serialProcessor;

    QueryCacheProcessor(QueryCacheFactory factory, TerminalQueryOperationNodeProcessor serialProcessor) {
        this.factory = requireNonNull(factory);
        this.serialProcessor = serialProcessor;
    }

    private Object cacheMiss(TerminalQueryOperationNode node) {
        Supplier<TerminalQueryOperationNodeProcessor> generator = factory.createCachable(node);
        if (generator != null) {
            return cache.cache(generator, node).process(node);
        } else {
            if (serialProcessor == null) {
                throw new Error("This node does not support evaluation");
            }
            return serialProcessor.process(node);
        }
    }

    long getCacheMisses() {
        return cacheMisses.get();
    }

    public Object process(TerminalQueryOperationNode node) {
        TerminalQueryOperationNodeProcessor cachedProcessor = cache.lookup(node);
        if (cachedProcessor != null) {
            return cachedProcessor.process(node); // cacheHits.incrementAndGet();
        }
        cacheMisses.incrementAndGet();
        return cacheMiss(node);
    }
}
