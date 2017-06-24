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
package io.faststream.query.db.query.common.nodes.elements;

import static io.faststream.codegen.model.expression.Expressions.literal;
import static io.faststream.codegen.model.expression.Expressions.newVar;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;

import io.faststream.codegen.model.body.VariableDeclarator;
import io.faststream.codegen.model.expression.ArrayAccessExpression;
import io.faststream.codegen.model.expression.ArrayCreationExpression;
import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.Literal;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.expression.VariableDeclarationExpression;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.statement.ExpressionStatement;
import io.faststream.codegen.model.statement.Statement;
import io.faststream.codegen.model.type.ReferenceType;
import io.faststream.codegen.model.type.Type;
import io.faststream.query.db.query.compiler.datasource.ArrayOrListComposite;
import io.faststream.query.db.query.compiler.datasource.StreamingComposite;
import io.faststream.query.db.query.compiler.render.util.AbstractOperationNode;
import io.faststream.query.db.query.compiler.render.util.IntroduceArrays;
import io.faststream.query.db.query.node.defaults.AllQueryOperations;
import io.faststream.query.db.query.util.tree.WalkOrder;

/**
 *
 * @author Kasper Nielsen
 */
public class CopyToArray extends AbstractOperationNode implements AllQueryOperations {

    public IntroduceArrays.Array ar;

    public boolean reused;

    public boolean sizeConstant() {
        return find(WalkOrder.PREVIOUS_OR_PARENT).pathBetween(n -> n instanceof ForAll).allMatch(
                n -> n.is(CA_SIZE_CONSTANT));
    }

    public boolean sizeIncreasing() {
        return find(WalkOrder.PREVIOUS_OR_PARENT).pathBetween(n -> n instanceof ForAll).anyMatch(n -> n.is(C_FLAT_MAP));
    }

    public boolean sizeNotIncreasing() {
        return find(WalkOrder.PREVIOUS_OR_PARENT).pathBetween(n -> n instanceof ForAll).allMatch(
                n -> n.is(CA_SIZE_NOT_INCREASING));
    }

    // Maaske kan vi lave den mere general.
    // bare koere alle statements igennem
    public Expression stealIfVariableDeclaration(BlockStatement bs, Expression ne) {
        Statement last = bs.last();
        if (last != null && last instanceof ExpressionStatement) {
            ExpressionStatement s = (ExpressionStatement) last;
            if (s.getExpression() instanceof VariableDeclarationExpression) {
                VariableDeclarationExpression vde = (VariableDeclarationExpression) s.getExpression();
                if (vde.getDeclarators().size() == 1 && vde.getDeclarators().get(0).getName() == ne) {
                    bs.remove(s);
                    return vde.getDeclarators().get(0).getInit();
                }
            }
        }
        return ne;
    }

    @Override
    public void buildModel() {
        StreamingComposite sc = (StreamingComposite) sources().getMain();

        boolean sizeConstant = sizeConstant();
        ForAll forAll = findFirst(WalkOrder.PREVIOUS_OR_PARENT, ForAll.class);

        ArrayOrListComposite previous = (ArrayOrListComposite) forAll.sources().getMain();
        requireNonNull(previous);
        ArrayOrListComposite composite = previous;

        // getTree().print(e -> e.toString());
        // group().addComment(" " + ar.getComponentType());

        if (!reused) {
            Expression nie = new ArrayCreationExpression(Type.of(ar.getComponentType()),
                    Arrays.asList(sizeConstant ? previous.getUpperBound().minus(previous.getLowerBound()).simplify()
                            : literal(2)));
            group().bAddFirst(
                    new VariableDeclarationExpression(ReferenceType.createArray(ar.getComponentType()), Arrays
                            .asList(new VariableDeclarator(ar.getArray(), 0, nie))));
            composite = ArrayOrListComposite.create(ar.getType(), ar.getArray());
            if (!sizeConstant) {
                group().bAddFirst(newVar(ar.getArraySize(), int.class, new Literal(2)).statement());
            } else {
                ar.sizeConstant = true;
            }
        } else {
            composite = ArrayOrListComposite.create(ar.getType(), ar.getArray()).withUpperBounds(
                    previous.getUpperBound());
        }
        Expression aIndex = forAll.getI().getI();

        if (!sizeConstant) {
            NameExpression upper = new NameExpression("upper");
            group().bAddFirst(newVar(upper, int.class, new Literal(0)).statement());

            if (!reused || sizeIncreasing()) {
                ar.enlargeArrayIfNeeded(this, b(), upper);
            }
            aIndex = upper.postIncrement();
            composite = composite.withBounds(new Literal(0), upper);
            b().add(new ArrayAccessExpression(ar.getArray(), Arrays.asList(aIndex)).assign(stealIfVariableDeclaration(
                    b(), sc.getAccessor())));

        } else {
            b().add(new ArrayAccessExpression(ar.getArray(), Arrays.asList(aIndex.minus(previous.getLowerBound())
                    .simplify())).assign(stealIfVariableDeclaration(b(), sc.getAccessor())));
            composite = composite.withUpperBounds(previous.getUpperBound().minus(previous.getLowerBound()));
        }

        group().next().sources().setMain(composite);
    }
}
