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
import java.util.function.BiFunction;

/**
 * @param <K>
 *            the type of keys we map
 * @param <V>
 *            the type of values we map
 * @param <T>
 *            the type of element we map to
 * @author Kasper Nielsen
 */
public class DefaultBiFunction<K, V, T> implements BiFunction<K, V, T> {

    @SuppressWarnings("rawtypes")
    static final DefaultBiFunction M = new DefaultBiFunction();

    DefaultBiFunction() {}

    @SuppressWarnings("unchecked")
    public static <K, V, T> BiFunction<K, V, T> mapper() {
        return M;
    }

    public String toString() {
        return "DefaultBinaryMapper.mapper()";
    }

    @Override
    @SuppressWarnings("unchecked")
    public T apply(K a, V o) {
        return (T) map0(a, o);
    }

    public Object map0(Object a, Object b) {
        if (a == null || b == null) {
            return null;
        }
        if (a instanceof Map.Entry) {
            Map.Entry<?, ?> ea = (Entry<?, ?>) a;
            if (b instanceof Map.Entry) {
                Map.Entry<?, ?> eb = (Entry<?, ?>) b;
                return new AbstractMap.SimpleEntry<>(map0(ea.getKey(), eb.getKey()), map0(ea.getValue(), eb.getValue()));
            } else {
                return new AbstractMap.SimpleEntry<>(map0(ea.getKey(), b), map0(ea.getValue(), b));
            }
        } else if (b instanceof Map.Entry) {
            Map.Entry<?, ?> eb = (Entry<?, ?>) b;
            return new AbstractMap.SimpleEntry<>(map0(a, eb.getKey()), map0(a, eb.getValue()));
        }
        Number aa;
        if (a instanceof Number) {
            aa = (Number) a;
        } else {
            aa = new Integer(a.hashCode());
        }
        Number bb;
        if (b instanceof Number) {
            bb = (Number) b;
        } else {
            bb = new Integer(b.hashCode());
        }

        if (a instanceof Byte) {
            return new Byte((byte) (aa.byteValue() + bb.byteValue()));
        } else if (a instanceof Double) {
            return new Double(aa.doubleValue() + bb.doubleValue());
        } else if (a instanceof Float) {
            return new Float(aa.floatValue() + bb.floatValue());
        } else if (a instanceof Integer) {
            return new Integer(aa.intValue() + bb.intValue());
        } else if (a instanceof Long) {
            return new Long(aa.longValue() + bb.longValue());
        } else if (a instanceof Short) {
            return new Short((short) (aa.shortValue() + bb.shortValue()));
        } else if (a instanceof BigInteger) {
            return ((BigInteger) a).add(BigInteger.valueOf(bb.intValue()));
        } else if (a instanceof BigDecimal) {
            return ((BigDecimal) a).add(BigDecimal.valueOf(bb.intValue()));
        } else {
            return new Integer(aa.intValue() + bb.intValue());
        }
    }

}
