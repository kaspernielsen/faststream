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
package io.faststream.query.view.compiler.test;

import java.util.function.BinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenConfiguration;
import io.faststream.query.db.query.compiler.CompiledCollectionViewConfiguration;
import io.faststream.query.db.query.compiler.DataSource;
import io.faststream.query.util.view.CollectionView;

/**
 *
 * @author Kasper Nielsen
 */
public class WithArray3 {

    public static void main(String[] args) {
        CodegenConfiguration cc = new CodegenConfiguration();
        cc.addCodeWriter(System.out);
        Codegen c = new Codegen(cc);

        final Data data = new Data();
        // Create a data source

        CompiledCollectionViewConfiguration<Object> conf = new CompiledCollectionViewConfiguration<>();
        conf.setCachingDisabled(true);
        conf.setNullable(false);
        conf.setType(Object.class);
        DataSource src = conf.addSource("source", data);
        conf.setMain(src.createIndexBoundComposite("aaSmall"));

        conf.setNullable(true);
        CollectionView<Object> v = conf.compile(c).create();

        // v.mapToIndex().values().size();
        // v.map(e -> e).mapToIndex().entries().apply(e -> {});

        // Original Query:
        // C_FILTER_NULLS->C_DISTINCT->C_FILTER->C_AS_OBJECT_STREAM->C_DISTINCT->C_MAP_TO_INT->C_DISTINCT->CT_FOR_EACH
        // Simplified : C_FILTER_NULLS->C_DISTINCT->C_FILTER->C_DISTINCT->C_MAP_TO_INT->C_DISTINCT->CT_FOR_EACH

        // v.groupBy(e -> e).map((a, b) -> v).filter(e -> true).size();

        // v.mapToIndex().filter((a, b) -> true).mapKey(e -> e).keys().toList();

        Stream<Object> s = v.asStream();
        BinaryOperator<Object> o = (a, b) -> a;

        // System.out.println(s.flatMap(e -> Stream.of(e, e)).mapToInt(e -> e.hashCode()).toArray());

        // System.out.println(s.flatMap(e -> Stream.of(e, e)).toArray());

        // System.out.println(s.mapToInt(e -> e.hashCode()).flatMap(e -> IntStream.of(e, e)).toArray());

        System.out.println(s.flatMap(e -> Stream.of(e, e)).mapToInt(e -> e.hashCode()).flatMap(e -> IntStream.of(e, e))
                .toArray().length);

        // reduce(U, BiFunction<U, ? super T, U>, BinaryOperator<U>)
        // toArray(IntFunction<A[]>)
    }
}
