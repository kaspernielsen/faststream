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
package io.faststream.query.db.query.compiler.anew;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import io.faststream.query.db.query.node.TerminalQueryOperationNode;
import io.faststream.query.db.query.node.TerminalQueryOperationNodeProcessor;

/**
 * A view root that caches {@link TerminalQueryOperationNodeProcessor view processors}.
 *
 * @author Kasper Nielsen
 */
class QueryCacheProcessor<T> implements QueryCompiler<T> {

    private final QueryCache<T> cache = new QueryCache<>();

    private final AtomicLong cacheMisses = new AtomicLong();

    private final QueryCacheFactory<T> factory;

    private final T serialProcessor;

    QueryCacheProcessor(QueryCompiler<T> factory, T serialProcessor) {
        this.factory = new QueryCacheFactory<T>() {
            public Supplier<T> createCachable(TerminalQueryOperationNode node) {
                return () -> factory.create(node);
            }
        };
        this.serialProcessor = serialProcessor;
    }

    private T cacheMiss(TerminalQueryOperationNode node) {
        Supplier<T> generator = factory.createCachable(node);
        if (generator != null) {
            try {
                return cache.cache(generator, node);
            } catch (RuntimeException e) {
                System.err.println(node);
                throw e;
            }
        } else {
            if (serialProcessor == null) {
                throw new Error("This node does not support evaluation");
            }
            return serialProcessor;
        }
    }

    long getCacheMisses() {
        return cacheMisses.get();
    }

    public T create(TerminalQueryOperationNode node) {
        T cachedProcessor = cache.lookup(node);
        if (cachedProcessor != null) {
            return cachedProcessor;
        }
        cacheMisses.incrementAndGet();
        return cacheMiss(node);
    }
}
