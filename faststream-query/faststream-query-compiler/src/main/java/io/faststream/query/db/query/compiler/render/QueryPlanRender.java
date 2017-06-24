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
package io.faststream.query.db.query.compiler.render;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.faststream.codegen.core.CodegenBlock;
import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Expressions;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.expression.VariableDeclarationExpression;
import io.faststream.codegen.model.placeholders.PlaceholderStatement;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.statement.ExpressionStatement;
import io.faststream.codegen.model.visitor.JavaRenderer;
import io.faststream.codegen.model.visitor.Visitors;
import io.faststream.query.db.query.common.nodes.elements.RealNode;
import io.faststream.query.db.query.compiler.datasource.Composite;
import io.faststream.query.db.query.compiler.render.util.CacheStatement;
import io.faststream.query.db.query.compiler.render.util.ReducerSimplifier;
import io.faststream.query.db.query.compiler.render.util.ReturnIteratorOrSpliterator;
import io.faststream.query.db.query.compiler.render.util.TemporaryBlockStatement;
import io.faststream.query.db.query.plan.ModelProcessor;
import io.faststream.query.db.query.plan.QueryNode;
import io.faststream.query.db.query.plan.QueryPlan;

/**
 * A query plan logical analyzer does various analysis of a query. For example, whether a particular element can be
 * null. Or what it is.
 *
 * @author Kasper Nielsen
 */
public final class QueryPlanRender {

    public static final QueryPlanRender DEFAULT = new QueryPlanRender();

    public void render(ViewRender vr, QueryPlan plan, CodegenBlock block) {
        BlockStatement bs = plan.getPq().main;
        plan.getPq().getRoot().setIn(bs);

        // plan.print(n -> "" + n.getOperation());

        writeQueryInformation(plan, bs);

        // Extracts all functions (predicates, consumers, comparators, ...) from the various nodes.
        NodeParameterExtractor.extract(new NameExpression("node"), plan, bs);

        Composite com = plan.getPq().getRoot().sources().getMain();
        Expression main = null;
        Expression replaceWith = null;
        PlaceholderStatement place = new PlaceholderStatement() {};
        Expression ww = null;
        if (!(com.getAccessor() instanceof NameExpression)) {
            NameExpression na = new NameExpression("main");
            // The expression declaring the temporary variable
            VariableDeclarationExpression newVar = Expressions.newVar(na, com.getType(), com.getAccessor());
            bs.add(place);
            main = com.getAccessor();
            replaceWith = na;
            ww = newVar;
        }
        ReturnIteratorOrSpliterator.foo(vr, plan.getPq());

        for (QueryNode n : plan.getPq().getRoot().children()) {
            n.render();
        }

        for (ModelProcessor p : plan.getProcessors()) {
            p.afterBuild(plan);
        }

        CacheStatement.doMagic(plan.getPq());
        if (main != null) {
            Visitors.replace(bs, main, replaceWith);
            place.setStatement(new ExpressionStatement(ww));
        }

        TemporaryBlockStatement.cleanup(plan.getPq()); // must be before renaming, as we remove some blocks
        // Takes all name expressions (identifiers) and make sure they have different names
        Visitors.renameDuplicateNameExpressions(bs);
        ReducerSimplifier.check(plan, bs);

        vr.render();

        // Renders the actual java code, from the model
        block.addImports(plan.imports);
        block.add(JavaRenderer.renderBlock(bs, 2));
    }

    private static void writeQueryInformation(QueryPlan plan, BlockStatement bs) {
        String orig = Stream.of(plan.getTerminalOperation().operations())
                .map(e -> e.getOperationPackage().getOperation().toString()).collect(Collectors.joining("->"));

        String simplified = RealNode.streamAll(plan.getPq()).map(e -> e.getOperation().toString())
                .collect(Collectors.joining("->"));
        if (orig.equals(simplified)) {
            bs.addComment("Query: " + orig);
        } else {
            bs.addComment("Original Query: " + orig);
            bs.addComment("Simplified    : " + simplified);
        }
        bs.addEmptyLine();
    }
}
// NameExpression root = new NameExpression("root");
// ClassOrInterfaceType rNode = new ClassOrInterfaceType("ArrayList", "RootNode");
// VariableDeclarationExpression dec1 = Expressions.newVar(root, rNode, new NameExpression("node").invoke("root")
// .cast(rNode));
//
// // node.root()
// rNode = new ClassOrInterfaceType("DefaultArrayFactory", "ArrayHolder");
// dec1 = Expressions.newVar(root, rNode, new NameExpression("node").invoke("root").cast(rNode));
//
// NameExpression info = new NameExpression("info");
// ClassOrInterfaceType cit = new ClassOrInterfaceType("ArrayList", "ArrayListInfo");
// VariableDeclarationExpression dec2 = Expressions.newVar(info, cit, root.fieldAccess("i"));

// bs.add(dec1);
// bs.add(dec2);

// bs.add("ArrayList.ArrayListInfo info");
