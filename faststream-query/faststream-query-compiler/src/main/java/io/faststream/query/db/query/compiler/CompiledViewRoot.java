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
package io.faststream.query.db.query.compiler;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenClass;
import io.faststream.codegen.core.CodegenMethod;
import io.faststream.query.db.nodes.simplecaching.QueryCacheFactory;
import io.faststream.query.db.query.compiler.render.QueryPlanRender;
import io.faststream.query.db.query.node.AbstractTerminalQueryOperationProcessor;
import io.faststream.query.db.query.node.TerminalQueryOperationNode;
import io.faststream.query.db.query.node.TerminalQueryOperationNodeProcessor;
import io.faststream.query.db.query.plan.QueryEngine;
import io.faststream.query.db.query.plan.QueryPlan;

/**
 * An abstract ViewRoot that caches view executors
 *
 * @author Kasper Nielsen
 */
class CompiledViewRoot extends AbstractTerminalQueryOperationProcessor implements QueryCacheFactory {

    /** The codegen used for compiling view queries. */
    private final Codegen codegen;

    private final ConcurrentHashMap<String, Integer> names = new ConcurrentHashMap<>();

    private final AbstractCompiledEntityConfiguration viewSource;

    private final QueryEngine planner;

    public CompiledViewRoot(QueryEngine planner, Codegen codegen, AbstractCompiledEntityConfiguration viewSource) {
        this.planner = requireNonNull(planner);
        this.codegen = requireNonNull(codegen);
        this.viewSource = requireNonNull(viewSource);
    }

    // /**
    // * Some queries might exists of multiple files. For example, queries that uses the fork join framework.
    // *
    // * @author Kasper Nielsen
    // */
    // public class CompiledQueryUnit {
    // }

    public TerminalQueryOperationNodeProcessor createViewNodeProcessor(QueryPlan plan) {
        // Do not think we want more than say 100 characters
        String name = plan.getTerminalOperation().toFileName();
        Integer count = names.compute(name, (k, v) -> v == null ? 0 : v + 1);
        String className = count > 0 ? name + count : name;

        // TODO right now some generate the same view, for example, order().to og order(Comp).to
        // Det gaar ikke rigtig med classLoading, skal generere unikke klasseNavne
        // className = "xxx" + System.nanoTime();
        // assert !className.endsWith("1");
        Codegen codegen = new Codegen(this.codegen);
        CodegenClass cl = codegen.newClass("public class ", className, " extends ",
                AbstractTerminalQueryOperationProcessor.class);
        cl.addImport(AbstractTerminalQueryOperationProcessor.class).addImport(TerminalQueryOperationNode.class);

        if (!viewSource.getSources().isEmpty()) {
            createConstructor(cl, viewSource.getSources());
        }
        String methodName = "process";
        CodegenMethod method = cl.addMethod("public Object process(", TerminalQueryOperationNode.class, " node)");

        QueryPlanRender.DEFAULT.render(null, plan, method);

        if (!viewSource.getSources().isEmpty()) {
            Class<?> data1 = viewSource.getSources().keySet().iterator().next().getType();
            Object source1 = viewSource.getSources().values().iterator().next();
            return (TerminalQueryOperationNodeProcessor) cl.compileAndInstantiate(data1, source1);
        } else {
            return (TerminalQueryOperationNodeProcessor) cl.compileAndInstantiate();
        }
    }

    public void createConstructor(CodegenClass c, Map<DataSource, Object> sources) {
        DataSource source1 = sources.keySet().iterator().next();
        CodegenMethod m = c.addMethod("public (", source1.getType(), " ", source1.getName(), ")");
        m.add("this.", source1.getName(), " = ", source1.getName(), ";");
        m.getDeclaringClass().addField("private final ", source1.getType(), " ", source1.getName(), ";");
        m.addImport(source1.getType());
    }

    /** {@inheritDoc} */
    @Override
    public Supplier<TerminalQueryOperationNodeProcessor> createCachable(TerminalQueryOperationNode node) {
        return () -> createProcessor(node);
    }

    /** {@inheritDoc} */
    @Override
    public Object process(TerminalQueryOperationNode operation) {
        return createProcessor(operation).process(operation);
    }

    TerminalQueryOperationNodeProcessor createProcessor(TerminalQueryOperationNode operation) {
        return createViewNodeProcessor(planner.createPlan(operation));
    }
}
