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

import java.util.NoSuchElementException;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 *
 * @author Kasper Nielsen
 */
public class TestIt {
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

        FastStreams.ofLong(12, 123).filter(e -> true).map(e -> e).sum();

        // FastStreams.of(1, 2).distinct().map(e -> e).filter(e -> true).forEach(e -> {});

        // System.out.println(f.of(1, 2, 4, 5).mapToLong(e ->
        // e.hashCode()).distinct().skip(3).boxed().toArray().length);

        // System.out
        // .println(mapToLong(e -> e.hashCode()).skip(2).skip(Long.MAX_VALUE).boxed().toArray().length);

        // Stream<Integer> s1 = f.of(1, 2, 3, 4, 5);

        // Count.count
        // / flatMapToLong().boxed().unordered()
        // s1.filter(e -> true).flatMap(e -> null).count();
        // s1.filter(e -> true).skip(10).flatMapToDouble(e -> DoubleStream.of(123)).limit(12).boxed().toArray();
        //
        // Filter.filter
        // SkipLimit.skip
        // FlatMap.flatMapToDouble
        // SkipLimit.limit

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

class ResultIterator implements java.util.Iterator {

    int cursor = -1;

    Object[] a;

    int start;

    int end;

    Object next;

    ResultIterator(Object[] a, int start, int end) {
        this.a = a;
        this.start = start;
        this.end = end - 1;
        advance();
    }

    public void advance() {
        while (cursor < end) {
            Object o = a[++cursor];
            next = o;
            return;
        }
        cursor = end + 1;
    }

    public void remove() {
        throw new UnsupportedOperationException("remove is not supported");
    }

    public boolean hasNext() {
        return cursor <= end;
    }

    public Object next() {
        if (cursor > end) {
            throw new NoSuchElementException();
        }
        Object next = this.next;
        advance();
        return next;
    }
}
