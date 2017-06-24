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
package io.faststream.codegen.model.visitor;

import java.util.List;

import io.faststream.codegen.core.CodegenUtil;
import io.faststream.codegen.model.AbstractASTNode;
import io.faststream.codegen.model.PackageDeclaration;
import io.faststream.codegen.model.body.FieldDeclaration;
import io.faststream.codegen.model.body.InitializerDeclaration;
import io.faststream.codegen.model.body.Parameter;
import io.faststream.codegen.model.body.VariableDeclarator;
import io.faststream.codegen.model.expression.ArrayAccessExpression;
import io.faststream.codegen.model.expression.ArrayCreationExpression;
import io.faststream.codegen.model.expression.AssignExpression;
import io.faststream.codegen.model.expression.BinaryExpression;
import io.faststream.codegen.model.expression.CastExpression;
import io.faststream.codegen.model.expression.ClassExpression;
import io.faststream.codegen.model.expression.ConditionalExpression;
import io.faststream.codegen.model.expression.EncapsulatedExpression;
import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.expression.FieldAccessExpression;
import io.faststream.codegen.model.expression.InstanceOfExpression;
import io.faststream.codegen.model.expression.Literal;
import io.faststream.codegen.model.expression.MethodInvocation;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.expression.NewInstanceExpression;
import io.faststream.codegen.model.expression.SuperExpression;
import io.faststream.codegen.model.expression.ThisExpression;
import io.faststream.codegen.model.expression.UnaryExpression;
import io.faststream.codegen.model.expression.VariableDeclarationExpression;
import io.faststream.codegen.model.expression.UnaryExpression.UnOperator;
import io.faststream.codegen.model.statement.AssertStatement;
import io.faststream.codegen.model.statement.BlockStatement;
import io.faststream.codegen.model.statement.BreakStatement;
import io.faststream.codegen.model.statement.CatchClause;
import io.faststream.codegen.model.statement.ContinueStatement;
import io.faststream.codegen.model.statement.DoStatement;
import io.faststream.codegen.model.statement.EmptyStatement;
import io.faststream.codegen.model.statement.ExplicitConstructorInvocationStatement;
import io.faststream.codegen.model.statement.ExpressionStatement;
import io.faststream.codegen.model.statement.ForStatement;
import io.faststream.codegen.model.statement.IfStatement;
import io.faststream.codegen.model.statement.LabeledStatement;
import io.faststream.codegen.model.statement.ReturnStatement;
import io.faststream.codegen.model.statement.Statement;
import io.faststream.codegen.model.statement.SwitchEntryStatement;
import io.faststream.codegen.model.statement.SwitchStatement;
import io.faststream.codegen.model.statement.SynchronizedStatement;
import io.faststream.codegen.model.statement.TextLineStatement;
import io.faststream.codegen.model.statement.ThrowStatement;
import io.faststream.codegen.model.statement.TryStatement;
import io.faststream.codegen.model.statement.WhileStatement;
import io.faststream.codegen.model.type.ClassOrInterfaceType;
import io.faststream.codegen.model.type.PrimitiveType;
import io.faststream.codegen.model.type.ReferenceType;

/**
 * A visitor that creates java code for the specified model.
 *
 * @author Kasper Nielsen
 */
public class JavaRenderer extends CodegenVisitor {

    private int indent;

    StringBuilder sb = new StringBuilder();

    JavaRenderer a(AbstractASTNode n) {
        if (n != null) {
            n.accept(this);
        }
        return this;
    }

    JavaRenderer a(boolean b, AbstractASTNode s) {
        return b ? a(s) : this;
    }

    JavaRenderer a(boolean b, Object s) {
        return b ? a(s) : this;
    }

    JavaRenderer a(boolean b, Object s, Object t) {
        return b ? a(s) : a(t);
    }

    /** {@inheritDoc} */
    @Override
    public void visit(TextLineStatement n) {
        a(n.getComment());
    }

    private JavaRenderer a(List<? extends AbstractASTNode> args, boolean usePar) {
        a(usePar, "(");
        for (int i = 0; i < args.size(); i++) {
            a(i > 0, ", ");
            a(args.get(i));
        }
        a(usePar, ")");
        return this;
    }

    JavaRenderer a(Object s) {
        // System.err.println("ADDDing " + s);
        // new Exception().printStackTrace();
        // new Exception().printStackTrace();
        sb.append(s);
        return this;
    }

    void indent() {
        a(CodegenUtil.indent(indent));
    }

    void newLine() {
        a(CodegenUtil.LS);
    }

    public String toString() {
        return sb.toString();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ArrayAccessExpression n) {
        a(n.getArray());
        for (Expression e : n) {
            a("[").a(e).a("]");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ArrayCreationExpression n) {
        a("new ");
        // array initializer
        n.getType().accept(this);

        if (n.getExpression() != null) {
            for (Expression dim : n.getExpression()) {
                a("[");
                dim.accept(this);
                a("]");
            }
            // for (int i = 0; i < n.getArrayCount(); i++) {
            // printer.print("[]");
            // }
        }
        // else {
        // for (int i = 0; i < n.getArrayCount(); i++) {
        // printer.print("[]");
        // }
        // printer.print(" ");
        // n.getInitializer().accept(this, arg);
        // }
    }

    /** {@inheritDoc} */
    @Override
    public void visit(AssertStatement n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(AssignExpression n) {
        a(n.getLeft()).a(" ").a(n.getOperator()).a(" ").a(n.getRight());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(BinaryExpression n) {
        a(n.getLeft()).a(" ").a(n.getOperator()).a(" ").a(n.getRight());
    }

    public void visit(BlockStatement n) {
        // if (n instanceof QuiteBlockStatement) {
        // for (int i = 0; i < n.getStatements().size(); i++) {
        // n.getStatements().get(i).accept(this);
        // if (i != n.getStatements().size() - 1) {
        // newLine();
        // indent();
        // }
        // }
        // } else {
        a("{");
        if (!n.getStatements().isEmpty()) {
            newLine();
            indent++;
            for (Statement st : n) {
                indent();
                st.accept(this);
                newLine();
            }
            indent--;
            indent();
        }
        a("}");
        // }
    }

    /** {@inheritDoc} */
    @Override
    public void visit(BreakStatement n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(CastExpression n) {
        a("(").a(n.getType()).a(") ").a(n.getExpression());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(CatchClause n) {
        a(" catch (").a(n.getParameter()).a(") ").a(n.getBody());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ClassExpression n) {
        a(n.getType()).a(".class");
    }

    public void visit(ConditionalExpression n) {
        a(n.getCondition()).a(" ? ").a(n.getThenExpression()).a(" : ").a(n.getElseExpression());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ContinueStatement n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(DoStatement n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(EmptyStatement n) {

    }

    /** {@inheritDoc} */
    @Override
    public void visit(EncapsulatedExpression n) {
        a("(").a(n.getInner()).a(")");
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ExplicitConstructorInvocationStatement n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ExpressionStatement n) {
        a(n.getExpression()).a(";");
    }

    /** {@inheritDoc} */
    @Override
    public void visit(FieldAccessExpression n) {
        a(n.getScope()).a(n.getScope() != null, ".").a(n.getFieldName());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(FieldDeclaration n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ForStatement n) {
        a("for (").a(n.getInit(), false);
        a(n.getCompare() == null, ";", "; ").a(n.getCompare());
        a(n.getUpdate().isEmpty(), ";", "; ").a(n.getUpdate(), false).a(") ").a(n.getBody());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(IfStatement n) {
        a("if (").a(n.getCondition()).a(") ").a(n.getThenStatement());
        Statement e = n.getElseStatement();
        while (e instanceof IfStatement) {
            n = (IfStatement) e;
            a(" else if (").a(n.getCondition()).a(") ").a(n.getThenStatement());
            e = n.getElseStatement();
        }
        a(e != null, " else ").a(e != null, e);
    }

    /** {@inheritDoc} */
    @Override
    public void visit(InitializerDeclaration n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(InstanceOfExpression n) {
        a(n.getExpression()).a(" instanceof ").a(n.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(LabeledStatement n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(Literal n) {
        a(n.toString());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(MethodInvocation n) {
        if (n.getScope() != null) {
            a(n.getScope()).a(".");
        }
        a(n.getName());
        a(n.getArguments(), true);
    }

    /** {@inheritDoc} */
    @Override
    public void visit(NameExpression n) {
        a(n.getName());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(NewInstanceExpression n) {
        a("new ").a(n.getType()).a(n.getArguments(), true);
    }

    /** {@inheritDoc} */
    @Override
    public void visit(PackageDeclaration n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(Parameter n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(PrimitiveType n) {
        a(n.toString());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ReferenceType n) {
        n.getType().accept(this);
        for (int i = 0; i < n.getArrayDimensions(); i++) {
            a("[");
        }
        for (int i = 0; i < n.getArrayDimensions(); i++) {
            a("]");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ReturnStatement n) {
        a("return");
        if (n.getExpression() != null) {
            a(" ").a(n.getExpression());
        }
        a(";");
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ClassOrInterfaceType n) {
        if (n.getScope() != null) {
            n.getScope().accept(this);
            a(".");
        }
        a(n.getName());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(SuperExpression n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(SwitchEntryStatement n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(SwitchStatement n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(SynchronizedStatement n) {
        a("synchronized (").a(n.getMutex()).a(") ").a(n.getBlock());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ThisExpression n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(ThrowStatement n) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(TryStatement n) {
        a("try ");
        n.getTryBlock().accept(this);
        a(" finally ");
        n.getFinallyBlock().accept(this);
    }

    /** {@inheritDoc} */
    @Override
    public void visit(UnaryExpression n) {
        boolean post = n.getOperator() == UnOperator.POST_DECREMENT || n.getOperator() == UnOperator.POST_INCREMENT;
        a(!post, n.getOperator()).a(n.getExpression()).a(post, n.getOperator());
    }

    /** {@inheritDoc} */
    @Override
    public void visit(VariableDeclarator n) {
        a(n.getName());
        for (int i = 0; i < n.getDimensions(); i++) {
            a("[]");
        }
        if (n.getInit() != null) {
            a(" = ").a(n.getInit());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void visit(VariableDeclarationExpression n) {
        a(n.getType()).a(" ").a(n.getDeclarators(), false);
    }

    /** {@inheritDoc} */
    @Override
    public void visit(WhileStatement n) {
        a("while (").a(n.getCondition()).a(") ").a(n.getBody());
    }

    public static String renderBlock(BlockStatement bs, int indent) {
        JavaRenderer jr = new JavaRenderer();
        jr.indent = indent;
        List<Statement> statements = bs.getStatements();
        for (int i = 0; i < statements.size(); i++) {
            if (i > 0) {// CodegenBlock.add() has allready indented
                jr.indent();
            }
            statements.get(i).accept(jr);
            if (i != statements.size() - 1) {
                jr.newLine();
            }
        }

        return jr.sb.toString();
    }

    public static String toString(AbstractASTNode n) {
        JavaRenderer jr = new JavaRenderer();
        n.accept(jr);
        return jr.sb.toString();
    }
}
