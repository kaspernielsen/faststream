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
package org.cakeframework.internal.db.query.compiler.anew;

import java.util.function.Supplier;

import org.cakeframework.internal.db.query.node.TerminalQueryOperationNode;

/**
 *
 * @author Kasper Nielsen
 */
public interface QueryCacheFactory<T> {

    /**
     * Returns a generator that can create a view node processor of the specified type.
     * <p>
     * A generator is returned instead of a view node processor to allow for clients to create them lazily.
     * <p>
     * Returns null if the factory does not know how to create the query. In which case another factory, for example, an
     * interpreter can be used.
     *
     * @param node
     *            the node to process
     * @return the result of the processing
     */
    Supplier<T> createCachable(TerminalQueryOperationNode node);
}
