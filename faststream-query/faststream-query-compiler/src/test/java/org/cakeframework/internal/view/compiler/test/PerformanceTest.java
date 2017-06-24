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
package org.cakeframework.internal.view.compiler.test;

import org.cakeframework.internal.db.query.compiler.CompiledCollectionViewConfiguration;
import org.cakeframework.internal.db.query.compiler.DataSource;
import org.cakeframework.util.view.CollectionView;

import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenConfiguration;

/**
 *
 * @author Kasper Nielsen
 */
public class PerformanceTest {
    public static void main(String[] args) {
        CodegenConfiguration cc = new CodegenConfiguration();
        cc.addCodeWriter(System.out);
        Codegen c = new Codegen(cc);

        final Data data = new Data();
        // Create a data source

        CompiledCollectionViewConfiguration<Object> conf = new CompiledCollectionViewConfiguration<>();

        DataSource src = conf.addSource("source", data);
        conf.setMain(src.createIndexBoundComposite("aaSmall"));

        conf.setNullable(false);
        CollectionView<Object> v = conf.compile(c).create();

        v.take(1).filter(e -> true).isEmpty();

        v.asStream().mapToInt(e -> e.hashCode()).filter(e -> true).count();

        // List<?> list = new ArrayList<>(Arrays.asList("A", "B", "C", "C"));
        long time = System.nanoTime();
        for (int i = 0; i < 100000000; i++) {
            v.asStream().mapToInt(e -> e.hashCode()).filter(e -> true).count();
            // list.stream().mapToInt(e -> e.hashCode()).filter(e -> true).count();
        }
        // 4710657000
        // 12509784000
        System.out.println(System.nanoTime() - time);
        // BinaryOperator<Object> bo = (k, v) -> k;

        // v.reduce("", (k, l) -> k);
    }
}
