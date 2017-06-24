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
package io.faststream;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * An interface for creating streams from object arrays. Is normally created by an {@link ArrayFactoryBuilder}.
 *
 * @author Kasper Nielsen
 */
public interface ArrayFactory {

    /**
     * Returns a sequential ordered stream which consist of the specified values.
     *
     * @param <T>
     *            the type of elements in the stream
     * @param values
     *            the elements of the new stream
     * @return the new stream
     * @throws NullPointerException
     *             if the specified array is null
     * @see FastStreams#of(Object...)
     * @see ArrayFactory#of(Object...)
     */
    <T> Stream<T> of(@SuppressWarnings("unchecked") T... values);

    /**
     * An interface for creating streams from double arrays. Is normally created by an {@link ArrayFactoryBuilder}.
     */
    interface OfDouble {
        DoubleStream of(double... array);
    }

    /**
     * An interface for creating streams from int arrays. Is normally created by an {@link ArrayFactoryBuilder}.
     */
    interface OfInt {
        IntStream of(int... array);
    }

    /**
     * An interface for creating streams from long arrays. Is normally created by an {@link ArrayFactoryBuilder}.
     */
    interface OfLong {
        LongStream of(long... array);
    }
}
