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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.stream.Collector;

import io.faststream.sisyphus.javautil.stream.object.CollectorHelper.ResultContainer;

/**
 *
 * @author Kasper Nielsen
 */
public class CollectorHelper<E> implements Collector<E, ResultContainer<E>, ResultContainer<E>> {

    final Set<Collector.Characteristics> characteristics;

    public CollectorHelper(Characteristics... c) {
        characteristics = new HashSet<>(Arrays.asList(c));
    }

    /** {@inheritDoc} */
    @Override
    public BiConsumer<ResultContainer<E>, E> accumulator() {
        return (a, b) -> a.result.add(b);
    }

    @SuppressWarnings("unchecked")
    public ObjIntConsumer<ResultContainer<E>> accumulatorInt() {
        return (a, b) -> ((List<Integer>) a.result).add(b);
    }

    @SuppressWarnings("unchecked")
    public ObjDoubleConsumer<ResultContainer<E>> accumulatorDouble() {
        return (a, b) -> ((List<Double>) a.result).add(b);
    }

    @SuppressWarnings("unchecked")
    public ObjLongConsumer<ResultContainer<E>> accumulatorLong() {
        return (a, b) -> ((List<Long>) a.result).add(b);
    }

    /** {@inheritDoc} */
    @Override
    public Set<Collector.Characteristics> characteristics() {
        return characteristics;
    }

    /** {@inheritDoc} */
    @Override
    public BinaryOperator<ResultContainer<E>> combiner() {
        return (a, b) -> {
            a.result.addAll(b.result);
            return a;
        };
    }

    public BiConsumer<ResultContainer<E>, ResultContainer<E>> combinerConsumer() {
        return (a, b) -> {
            a.result.addAll(b.result);
        };
    }

    /** {@inheritDoc} */
    @Override
    public Function<ResultContainer<E>, ResultContainer<E>> finisher() {
        if (characteristics.contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return (a) -> {
                throw new AssertionError();
            };
        }
        return (a) -> {
            a.isFinshed = true;
            return a;
        };
    }

    /** {@inheritDoc} */
    @Override
    public Supplier<ResultContainer<E>> supplier() {
        return () -> new ResultContainer<>(this);
    }

    public static class ResultContainer<E> {
        final CollectorHelper<E> h;
        public final ArrayList<E> result = new ArrayList<>();
        boolean isFinshed;

        ResultContainer(CollectorHelper<E> h) {
            this.h = h;
        }
    }
}
