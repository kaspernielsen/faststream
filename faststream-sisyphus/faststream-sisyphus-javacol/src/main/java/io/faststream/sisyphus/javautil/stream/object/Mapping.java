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

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.DefaultFunction;

/**
 * Test the {@link Stream#allMatch(Predicate)} method.
 * 
 * <p>
 * This class is called MapElement and not Map to avoid configurations with the Java's build in {@link Map}.
 * 
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class Mapping<E> extends AbstractRandomStreamTestCase<E> {

    @RndTest
    public void map() {
        Function<E, Object> mapper = DefaultFunction.newInstance(Math.round(random().nextGaussian(0, 5)));
        nested(expected().map(mapper), actual().map(mapper));
    }

    @RndTest
    public void mapToDouble() {
        ToDoubleFunction<E> mapper = new ToDoubleFunction<E>() {
            public double applyAsDouble(E value) {
                return value.hashCode();
            }
        };
        nested(expected().mapToDouble(mapper), actual().mapToDouble(mapper));
    }

    @RndTest
    public void mapToInt() {
        ToIntFunction<E> mapper = new ToIntFunction<E>() {
            public int applyAsInt(E value) {
                return value.hashCode();
            }
        };
        nested(expected().mapToInt(mapper), actual().mapToInt(mapper));
    }

    @RndTest
    public void mapToLong() {
        ToLongFunction<E> mapper = l(e -> e.hashCode(), "e -> e.hashCode()");
        nested(expected().mapToLong(mapper), actual().mapToLong(mapper));
    }

}
