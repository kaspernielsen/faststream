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
package io.faststream.codegen.model.util;

import java.util.Arrays;

import io.faststream.codegen.model.body.VariableDeclarator;
import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.expression.VariableDeclarationExpression;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.statement.ForStatement;
import io.faststream.codegen.model.type.Type;

/**
 *
 * @author Kasper Nielsen
 */
public class ForAllI {

    /** The actual for statement. */
    private final ForStatement forStatement = new ForStatement();

    /** An name of the i element */
    private final NameExpression i = new NameExpression("i");

    /** The block that is executed for each element. */
    private final BlockStatement mainBlock = new BlockStatement();

    private ForAllI() {
        forStatement.setBody(mainBlock);
    }

    public BlockStatement getBlock() {
        return mainBlock;
    }

    /**
     * @return the forStatement
     */
    public ForStatement getForStatement() {
        return forStatement;
    }

    public NameExpression getI() {
        return i;
    }

    public static ForAllI create(Expression lowerBound, Expression upperBound) {
        ForAllI i = new ForAllI();
        VariableDeclarator vd = new VariableDeclarator(i.i, 0, lowerBound);
        VariableDeclarationExpression vde = new VariableDeclarationExpression(Type.of(int.class), Arrays.asList(vd));
        i.forStatement.getInit().add(vde);
        i.forStatement.setCompare(i.getI().lessThen(upperBound));
        i.forStatement.getUpdate().add(i.getI().postIncrement());
        return i;
    }

    public static ForAllI createAndAdd(BlockStatement bs, Expression lowerBound, Expression upperBound) {
        ForAllI i = create(lowerBound, upperBound);
        bs.add(i.forStatement);
        return i;
    }
}
