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
package io.faststream.sisyphus.stubs;

import java.math.BigInteger;
import java.util.function.BinaryOperator;

/**
 * 
 * @author Kasper Nielsen
 */
public class DefaultBinaryOperator implements BinaryOperator<Object> {

    public static final DefaultBinaryOperator REDUCER = new DefaultBinaryOperator();

    @Override
    public Object apply(Object a, Object b) {
        // More, complex
        // same type, returns same time,
        // different types returns BigInteger
        return val(a).add(val(b));
    }

    static BigInteger val(Object o) {
        if (o instanceof BigInteger) {
            return (BigInteger) o;
        } else if (o instanceof Number) {
            return BigInteger.valueOf(((Number) o).longValue());
        }
        return BigInteger.valueOf(o == null ? 0 : o.hashCode());
    }

    public static long longValue(Object o) {
        if (o instanceof Number) {
            return ((Number) o).longValue();
        }
        return o == null ? 0 : o.hashCode();
    }

    @SuppressWarnings("unchecked")
    public static <E> BinaryOperator<E> reducer() {
        return (BinaryOperator<E>) REDUCER;
    }

    public String toString() {
        return "DefaultReducer.REDUCER";
    }
}
