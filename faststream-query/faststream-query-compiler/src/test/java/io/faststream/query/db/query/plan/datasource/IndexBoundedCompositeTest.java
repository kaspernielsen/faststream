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
package io.faststream.query.db.query.plan.datasource;

/**
 *
 * @author Kasper Nielsen
 */
public class IndexBoundedCompositeTest {

    // public static void main(String[] args) {
    // IndexBoundedComposite c1 = IndexBoundedComposite.create(Object[].class, new NameExpression("e"));
    // IndexBoundedComposite c2 = IndexBoundedComposite.create(Object[].class, new NameExpression("e"));
    // BlockStatement bs = new BlockStatement();
    // c1.forAll(bs);
    //
    // c2.forAll(bs);
    //
    // new NamePlanner().visit(bs);
    //
    //
    // ForStatement fs = (ForStatement) bs.getStatements().get(0);
    //
    // for (Expression e : fs.getInit()) {
    // VariableDeclarationExpression v = (VariableDeclarationExpression) e;
    // for (VariableDeclarator d : v) {
    // System.err.println(d.getName().getClass() + " " + System.identityHashCode(d.getName()));
    // }
    // }
    //
    // for (Expression e : fs.getUpdate()) {
    // UnaryExpression ue = (UnaryExpression) e;
    // System.err.println(ue.getExpression().getClass() + " " + System.identityHashCode(ue.getExpression()));
    // }
    //
    // BinaryExpression ue = (BinaryExpression) fs.getCompare();
    // System.err.println(ue.getLeft().getClass() + " " + System.identityHashCode(ue.getLeft()));

    // }
}
