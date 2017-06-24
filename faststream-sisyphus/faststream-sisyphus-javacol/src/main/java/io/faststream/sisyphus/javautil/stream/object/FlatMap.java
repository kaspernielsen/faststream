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
package io.faststream.sisyphus.javautil.stream.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import io.faststream.sisyphus.annotations.RndTest;

/**
 *
 * @author Kasper Nielsen
 */
public class FlatMap<E> extends AbstractRandomStreamTestCase<E> {
    @RndTest
    public void flatMapToDouble() {
        HashMap<E, double[]> map = new HashMap<>();
        for (E e : expected()) {
            double[] a = new double[Math.abs((int) (random().nextGaussian() * 3))];
            for (int i = 0; i < a.length; i++) {
                a[i] = random().nextDouble();
            }
            map.put(e, a);
        }
        // We have to do this after all values have been mapped (aboce). Because if we encounter identical values they
        // are remapped.
        ArrayList<Double> exp = new ArrayList<>();
        for (E l : expected()) {
            for (double l1 : map.get(l)) {
                exp.add(l1);
            }
        }
        nested(expected().withList(exp), actual().flatMapToDouble(e -> {
            double[] a = map.get(e);
            return a.length == 0 && random().nextBoolean() ? null : DoubleStream.of(a);
        }));
    }

    @RndTest
    public void flatMapToLong() {
        HashMap<E, long[]> map = new HashMap<>();
        for (E e : expected()) {
            long[] a = new long[Math.abs((int) (random().nextGaussian() * 3))];
            for (int i = 0; i < a.length; i++) {
                a[i] = random().nextLong();
            }
            map.put(e, a);
        }
        // We have to do this after all values have been mapped (aboce). Because if we encounter identical values they
        // are remapped.
        ArrayList<Long> exp = new ArrayList<>();
        for (E l : expected()) {
            for (long l1 : map.get(l)) {
                exp.add(l1);
            }
        }
        nested(expected().withList(exp), actual().flatMapToLong(e -> {
            long[] a = map.get(e);
            return a.length == 0 && random().nextBoolean() ? null : LongStream.of(a);
        }));

    }

    @RndTest
    public void flatMapToInt() {
        HashMap<E, int[]> map = new HashMap<>();
        for (E e : expected()) {
            int[] a = new int[Math.abs((int) (random().nextGaussian() * 3))];
            for (int i = 0; i < a.length; i++) {
                a[i] = random().nextInt();
            }
            map.put(e, a);

        }
        // We have to do this after all values have been mapped (aboce). Because if we encounter identical values they
        // are remapped.
        ArrayList<Integer> exp = new ArrayList<>();
        for (E l : expected()) {
            for (int l1 : map.get(l)) {
                exp.add(l1);
            }
        }
        nested(expected().withList(exp), actual().flatMapToInt(e -> {
            int[] a = map.get(e);
            return a.length == 0 && random().nextBoolean() ? null : IntStream.of(a);
        }));
    }
}
