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
package io.faststream.query.view.compiler;

import java.util.List;
import java.util.function.BiFunction;

import org.junit.Ignore;
import org.junit.Test;

import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenConfiguration;
import io.faststream.query.db.query.compiler.CompiledCollectionViewConfiguration;
import io.faststream.query.db.query.compiler.DataSource;
import io.faststream.query.db.query.node.QueryOperationNode;
import io.faststream.query.db.query.node.TerminalQueryOperationNode;
import io.faststream.query.db.query.node.TerminalQueryOperationNodeProcessor;
import io.faststream.query.db.query.node.defaults.AllQueryOperations;
import io.faststream.query.util.view.CollectionView;
import io.faststream.query.view.interpreter.ViewInterpreters;
import io.faststream.sisyphus.view.CollectionViewRandomTestBuilder;

/**
 *
 * @author Kasper Nielsen
 */
public class BatchTest implements AllQueryOperations {

    static NodePresent supportedD = NodePresent.create(C_TRUNCATE_TAKE, CT_FOR_EACH);

    static NodePresent supported = NodePresent.create(

            C_MAP, C_FILTER, C_AS_STREAM, /* C_GROUP_BY, */C_PEEK, C_SORTED, C_DISTINCT, C_MAP_TO_INDEX,

            CT_FOR_EACH, CT_SIZEABLE, CT_MATH_SUM, CT_REDUCE, CT_MINMAX, CT_TO_LIST,

            M_FILTER, M_MAP,

            MT_SIZEABLE, MT_FOR_EACH);

    static NodePresent unsupported = NodePresent.create

            (

                    C_FREQUENCY_COUNT, C_GATHER, C_REVERSE, C_SHUFFLE, C_TRUNCATE, C_UNORDERED, C_PARALLEL, C_SEQUENTIAL,

                    CT_ANYFIRSTLAST, CT_ONE,

                    CT_TO, CT_MATH_ARITHMETIC_MEAN,

    M_ORDERING, M_TRUNCATE_TAKE, MT_TO,

                    U_VIEW_KEYS,

                    U_ORDERING, U_TRUNCATE, U_REDUCE, U_COUNT, U_DISTINCT, U_ANY, U_FIRST, U_LAST, U_ONE, UT_TO);

    static NodePresent supportedx = NodePresent.create(C_MAP, C_FILTER, CT_SIZEABLE, C_AS_STREAM, CT_REDUCE);

    @Test
    @Ignore
    public void test() {
        CollectionViewRandomTestBuilder<Integer> builder = new CollectionViewRandomTestBuilder<Integer>() {
            @Override
            protected CollectionView<Integer> createTestStructure(List<Integer> bootstrap) {
                return create(bootstrap);
            }
        };
        builder.setExecutor(1);
        builder.setInitialSeed(139493159438880L);
        builder.start(1000000);
    }

    public CollectionView<Integer> create(final List<Integer> bootstrap) {

        CodegenConfiguration cc = new CodegenConfiguration();
        cc.setSourcePath("../GeneratedClasses/src/main/java");
        // cc.addCodeWriter(System.out);

        Codegen c = new Codegen(cc);

        final Foo data = new Foo();
        data.objects = bootstrap.toArray();

        BiFunction<TerminalQueryOperationNode, TerminalQueryOperationNodeProcessor, Object> p = new BiFunction<TerminalQueryOperationNode, TerminalQueryOperationNodeProcessor, Object>() {
            @Override
            public Object apply(TerminalQueryOperationNode t, TerminalQueryOperationNodeProcessor u) {
                for (QueryOperationNode o : t.operations()) {
                    if (unsupported.isPresent(o.getOperationPackage().getOperation())) {
                        return ViewInterpreters.createCollectionViewProcessor(bootstrap).process(t);
                    }
                }
                return u.process(t);
            }
        };
        // Create a data source
        CompiledCollectionViewConfiguration<Integer> conf = new CompiledCollectionViewConfiguration<>();
        DataSource src = conf.addSource("source", data);
        conf.setMain(src.createIndexBoundComposite("objects"));

        conf.setPreprocesser(p);
        return conf.compile(c).create();
    }

    public static class Foo {
        public Object[] objects;
    }
}
