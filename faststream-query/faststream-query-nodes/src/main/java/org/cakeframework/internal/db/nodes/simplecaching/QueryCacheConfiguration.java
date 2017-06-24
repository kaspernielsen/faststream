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

import org.cakeframework.internal.db.query.node.TerminalQueryOperationNodeProcessor;

/**
 * 
 * @author Kasper Nielsen
 */
public class QueryCacheConfiguration {

    private boolean isDisabled;

    // Used if factory returns null
    private TerminalQueryOperationNodeProcessor notCacheable;

    public TerminalQueryOperationNodeProcessor create(QueryCacheFactory factory) {
        return new QueryCacheProcessor(factory, notCacheable);
    }

    public TerminalQueryOperationNodeProcessor getNotCacheable() {
        return notCacheable;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    /**
     * @param isDisabled
     *            the isDisabled to set
     */
    public QueryCacheConfiguration setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
        return this;
    }

    public QueryCacheConfiguration setNotCacheable(TerminalQueryOperationNodeProcessor notCacheable) {
        this.notCacheable = notCacheable;
        return this;
    }
}
