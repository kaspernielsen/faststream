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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.model.util.Identifier;

/**
 *
 * @author Kasper Nielsen
 */
public class AbstractCompiledEntityConfiguration<T> {

    final List<Identifier> identifiers = new ArrayList<>();

    /** Whether or not caching of generated views is disabled */
    private boolean isCachingDisabled;

    final Class<?> type;

    final Codegen codegen;

    AbstractCompiledEntityConfiguration(Codegen codegen, Class<?> type) {
        this.codegen = requireNonNull(codegen);
        this.type = requireNonNull(type);
    }

    public void addParameter(Identifier i) {
        identifiers.add(requireNonNull(i));
    }

    public QueryCompiler<T> create() {
        return null;
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
