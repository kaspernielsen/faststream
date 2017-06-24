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
package io.faststream.query.db.query.node;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;

/**
 *
 * @author Kasper Nielsen
 */
public class QueryOperationNodeDefinition {

    final String[] names;

    final Operation operation;

    final Class<?>[] parameters;

    private QueryOperationNodeDefinition(Operation operation, String[] names, Class<?>[] parameters) {
        this.operation = requireNonNull(operation);
        this.names = names;
        this.parameters = parameters;
    }

    public LinkedHashMap<String, Class<?>> getAll() {
        LinkedHashMap<String, Class<?>> m = new LinkedHashMap<>();
        for (int i = 0; i < names.length; i++) {
            m.put(names[i], parameters[i]);
        }
        return m;
    }

    public int getNumberOfParameters() {
        return names.length;
    }

    /**
     * @return the operation
     */
    public Operation getOperation() {
        return operation;
    }

    public static QueryOperationNodeDefinition create(Operation operation) {
        return new QueryOperationNodeDefinition(operation, new String[0], new Class<?>[0]);
    }

    public static QueryOperationNodeDefinition create(Operation operation, String name, Class<?> type) {
        return new QueryOperationNodeDefinition(operation, new String[] { name }, new Class<?>[] { type });
    }

    public static QueryOperationNodeDefinition create(Operation operation, String name1, Class<?> type1, String name2,
            Class<?> type2) {
        return new QueryOperationNodeDefinition(operation, new String[] { name1, name2 },
                new Class<?>[] { type1, type2 });
    }

    public static QueryOperationNodeDefinition create(Operation operation, String name1, Class<?> type1, String name2,
            Class<?> type2, String name3, Class<?> type3) {
        return new QueryOperationNodeDefinition(operation, new String[] { name1, name2, name3 }, new Class<?>[] {
                type1, type2, type3 });
    }
}

//
// public List<Class<?>> parameterTypes() {
// return new ArrayList<>(Arrays.asList(parameters));
// }
//
// public List<String> names() {
// return new ArrayList<>(Arrays.asList(names));
// }
//
// public String getParameter1Name() {
// if (names.length > 0) {
// return names[0];
// }
// throw new IllegalStateException("Only " + names.length + " elements available");
// }
//
// public Class<?> getParameter1Type() {
// if (parameters.length > 0) {
// return parameters[0];
// }
// throw new IllegalStateException("Only " + parameters.length + " elements available");
// }
//
// public String getParameter2Name() {
// if (names.length > 1) {
// return names[1];
// }
// throw new IllegalStateException("Only " + names.length + " elements available");
// }
//
// public Class<?> getParameter2Type() {
// if (parameters.length > 1) {
// return parameters[1];
// }
// throw new IllegalStateException("Only " + parameters.length + " elements available");
// }
//
// public String getParameter3Name() {
// if (names.length > 2) {
// return names[2];
// }
// throw new IllegalStateException("Only " + names.length + " elements available");
// }
//
// public Class<?> getParameter3Type() {
// if (parameters.length > 2) {
// return parameters[2];
// }
// throw new IllegalStateException("Only " + parameters.length + " elements available");
// }
