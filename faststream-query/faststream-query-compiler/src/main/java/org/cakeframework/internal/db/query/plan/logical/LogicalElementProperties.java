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
package org.cakeframework.internal.db.query.plan.logical;

import static java.util.Objects.requireNonNull;

/**
 *
 * @author Kasper Nielsen
 */
public class LogicalElementProperties {

    private boolean isNullable;

    private LogicalReferenceTracker reference;

    private Class<?> type;

    public LogicalElementProperties(LogicalReferenceTracker reference, Class<?> type, boolean isNullable) {
        this.reference = reference;
        this.type = requireNonNull(type);
        this.isNullable = isNullable;
    }

    public LogicalReferenceTracker getReference() {
        return reference;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isNullable() {
        return isNullable || type.isPrimitive();
    }

    public LogicalElementProperties setNullable(boolean isNullable) {
        this.isNullable = isNullable;
        return this;
    }

    public void setReference(LogicalReferenceTracker reference) {
        this.reference = reference;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public LogicalElementProperties clone() {
        return new LogicalElementProperties(reference, type, isNullable);
    }

    public String toString() {
        return "type = " + type.getSimpleName() + ", isNullable = " + isNullable;
    }
}
