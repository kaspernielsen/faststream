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

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 *
 * @author Kasper Nielsen
 */
public class Fff2 {

    static IntStream s() {
        return StreamSupport.intStream(Spliterators.spliterator(new int[] { 12, 34 }, Spliterator.DISTINCT), false);
    }

    public static void main(String[] args) {
        System.out.println(s().spliterator().hasCharacteristics(Spliterator.DISTINCT));
        System.out.println(s().distinct().spliterator().hasCharacteristics(Spliterator.DISTINCT));
        System.out.println(s().boxed().spliterator().hasCharacteristics(Spliterator.DISTINCT));
        System.out.println(s().asDoubleStream().spliterator().hasCharacteristics(Spliterator.DISTINCT));
    }

}
