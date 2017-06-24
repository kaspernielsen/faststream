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
package io.faststream.query.db.query.compiler.anew;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenClass;
import io.faststream.codegen.core.CodegenMethod;
import io.faststream.codegen.model.util.Identifier;
import io.faststream.query.db.query.compiler.render.QueryPlanRender;
import io.faststream.query.db.query.compiler.render.ViewRender;
import io.faststream.query.db.query.node.TerminalQueryOperationNode;
import io.faststream.query.db.query.plan.QueryEngine;
import io.faststream.query.db.query.plan.QueryPlan;

/**
 * An abstract ViewRoot that caches view executors
 *
 * @author Kasper Nielsen
 */
class CompiledViewRoot<T> implements QueryCompiler<T> {

    /** The codegen used for compiling view queries. */
    private final Codegen codegen;

    private final ConcurrentHashMap<String, Integer> names = new ConcurrentHashMap<>();

    private final AbstractCompiledEntityConfiguration<T> viewSource;

    private final QueryEngine planner;

    CompiledViewRoot(QueryEngine planner, Codegen codegen, AbstractCompiledEntityConfiguration<T> viewSource) {
        this.planner = requireNonNull(planner);
        this.codegen = requireNonNull(codegen);
        this.viewSource = requireNonNull(viewSource);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public T create(TerminalQueryOperationNode node) {
        // System.out.println(node);
        QueryPlan plan = planner.createPlan(node);
        // Do not think we want more than say 100 characters so we need to trim the string before adding the count
        String name = plan.getTerminalOperation().toFileName();

        // System.out.println(node);
        Integer count = names.compute(name, (k, v) -> v == null ? 0 : v + 1);
        String className = count > 0 ? name + count : name;

        // TODO right now some generate the same view, for example, order().to og order(Comp).to
        // Det gaar ikke rigtig med classLoading, skal generere unikke klasseNavne
        // className = "xxx" + System.nanoTime();
        // assert !className.endsWith("1");

        ViewRender vr = new ViewRender(new Codegen(codegen));
        CodegenClass cl = vr.cc().setDefinition("public class ", className, " extends ", viewSource.type);
        cl.addImport(viewSource.type);

        ArrayList<Object> l = new ArrayList<>();
        l.add("public Object process(");
        for (Identifier i : viewSource.identifiers) {
            cl.addImport(i.getType());
            l.add(i.getType());
            l.add(" ");
            l.add(i.getName());
            l.add(", ");
        }

        l.add(TerminalQueryOperationNode.class);
        cl.addImport(TerminalQueryOperationNode.class);
        l.add(" node)");
        CodegenMethod method = cl.addMethod(l.toArray());

        QueryPlanRender.DEFAULT.render(vr, plan, method);

        return (T) cl.compileAndInstantiate();
    }
}
