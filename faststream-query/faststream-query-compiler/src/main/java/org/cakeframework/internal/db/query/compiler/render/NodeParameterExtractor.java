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
package org.cakeframework.internal.db.query.compiler.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cakeframework.internal.db.query.common.nodes.elements.IterateContinue;
import org.cakeframework.internal.db.query.node.QueryOperationNode;
import org.cakeframework.internal.db.query.node.TerminalQueryOperationNode;
import org.cakeframework.internal.db.query.plan.QueryNode;
import org.cakeframework.internal.db.query.plan.QueryOperationParameterList.QueryOperationParameter;
import org.cakeframework.internal.db.query.plan.QueryPlan;
import org.cakeframework.internal.db.query.util.tree.WalkOrder;

import io.faststream.codegen.core.CodegenUtil;
import io.faststream.codegen.model.body.VariableDeclarator;
import io.faststream.codegen.model.expression.EncapsulatedExpression;
import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.expression.VariableDeclarationExpression;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.type.Type;

/**
 * Generates code to extract the various functions and parameters for each {@link QueryNode}.
 *
 * @author Kasper Nielsen
 */
class NodeParameterExtractor {

    /**
     * Generates the code that extracts all parameters from {@link TerminalQueryOperationNode}.
     *
     * @param plan
     *            the query plan
     * @param importset
     *            a set of imports
     */
    static void extract(Expression node, QueryPlan plan, BlockStatement bs) {
        // Since we sometimes optimize some statements away we need to find all query operations
        // that are still in the query and which provides one or more parameters
        Map<QueryOperationNode, List<QueryOperationParameter>> m = new HashMap<>();

        for (QueryNode n : plan.getPq().depthFirstTraversal()) {
            for (QueryOperationParameter p : n.parameters()) {
                // stupid shit, when we reduce node we copy them and their parameters
                if (n.findFirst(WalkOrder.PREVIOUS_OR_PARENT, e -> e instanceof IterateContinue) == null) {
                    m.computeIfAbsent(p.getOperation(), e -> new ArrayList<>()).add(p);
                }
            }
        }
        // Extract the actual parameters if there are any
        if (!m.isEmpty()) {
            extract(node, plan, bs, m);
        }
    }

    private static void extract(Expression node, QueryPlan plan, BlockStatement bs,
            Map<QueryOperationNode, List<QueryOperationParameter>> m) {
        Expression accessor = node; // The terminal node

        QueryOperationNode[] operations = plan.getTerminalOperation().operations();

        // need to run through all the nodes in the definition order
        bs.addComment("Extract all functions from a linked list of query objects");
        for (int i = operations.length - 1; i >= 0; i--) {
            QueryOperationNode operationNode = operations[i];
            List<QueryOperationParameter> c = m.remove(operationNode);
            if (c != null) { // test if want to retain the parameter

                for (QueryOperationParameter parameter : c) {

                    plan.imports.add(operationNode.getClass());
                    plan.imports.add(parameter.getType());

                    // Create code that extracts the previous QueryOperation
                    String method = "get" + CodegenUtil.capitalizeFirstLetter(parameter.getName());

                    Expression e;
                    if (m.size() > 0 && i != operations.length - 1) {
                        NameExpression ne = new NameExpression(new String("on"));
                        e = define(bs, Type.of(operationNode.getClass()), ne, accessor.cast(operationNode.getClass()));
                        accessor = ne;
                    } else {
                        e = new EncapsulatedExpression(accessor.cast(operationNode.getClass()));
                    }
                    define(bs, Type.of(parameter.getType()), parameter.accessor(), e.invoke(method));
                }
                bs.addEmptyLine();
            }
            accessor = accessor.invoke("previous");
        }
        // bs.addEmptyLine();
    }

    private static NameExpression define(BlockStatement bs, Type type, NameExpression name, Expression e) {
        VariableDeclarator vd = new VariableDeclarator(name, 0, e);
        bs.add(new VariableDeclarationExpression(type, Arrays.asList(vd)));
        return name;
    }
}
