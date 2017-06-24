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

import java.util.function.Predicate;
import java.util.stream.DoubleStream;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.HashcodeModPredicate;

/**
 * Test the {@link DoubleStream#filter(Predicate)} method.
 * 
 * @param <E>
 *            the type of elements in the stream
 * 
 * @author Kasper Nielsen
 */
public class Filter extends AbstractRandomDoubleStreamTestCase {

    @RndTest
    public void filter() {
        HashcodeModPredicate<Double> p = new HashcodeModPredicate<>(random().nextInt(1, 12));
        nested(expected().filter(p), actual().filter(p));
    }
}
