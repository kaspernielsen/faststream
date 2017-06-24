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
package org.cakeframework.internal.db.query.compiler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.cakeframework.internal.db.nodes.simplecaching.QueryCacheConfiguration;
import org.cakeframework.internal.db.query.node.AbstractTerminalQueryOperationProcessor;
import org.cakeframework.internal.db.query.node.TerminalQueryOperationNode;
import org.cakeframework.internal.db.query.node.TerminalQueryOperationNodeProcessor;
import org.cakeframework.internal.db.query.plan.QueryEngine;

import io.faststream.codegen.core.Codegen;

/**
 * An abstract class for the configuration of compiled views.
 *
 * @author Kasper Nielsen
 */
public abstract class AbstractCompiledEntityConfiguration {

    /** Whether or not caching of generated views is disabled */
    private boolean isCachingDisabled;

    BiFunction<TerminalQueryOperationNode, TerminalQueryOperationNodeProcessor, Object> preprocessor;

    /** A map of all sources */
    private final LinkedHashMap<DataSource, Object> sources = new LinkedHashMap<>();

    public void setPreprocesser(BiFunction<TerminalQueryOperationNode, TerminalQueryOperationNodeProcessor, Object> processor) {
        this.preprocessor = processor;
    }

    /**
     * @param source
     *            the unique name of the source
     * @param instance
     *            the instance
     * @return a new data source that can be used to access fields and methods on the instance
     */
    public DataSource addSource(String source, Object instance) {
        DataSource ne = new DataSource(source, instance.getClass());
        if (sources.containsKey(source)) {
            throw new IllegalArgumentException("A source with the specified name has already been added, source = "
                    + source);
        }
        sources.put(ne, instance);
        return ne;
    }

    protected TerminalQueryOperationNodeProcessor cre(QueryEngine qe, Codegen c) {
        CompiledViewRoot r = new CompiledViewRoot(qe, c, this);

        final TerminalQueryOperationNodeProcessor q;
        if (isCachingDisabled()) {
            q = r;
        } else {
            QueryCacheConfiguration qcc = new QueryCacheConfiguration();
            q = qcc.create(r);
        }

        TerminalQueryOperationNodeProcessor p = q;
        if (preprocessor != null) {
            p = new AbstractTerminalQueryOperationProcessor() {
                public Object process(TerminalQueryOperationNode operation) {
                    return preprocessor.apply(operation, q);
                }
            };
        }
        return p;
    }

    /**
     * Returns all sources that have been added via {@link #addSource(String, Object)}.
     *
     * @return a map of sources mapping to instances
     */
    public Map<DataSource, Object> getSources() {
        return new LinkedHashMap<>(sources);
    }

    /**
     * Returns whether or not caching of generated classes is disabled.
     *
     * @return whether or not caching of generated classes is disabled
     * @see #setCachingDisabled(boolean)
     */
    public boolean isCachingDisabled() {
        return isCachingDisabled;
    }

    /**
     * Disables caching of generated classes.
     *
     * @param disableCaching
     *            if caching of generated classes should be disabled
     *
     * @see #isCachingDisabled()
     */
    public void setCachingDisabled(boolean disableCaching) {
        this.isCachingDisabled = disableCaching;
    }
}
