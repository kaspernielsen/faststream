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
package org.cakeframework.internal.db.query.runtime;

import java.util.PrimitiveIterator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 *
 * @author Kasper Nielsen
 */
public class StreamUtil {
    // These all have to do with problems with handling covariant returns types from Janino.
    public static PrimitiveIterator.OfInt fromIntStream(IntStream is) {
        return is.iterator();
    }

    public static PrimitiveIterator.OfLong fromLongStream(LongStream is) {
        return is.iterator();
    }

    public static PrimitiveIterator.OfDouble fromDoubleStream(DoubleStream is) {
        return is.iterator();
    }

    public static PrimitiveIterator.OfDouble fromStream(DoubleStream is) {
        return is.iterator();
    }

    public static int intSkip(long n, int lower, int upper) {
        if (n >= upper - lower) {
            return upper;
        }
        return (int) (lower + n);
    }

    public static int intSkip2(long n, int lower, int upper) {
        int l = n >= upper - lower ? upper : (int) (lower + n);

        if (n >= upper - lower) {
            return upper;
        }
        return (int) (lower + n);
    }
}
