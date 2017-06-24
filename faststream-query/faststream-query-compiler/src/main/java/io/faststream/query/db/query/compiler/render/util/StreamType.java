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
package io.faststream.query.db.query.compiler.render.util;

import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/**
 *
 * @author Kasper Nielsen
 */
public enum StreamType {
    OBJECT, LONG, INT, DOUBLE;

    public Class<?> getIteratorType() {
        if (this == OBJECT) {
            return Iterator.class;
        } else if (this == LONG) {
            return PrimitiveIterator.OfLong.class;
        } else if (this == INT) {
            return PrimitiveIterator.OfInt.class;
        }
        return PrimitiveIterator.OfDouble.class;
    }

    public Class<?> getSpliteratorType() {
        if (this == OBJECT) {
            return Spliterator.class;
        } else if (this == LONG) {
            return Spliterator.OfLong.class;
        } else if (this == INT) {
            return Spliterator.OfInt.class;
        }
        return Spliterator.OfDouble.class;
    }

    public boolean isPrimitive() {
        return this != OBJECT;
    }

    public Class<?> getConsumerType() {
        if (this == OBJECT) {
            return Consumer.class;
        } else if (this == LONG) {
            return LongConsumer.class;
        } else if (this == INT) {
            return IntConsumer.class;
        }
        return DoubleConsumer.class;
    }

    public static StreamType from(Class<?> type) {
        if (type == int.class) {
            return INT;
        } else if (type == long.class) {
            return LONG;
        } else if (type == double.class) {
            return DOUBLE;
        }
        return OBJECT;
    }

    public String prepend() {
        if (this == DOUBLE) {
            return "Double";
        } else if (this == LONG) {
            return "Long";
        } else if (this == INT) {
            return "Int";
        }
        return "";
    }

    public Class<?> getType() {
        if (this == OBJECT) {
            return Object.class;
        } else if (this == LONG) {
            return long.class;
        } else if (this == INT) {
            return int.class;
        }
        return double.class;
    }
}
