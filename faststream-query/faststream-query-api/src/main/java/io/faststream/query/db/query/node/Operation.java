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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Kasper Nielsen
 */
public class Operation implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private final Operation[] aliases;

    private final String name;

    Operation(String name) {
        this(name, new Operation[0]);
    }

    protected Operation(String name, Operation... aliases) {
        this.name = requireNonNull(name);
        this.aliases = checkArrayForNullsAndCopy("aliases", aliases);
    }

    public final boolean is(Operation other) {
        if (other == this || this.equals(other)) {
            return true;
        } else if (other instanceof AggregatedOperation) {
            AggregatedOperation va = (AggregatedOperation) other;
            for (Operation child : va.children) {
                if (this.is(child)) {
                    return true;
                }
            }
        } else {
            for (Operation ae : aliases) {
                if (ae.is(other)) {
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean isAnyOf(Operation... others) {
        for (Operation other : others) {
            if (is(other)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return name;
    }

    /**
     * Returns an aggregated element that is all the specified types.
     *
     * @param elements
     *            the elements to aggregate
     * @return an aggregated view element
     */
    public static Operation aggregate(Operation... elements) {
        return aggregate("aggregate", elements);
    }

    public static Operation aggregate(String name, Operation... elements) {
        return new AggregatedOperation(name, elements);
    }

    /**
     * Checks whether or not the specified array is {@code null} or contains a {@code null} at any index.
     *
     * @param a
     *            the array to check
     * @return the specified array
     * @throws NullPointerException
     *             if the specified array is null or contains a null at any index
     */
    @SafeVarargs
    static <T> T[] checkArrayForNulls(String parameterName, T... a) {
        requireNonNull(a, parameterName + " is null");
        for (int i = 0; i < a.length; i++) {
            if (a[i] == null) {
                throw new NullPointerException(parameterName + " array contains a null at index " + i);
            }
        }
        return a;
    }

    /**
     * Equivalent to {@link #arrayNotNull(Object[])} except that this method will copy the specified array before
     * checking it. Checks whether or not the specified collection contains a {@code null}.
     *
     * @param a
     *            a copy of the checked array
     * @throws NullPointerException
     *             if the specified collection contains a null
     */
    @SafeVarargs
    static <T> T[] checkArrayForNullsAndCopy(String parameterName, T... a) {
        return checkArrayForNulls(parameterName, Arrays.copyOf(a, a.length));
    }

    public static Operation of(String name, Operation... others) {
        return new Operation(name, others);
    }

    static class AggregatedOperation extends Operation {
        /** serialVersionUID */
        private static final long serialVersionUID = 1L;

        final Operation[] children;

        public AggregatedOperation(String name, Operation[] children) {
            super(name);
            this.children = checkArrayForNullsAndCopy("children", children);
        }

        public List<Operation> getElements() {
            return Collections.unmodifiableList(Arrays.asList(children));
        }
    }

}
