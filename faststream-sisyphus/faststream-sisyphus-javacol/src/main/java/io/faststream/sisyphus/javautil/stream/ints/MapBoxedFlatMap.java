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
package io.faststream.sisyphus.javautil.stream.ints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test {@link IntStream#asDoubleStream()()}, {@link IntStream#boxed()},
 * {@link IntStream#map(java.util.function.IntUnaryOperator)},
 * {@link IntStream#mapToDouble(java.util.function.IntToDoubleFunction)},
 * {@link IntStream#mapToInt(java.util.function.IntToIntFunction)},
 * {@link IntStream#mapToObj(java.util.function.IntFunction)}.
 * 
 * @author Kasper Nielsen
 */
public class MapBoxedFlatMap extends AbstractRandomIntStreamTestCase {

    @RndTest
    public void boxed() {
        nested(expected(), actual().boxed());
    }

    @RndTest
    public void asDoubleStream() {
        nested(expected().map(e -> e.doubleValue()), actual().asDoubleStream());
    }

    @RndTest
    public void asLongStream() {
        nested(expected().map(e -> e.longValue()), actual().asLongStream());
    }

    @RndTest
    public void map() {
        IntUnaryOperator f = l(e -> e + 1, "e -> e + 1");
        nested(expected().mapToInt(e -> e + 1), actual().map(f));
    }

    @RndTest
    public void mapToDouble() {
        IntToDoubleFunction f = l(e -> e, "e -> e");
        nested(expected().mapToDouble(e -> e.doubleValue()), actual().mapToDouble(f));
    }

    @RndTest
    public void mapToLong() {
        IntToLongFunction f = l(e -> e, "e -> (int) e");
        nested(expected().mapToLong(e -> e.longValue()), actual().mapToLong(f));
    }

    @RndTest
    public void mapToObj() {
        IntFunction<Integer> l = l(e -> e + 1, "e -> e + 1");
        nested(expected().map(e -> e + 1), actual().mapToObj(l));
    }

    @RndTest
    public void flatMap() {
        HashMap<Integer, int[]> map = new HashMap<>();
        for (Integer e : expected()) {
            int[] a = new int[Math.abs((int) (random().nextGaussian() * 3))];
            for (int i = 0; i < a.length; i++) {
                a[i] = random().nextInt();
            }
            map.put(e, a);
        }
        // We have to do this after all values have been mapped (aboce). Because if we encounter identical values they
        // are remapped.
        ArrayList<Integer> exp = new ArrayList<>();
        for (Integer l : expected()) {
            for (int l1 : map.get(l)) {
                exp.add(l1);
            }
        }
        nested(expected().withList(exp), actual().flatMap(e -> {
            int[] a = map.get(e);
            return a.length == 0 && random().nextBoolean() ? null : IntStream.of(a);
        }));
    }
}
