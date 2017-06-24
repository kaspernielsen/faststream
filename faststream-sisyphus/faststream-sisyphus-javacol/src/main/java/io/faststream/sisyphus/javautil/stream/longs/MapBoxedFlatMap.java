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
package io.faststream.sisyphus.javautil.stream.longs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.LongFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;

import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test {@link LongStream#asDoubleStream()()}, {@link LongStream#boxed()},
 * {@link LongStream#map(java.util.function.LongUnaryOperator)},
 * {@link LongStream#mapToDouble(java.util.function.LongToDoubleFunction)},
 * {@link LongStream#mapToInt(java.util.function.LongToIntFunction)},
 * {@link LongStream#mapToObj(java.util.function.LongFunction)}.
 * 
 * @author Kasper Nielsen
 */
public class MapBoxedFlatMap extends AbstractRandomLongStreamTestCase {

    @RndTest
    public void boxed() {
        nested(expected(), actual().boxed());
    }

    @RndTest
    public void asDoubleStream() {
        nested(expected().map(e -> e.doubleValue()), actual().asDoubleStream());
    }

    @RndTest
    public void map() {
        LongUnaryOperator f = l(e -> e + 1, "e -> e + 1");
        nested(expected().mapToLong(e -> e + 1), actual().map(f));
    }

    @RndTest
    public void mapToDouble() {
        LongToDoubleFunction f = l(e -> e, "e -> e");
        nested(expected().mapToDouble(e -> e.doubleValue()), actual().mapToDouble(f));
    }

    @RndTest
    public void mapToInt() {
        LongToIntFunction f = l(e -> (int) e, "e -> (int) e");
        nested(expected().mapToInt(e -> e.intValue()), actual().mapToInt(f));
    }

    @RndTest
    public void mapToObj() {
        LongFunction<Long> l = l(e -> e + 1L, "e -> e + 1L");
        nested(expected().map(e -> e + 1L), actual().mapToObj(l));
    }

    @RndTest
    public void flatMap() {
        HashMap<Long, long[]> map = new HashMap<>();
        for (Long e : expected()) {
            long[] a = new long[Math.abs((int) (random().nextGaussian() * 3))];
            for (int i = 0; i < a.length; i++) {
                a[i] = random().nextLong();
            }
            map.put(e, a);
        }
        // We have to do this after all values have been mapped (aboce). Because if we encounter identical values they
        // are remapped.
        ArrayList<Long> exp = new ArrayList<>();
        for (Long l : expected()) {
            for (long l1 : map.get(l)) {
                exp.add(l1);
            }
        }
        nested(expected().withList(exp), actual().flatMap(e -> {
            long[] a = map.get(e);
            return a.length == 0 && random().nextBoolean() ? null : LongStream.of(a);
        }));
    }
}
