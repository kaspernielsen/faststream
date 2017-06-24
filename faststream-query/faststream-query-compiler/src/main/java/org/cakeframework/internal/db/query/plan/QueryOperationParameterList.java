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
package org.cakeframework.internal.db.query.plan;

import static java.util.Objects.requireNonNull;
import static org.cakeframework.internal.db.query.compiler.render.util.FunctionalInterfaces.methodNameOf;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cakeframework.internal.db.query.node.QueryOperationNode;
import org.cakeframework.internal.db.query.node.QueryOperationNodeDefinition;
import org.cakeframework.internal.db.query.plan.QueryOperationParameterList.QueryOperationParameter;

import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.MethodInvocation;
import io.faststream.codegen.model.util.Identifier;

/**
 *
 * @author Kasper Nielsen
 */
public final class QueryOperationParameterList implements Iterable<QueryOperationParameter> {

    /** A parameters object indicating no parameters. */
    public static final QueryOperationParameterList EMPTY = new QueryOperationParameterList(
            Collections.<QueryOperationParameter> emptyList());

    private final List<QueryOperationParameter> parameters;

    private QueryOperationParameterList(List<QueryOperationParameter> parameters) {
        this.parameters = requireNonNull(parameters);
    }

    public QueryOperationParameter first() {
        return parameters.get(0);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        for (QueryOperationParameter p : parameters) {
            if (p.getType().equals(type)) {
                // For now we return first parameter, since we do not have
                // need for the second parameter for now
                return (T) p.getOperation();
            }
        }
        throw new IllegalArgumentException(type + ", not found in " + parameters);
    }

    public int getSize() {
        return parameters.size();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<QueryOperationParameter> iterator() {
        return Collections.unmodifiableList(parameters).iterator();
    }

    public QueryOperationParameter second() {
        return parameters.get(1);
    }

    public QueryOperationParameter third() {
        return parameters.get(2);
    }

    @SuppressWarnings("unchecked")
    public static QueryOperationParameterList from(QueryOperationNode view) {
        QueryOperationNodeDefinition o = view.getOperationPackage();
        int s = o.getNumberOfParameters();
        if (s == 0) {
            return EMPTY;
        }
        Object[] oo = o.getAll().entrySet().toArray();

        ArrayList<QueryOperationParameter> result = new ArrayList<>();
        for (int i = 0; i < oo.length; i++) {
            Map.Entry<String, Class<?>> e = (Entry<String, Class<?>>) oo[i];
            result.add(new QueryOperationParameter(view, e.getValue(), e.getKey(), i + 1));
        }
        return new QueryOperationParameterList(result);
    }

    /** A parameter. */
    public static class QueryOperationParameter {

        private final Identifier id;

        private final int index;

        private final String name;

        private final Class<?> type;

        private final QueryOperationNode view;

        QueryOperationParameter(QueryOperationNode view, Class<?> type, String name, int index) {
            this.index = index;
            this.type = requireNonNull(type);
            this.name = requireNonNull(name);
            this.view = requireNonNull(view);
            this.id = new Identifier(type, name);
        }

        /**
         * @return the id
         */
        public Identifier accessor() {
            return id;
        }

        /**
         * Returns the index of the parameter as returned by {@link Method#getParameterTypes()}.
         *
         * @return the index of the parameter
         */
        public final int getIndex() {
            return index;
        }

        /**
         * Returns the name of the parameter type.
         *
         * @return the name of the parameter type
         */
        public final String getName() {
            return name;
        }

        public QueryOperationNode getOperation() {
            return view;
        }

        public MethodInvocation invoke(String name, Expression... parameters) {
            return id.invoke(name, parameters);
        }

        public MethodInvocation invokeFunctional(Expression... parameters) {
            return invoke(methodNameOf(type), parameters);
        }

        /**
         * Returns the type of the parameter.
         *
         * @return the type of the parameter
         */
        public final Class<?> getType() {
            return type;
        }

        @Override
        public String toString() {
            return "ViewParameter [view=" + view + ", index=" + getIndex() + ", name=" + getName() + ", type="
                    + getType() + "]";
        }
    }
}
