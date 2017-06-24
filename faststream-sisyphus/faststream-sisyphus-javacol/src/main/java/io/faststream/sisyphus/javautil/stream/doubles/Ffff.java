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
public class Ffff {

    public static IntStream s() {
        Spliterator.OfInt spl = Spliterators.spliterator(new int[] { 12, 34 }, Spliterator.NONNULL | Spliterator.DISTINCT);
        IntStream s = StreamSupport.intStream(spl, false);

        return s;
    }

    public static void main(String[] args) {

        print(s().spliterator());

        print(s().distinct().spliterator());
        print(s().distinct().limit(10).spliterator());

        // print(s().mapToObj(e -> e).sorted().spliterator());

        print(s().mapToObj(e -> e).sorted().spliterator());

        print(s().mapToObj(e -> e).sorted().limit(10).spliterator());

        print(s().mapToObj(e -> e).sorted().spliterator());

        print(s().mapToObj(e -> e).sorted().limit(10).spliterator());

        System.out.println("AAA");
        print(s().mapToObj(e -> e).sorted().unordered().spliterator());
        print(s().mapToObj(e -> e).sorted((a, b) -> 1).spliterator());

        print(s().filter(e -> true).spliterator());
        print(s().distinct().filter(e -> true).spliterator());
        print(s().distinct().limit(10).spliterator());

    }

    private static void print(Spliterator<?> s) {
        System.out.println("SIZED " + s.hasCharacteristics(Spliterator.SIZED));
        System.out.println("ORDERED " + s.hasCharacteristics(Spliterator.ORDERED));
        System.out.println("SORTED " + s.hasCharacteristics(Spliterator.SORTED));
        System.out.println("NONNULL " + s.hasCharacteristics(Spliterator.NONNULL));
        System.out.println("DISTINCT " + s.hasCharacteristics(Spliterator.DISTINCT));
        System.out.println();
    }
}
