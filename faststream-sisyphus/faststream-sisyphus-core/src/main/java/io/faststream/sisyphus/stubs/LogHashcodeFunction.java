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

import java.util.function.Function;

/**
 * @param <E>
 *            the type of elements we map from
 * @author Kasper Nielsen
 */
public class LogHashcodeFunction<E> implements Function<E, Number> {
    @SuppressWarnings("rawtypes")
    public static final LogHashcodeFunction FUNCTION = new LogHashcodeFunction();

    @Override
    public Number apply(E a) {
        if (a == null) {
            return null;
        }
        long newValue = a.hashCode() == 0 ? 0 : StrictMath.round(StrictMath.log(a.hashCode()));
        if (a instanceof Integer) {
            return new Integer((int) newValue);
        } else if (a instanceof Double) {
            return new Double(newValue);
        } else if (a instanceof Float) {
            return new Float(newValue);
        }
        return new Long(newValue);
    }

    public String toString() {
        return "LogHashcodeMapper.mapper";
    }

    @SuppressWarnings("unchecked")
    public static <V> LogHashcodeFunction<V> instance() {
        return FUNCTION;
    }
}
