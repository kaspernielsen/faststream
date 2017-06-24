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

import io.faststream.ArrayFactory.OfLong;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.TreeSet;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author Kasper Nielsen
 */
public class TestIt2 {
    public static void ma2in(String[] args) {
        ArrayFactoryBuilder b = new ArrayFactoryBuilder();
        b.addCodeWriter(System.out);
        b.setPackage("foo");
        Stream<Integer> s = b.build().of(1);

        // System.out.println(s.mapToLong(e -> e.hashCode()).filter(e -> true).toArray().length);

        s.distinct().filter(e -> true).map(e -> e).distinct().mapToLong(e -> e.hashCode()).distinct().limit(10).boxed()
                .filter(e -> true).toArray();

        // add varargs

    }

    public static void main(String[] args) {
        ArrayFactoryBuilder b = new ArrayFactoryBuilder();
        b.addCodeWriter(System.out);

        ArrayFactory f = b.build();

        // FastStreams.of(1, 2).distinct().map(e -> e).filter(e -> true).forEach(e -> {});

        // System.out.println(f.of(1, 2, 4, 5).mapToLong(e ->
        // e.hashCode()).distinct().skip(3).boxed().toArray().length);

        // System.out
        // .println(mapToLong(e -> e.hashCode()).skip(2).skip(Long.MAX_VALUE).boxed().toArray().length);

        Stream<Integer> s1 = f.of(1, 2, 3, 4, 5);

        s1.mapToDouble(ed -> 1d).sorted().boxed().limit(123).parallel().reduce((a, fs) -> a);

        Comparator<Integer> com = (a, ba) -> 1;
        // System.out.println(Stream.of(1, 2, 3).sorted(com).spliterator().getComparator());

        System.out.println(Stream.of(1, 2, 3).sorted().spliterator().hasCharacteristics(Spliterator.SORTED));

        System.out.println(Stream.of(1, 2, 3).sorted().peek(e -> {}).spliterator()
                .hasCharacteristics(Spliterator.ORDERED));
        System.out.println(Stream.of(1, 2, 3).sorted().peek(e -> {}).unordered().spliterator()
                .hasCharacteristics(Spliterator.SORTED));

        System.out.println(new TreeSet().spliterator().hasCharacteristics(Spliterator.SORTED));
        System.out.println(StreamSupport.stream(new TreeSet().spliterator(), false).spliterator()
                .hasCharacteristics(Spliterator.SORTED));

        // System.out.println(Stream.of(1, 2, 3).sorted().spliterator().getComparator());

        // System.out.println(Stream.of(1, 2, 3).sorted(com).spliterator().hasCharacteristics(Spliterator.SORTED));

        // Stream<Integer> s2 = Stream.of(1, 2, 3, 4, 5);
        //
        // Iterator<Integer> iter = s1.flatMap(e -> Stream.of(e + 1, e + 2)).iterator();
        //
        // iter.forEachRemaining(e -> System.out.println(e));
        //
        // Iterator<Integer> iter2 = s2.flatMap(e -> Stream.of(e + 1, e + 2)).iterator();
        // iter2.forEachRemaining(e -> System.out.println(e));

        // Spliterator<Integer> s11 = s1.spliterator();
        // Spliterator<Integer> s12 = s11.trySplit();
        //
        // s11.forEachRemaining(e -> System.out.println(e));
        // System.out.println("");
        // s12.forEachRemaining(e -> System.out.println(e));
        // System.out.println("");
        //
        // ArrayList<Integer> al = new ArrayList<>();
        // al.addAll(Arrays.asList(1, 2, 3, 4, 5));
        // Stream<Integer> s2 = al.stream();
        //
        // // s.spliterator();
        //
        // Spliterator<Integer> s21 = s2.spliterator();
        // Spliterator<Integer> s22 = s21.trySplit();
        //
        // s21.forEachRemaining(e -> System.out.println(e));
        // System.out.println("");
        // s22.forEachRemaining(e -> System.out.println(e));

        // System.out.println(s2.estimateSize());
        // System.out.println(s.spliterator().estimateSize());

        // System.out.println(s.mapToDouble(e -> e.hashCode()).sorted().boxed().iterator());

        // s.filter(e -> true).distinct().mapToLong(e -> e.hashCode()).skip(2).boxed().toArray();

        // s.mapToLong(e -> e.hashCode()).peek(e -> {}).anyMatch(e -> true);
        // s.mapToLong(e -> e.hashCode()).sorted().skip(1).filter(e -> true).parallel().skip(2).average();

        // System.out.println(f.of(1, 2).skip(1).toArray().length);

    }

    public static void maind(String[] args) {
        ArrayFactoryBuilder b = new ArrayFactoryBuilder();
        b.addCodeWriter(System.out);
        b.setPackage("foo");
        b.setSourcePath("/Users/kasperni/workspace/GeneratedClasses/src/main/java");
        OfLong l = b.buildOfLong();

        LongStream is = l.of(1L, 2L).filter(e -> e > 0);

        System.out.println(is.count()); // TODO upgrade codegen
        // add varargs

    }
}
