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
package io.faststream.sisyphus.util;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

/**
 *
 * @author Kasper Nielsen
 */
public class ArrayUtil {

    public static long[] toLongArray(Collection<?> col) {
        // Collection<?> col = CollectionUtils.toCollection(iterable);
        long[] all = new long[col.size()];
        int i = 0;
        for (Object o : col) {
            if (o == null) {
                throw new NullPointerException("The specified iterable returned a null at index " + i);
            } else if (!(o instanceof Long)) {
                throw new ClassCastException(cannotCast(o, Long.class, i));
            }
            all[i++] = ((Long) o).longValue();
        }
        return all;
    }

    public static long[] toLongArray(Object... parameters) {
        requireNonNull(parameters, "parameters is null");
        long[] all = new long[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            if (parameter == null) {
                throw new NullPointerException("The specified array contains a null at index " + i);
            } else if (!(parameter instanceof Long)) {
                throw new ClassCastException(cannotCast(parameter, Long.class, i));
            }
            all[i] = ((Long) parameter).longValue();
        }
        return all;
    }

    public static int[] toIntegerArray(Collection<?> col) {
        // Collection<?> col = CollectionUtils.toCollection(iterable);
        int[] all = new int[col.size()];
        int i = 0;
        for (Object o : col) {
            if (o == null) {
                throw new NullPointerException("The specified iterable returned a null at index " + i);
            } else if (!(o instanceof Integer)) {
                throw new ClassCastException(cannotCast(o, Integer.class, i));
            }
            all[i++] = ((Integer) o).intValue();
        }
        return all;
    }

    public static double[] toDoubleArray(Object... parameters) {
        requireNonNull(parameters, "parameters is null");
        double[] all = new double[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            if (parameter == null) {
                throw new NullPointerException("The specified array contains a null at index " + i);
            } else if (!(parameter instanceof Double)) {
                throw new ClassCastException(cannotCast(parameter, Double.class, i));
            }
            all[i] = ((Double) parameter).doubleValue();
        }
        return all;
    }

    public static int[] toIntegerArray(Object... parameters) {
        requireNonNull(parameters, "parameters is null");
        int[] all = new int[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            if (parameter == null) {
                throw new NullPointerException("The specified array contains a null at index " + i);
            } else if (!(parameter instanceof Integer)) {
                throw new ClassCastException(cannotCast(parameter, Integer.class, i));
            }
            all[i] = ((Integer) parameter).intValue();
        }
        return all;
    }

    static String cannotCast(Object o, Class<?> to, int index) {
        return "Cannot cast " + o.getClass().getName() + " to " + to.getName() + (index >= 0 ? ", at index " + index : "");
    }
}
