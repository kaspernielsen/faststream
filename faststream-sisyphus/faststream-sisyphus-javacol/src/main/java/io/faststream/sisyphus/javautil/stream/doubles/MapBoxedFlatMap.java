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
package io.faststream.sisyphus.javautil.stream.doubles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;
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
public class MapBoxedFlatMap extends AbstractRandomDoubleStreamTestCase {

    @RndTest
    public void boxed() {
        nested(expected(), actual().boxed());
    }

    @RndTest
    public void map() {
        DoubleUnaryOperator f = l(e -> e + 1, "e -> e + 1");
        nested(expected().map(e -> e + 1), actual().map(f));
    }

    @RndTest
    public void mapToInt() {
        DoubleToIntFunction f = l(e -> (int) e, "e -> (int) e");
        nested(expected().mapToInt(e -> (int) ((double) e)), actual().mapToInt(f));
    }

    @RndTest
    public void mapToLong() {
        DoubleToLongFunction f = l(e -> (long) e, "e -> (long) e");
        nested(expected().mapToLong(e -> (long) ((double) e)), actual().mapToLong(f));
    }

    @RndTest
    public void mapToObj() {
        DoubleFunction<Double> l = l(e -> e + 1d, "e -> e + 1");
        nested(expected().map(e -> e + 1D), actual().mapToObj(l));
    }

    @RndTest
    public void flatMap() {
        HashMap<Double, double[]> map = new HashMap<>();
        for (Double e : expected()) {
            double[] a = new double[Math.abs((int) (random().nextGaussian() * 3))];
            for (int i = 0; i < a.length; i++) {
                a[i] = random().nextDouble();
            }
            map.put(e, a);
        }
        // We have to do this after all values have been mapped (aboce). Because if we encounter identical values they
        // are remapped.
        ArrayList<Double> exp = new ArrayList<>();
        for (Double l : expected()) {
            for (double l1 : map.get(l)) {
                exp.add(l1);
            }
        }
        nested(expected().withList(exp), actual().flatMap(e -> {
            double[] a = map.get(e);
            return a.length == 0 && random().nextBoolean() ? null : DoubleStream.of(a);
        }));
    }
}
