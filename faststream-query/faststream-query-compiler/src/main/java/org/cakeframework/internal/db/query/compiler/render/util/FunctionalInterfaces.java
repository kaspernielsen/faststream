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
package org.cakeframework.internal.db.query.compiler.render.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

/**
 * A helper method for finding the method names and their return types of the standard java function interfaces.
 *
 * @author Kasper Nielsen
 */
public final class FunctionalInterfaces {

    /** All functional interfaces defined in the java.util.function package */
    private static Class<?>[] FUNCTION_TYPES = new Class<?>[] { BiConsumer.class, BiFunction.class,
        BinaryOperator.class, BiPredicate.class, BooleanSupplier.class, Consumer.class, DoubleBinaryOperator.class,
        DoubleConsumer.class, DoubleFunction.class, DoublePredicate.class, DoubleSupplier.class,
        DoubleToIntFunction.class, DoubleToLongFunction.class, DoubleUnaryOperator.class, Function.class,
        IntBinaryOperator.class, IntConsumer.class, IntFunction.class, IntPredicate.class, IntSupplier.class,
        IntToDoubleFunction.class, IntToLongFunction.class, IntUnaryOperator.class, LongBinaryOperator.class,
        LongConsumer.class, LongFunction.class, LongPredicate.class, LongSupplier.class,
        LongToDoubleFunction.class, LongToIntFunction.class, LongUnaryOperator.class, ObjDoubleConsumer.class,
        ObjIntConsumer.class, ObjLongConsumer.class, Predicate.class, Supplier.class, ToDoubleBiFunction.class,
        ToDoubleFunction.class, ToIntBiFunction.class, ToIntFunction.class, ToLongBiFunction.class,
        ToLongFunction.class, UnaryOperator.class, Comparator.class,

        Collector.class }; // Collector is not a functional type but some stream methods takes one

    /** The name of the main method of all functional interfaces. */
    private final static IdentityHashMap<Class<?>, String> METHOD_NAME = new IdentityHashMap<>();

    /** The return type of the main method of all functional interfaces. */
    private final static IdentityHashMap<Class<?>, Class<?>> RETURN_TYPES = new IdentityHashMap<>();

    static {
        for (Class<?> c : FUNCTION_TYPES) {
            for (Method m : c.getMethods()) {
                if (Modifier.isAbstract(m.getModifiers())) { // only take the actual abstract method
                    RETURN_TYPES.put(c, m.getReturnType());
                    METHOD_NAME.put(c, m.getName());
                }
            }
        }
    }

    /** Cannot instantiate. */
    private FunctionalInterfaces() {}

    /**
     * Returns the method name of the specified functional interface.
     *
     * @param c
     *            the class to find the method name for
     * @throws IllegalArgumentException
     *             if the specified class is not a standard java.util.function interface
     * @return the method name of the specified functional interface
     */
    public static String methodNameOf(Class<?> c) {
        String name = METHOD_NAME.get(c);
        if (name == null) {
            throw new IllegalArgumentException(c.getCanonicalName());
        }
        return name;
    }

    /**
     * Returns the return type of the specified functional interface.
     *
     * @param c
     *            the class to find the return type of
     * @throws IllegalArgumentException
     *             if the specified class is not a standard java.util.function interface
     * @return the return type of the specified functional interface
     */
    public static Class<?> returnTypeOf(Class<?> c) {
        Class<?> type = RETURN_TYPES.get(c);
        if (type == null) {
            throw new IllegalArgumentException(c.getCanonicalName());
        }
        return type;
    }
}
