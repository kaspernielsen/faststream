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
package io.faststream.internal;

import static java.util.Objects.requireNonNull;
import io.faststream.ListFactory;
import io.faststream.codegen.core.CodegenClass;
import io.faststream.codegen.core.CodegenMethod;
import io.faststream.query.db.query.compiler.anew.QueryCompiler;
import io.faststream.query.db.query.node.TerminalQueryOperationNode;
import io.faststream.query.db.query.node.TerminalQueryOperationNodeProcessor;

import java.util.Collection;

/**
 *
 * @author Kasper Nielsen
 */
class ArrayListFactoryGeneratorSupport {
    private final CodegenClass c;

    ArrayListFactoryGeneratorSupport(CodegenClass c) {
        this.c = requireNonNull(c);
    }

    void build() {
        info();
        processor();
        factory();
    }

    private void factory() {
        CodegenClass c = this.c.addInnerClass();
        c.addImport(GeneratorUtil.class, TerminalQueryOperationNodeProcessor.class, TerminalQueryOperationNode.class,
                QueryCompiler.class);
        c.addField("static final ", QueryCompiler.class, " P = (", QueryCompiler.class, ") ", GeneratorUtil.class,
                ".getIt();");

        c.addImport(ListFactory.class);
        c.setDefinition("public static class Factory implements ", ListFactory.class);
        c.addMethod("public List newArrayList()").add("return new ArrayList();");
        c.addMethod("public List newArrayList(int size)").add("return new ArrayList(size);");
        c.addMethod("public List newArrayList(Collection c)").add("return new ArrayList(c);");
        c.addImport(Collection.class);
    }

    private void info() {
        CodegenClass c = this.c.addInnerClass();

        c.addImport(TerminalQueryOperationNode.class, TerminalQueryOperationNodeProcessor.class);
        c.setDefinition("public static final class ArrayListInfo implements ",
                TerminalQueryOperationNodeProcessor.class);
        c.addField("private final ArrayList l;");
        c.addMethod("ArrayListInfo(ArrayList l)").add("this.l = l;");

        CodegenMethod m = c.addMethod("private Processor find(", TerminalQueryOperationNode.class, " node)");
        m.add("return (Processor) Factory.P.create(node);");

        m = c.addMethod("public Object process(", TerminalQueryOperationNode.class, " node)");
        m.add("return find(node).process(l.a, l.size, node);");
    }

    private void processor() {
        CodegenClass c = this.c.addInnerClass();
        c.setDefinition("public static class Processor");
        c.addMethod("public Object process(Object[] array, int size, ", TerminalQueryOperationNode.class, " node)")
                .throwNewUnsupportedOperationException("not implemented");
    }
}
