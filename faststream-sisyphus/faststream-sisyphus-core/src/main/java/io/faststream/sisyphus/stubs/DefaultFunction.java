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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * @param <E>
 *            the type of elements we map from
 * @param <R>
 *            the type of elements we map to
 * @author Kasper Nielsen
 */
public class DefaultFunction<E, R> implements Function<E, R> {
    final int add;

    DefaultFunction(int add) {
        this.add = add;
    }

    @SuppressWarnings("unchecked")
    public R apply(E o) {
        return (R) map0(o);
    }

    public Object map0(Object o) {
        if (o == null) {
            return null;
        }
        Number a;
        if (o instanceof Number) {
            a = (Number) o;
        } else if (o instanceof Map.Entry) {
            Map.Entry<?, ?> e = (Entry<?, ?>) o;
            return new AbstractMap.SimpleImmutableEntry<>(map0(e.getKey()), map0(e.getValue()));
        } else {
            a = new Integer(o.hashCode());
        }
        if (a instanceof Byte) {
            return new Byte((byte) (a.byteValue() + add));
        } else if (a instanceof Double) {
            return new Double(a.doubleValue() + add);
        } else if (a instanceof Float) {
            return new Float(a.floatValue() + add);
        } else if (a instanceof Integer) {
            return new Integer(a.intValue() + add);
        } else if (a instanceof Long) {
            return new Long(a.longValue() + add);
        } else if (a instanceof Short) {
            return new Short((short) (a.shortValue() + add));
        } else if (a instanceof BigInteger) {
            return ((BigInteger) a).add(BigInteger.valueOf(add));
        } else if (a instanceof BigDecimal) {
            return ((BigDecimal) a).add(BigDecimal.valueOf(add));
        } else {
            return new Double(a.doubleValue() + add);
        }
    }

    public String toString() {
        return "DefaultMapper.newInstance(" + add + ")";
    }

    public static <V, T> DefaultFunction<V, T> newInstance(int add) {
        return new DefaultFunction<>(add);
    }
}
