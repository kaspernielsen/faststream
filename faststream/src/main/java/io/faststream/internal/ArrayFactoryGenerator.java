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

import io.faststream.ArrayFactoryBuilder;
import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenClass;
import io.faststream.codegen.core.CodegenMethod;
import io.faststream.codegen.model.util.Identifier;
import io.faststream.query.db.query.compiler.anew.CompiledCollectionConfiguration;
import io.faststream.query.db.query.compiler.anew.QueryCompiler;
import io.faststream.query.db.query.compiler.datasource.ArrayOrListComposite;
import io.faststream.query.db.query.node.TerminalQueryOperationNode;
import io.faststream.query.db.query.node.TerminalQueryOperationNodeProcessor;

import java.util.Objects;

/**
 *
 * @author Kasper Nielsen
 */
public class ArrayFactoryGenerator {

    /** {@inheritDoc} */
    private static CodegenClass generateClasses(Codegen codegen, Class<?> factoryType, Class<?> arrayType,
            Class<?> streamType, Class<?> abstractStreamType) {
        CodegenClass cc = codegen.newClass();
        // Generates the array factory
        cc.addImport(factoryType);
        cc.setDefinition("public class DefaultArrayFactory implements ", factoryType);

        cc.addImport(GeneratorUtil.class, TerminalQueryOperationNodeProcessor.class, TerminalQueryOperationNode.class,
                QueryCompiler.class);
        cc.addField("static final ", QueryCompiler.class, " P = (", QueryCompiler.class, ") ", GeneratorUtil.class,
                ".getIt();");

        cc.addImport(streamType, abstractStreamType);
        CodegenMethod m = cc.addMethod("public ", streamType, " of(", arrayType, " values)");
        m.add("ArrayHolder h = new ArrayHolder(values);");
        m.add("return ", abstractStreamType, ".from(h);");

        // Generates the custom Processor
        CodegenClass pr = cc.addInnerClass("public static class Processor ");
        pr.addMethod("public Object process(", arrayType, " array, ", TerminalQueryOperationNode.class, " node)")
        .throwNewUnsupportedOperationException("not implemented");

        // Generate a temporary holder of the array.
        CodegenClass ic = cc.addInnerClass("public static class ArrayHolder implements ",
                TerminalQueryOperationNodeProcessor.class);

        ic.addField("final ", arrayType, " array;");

        m.addImport(Objects.class);
        m = ic.addMethod("ArrayHolder(", arrayType, " array)");
        m.add("Objects.requireNonNull(array, \"array is null\");");
        m.add("this.array = array;");

        m = ic.addMethod("private Processor find(", TerminalQueryOperationNode.class, " node)");
        m.add("return (Processor) P.create(node);");

        m = ic.addMethod("public Object process(", TerminalQueryOperationNode.class, " node)");
        m.add("return find(node).process(array, node);");
        return cc;
    }

    @SuppressWarnings("unchecked")
    public static <T> T newFactory(ArrayFactoryBuilder builder, Class<T> factoryType, Class<?> arrayType,
            Class<?> streamType, Class<?> abstractStreamType) {
        Codegen codegen = GeneratorUtil.newCodegen(builder);
        CodegenClass cc = generateClasses(codegen, factoryType, arrayType, streamType, abstractStreamType);

        Class<?> cl = cc.compile();
        Class<?> proc = GeneratorUtil.getDeclaredClass(cl, e -> e.getName().contains("Processor"));

        CompiledCollectionConfiguration<?> vc = new CompiledCollectionConfiguration<>(codegen, proc);
        Identifier id = new Identifier(arrayType, "arr");
        vc.addParameter(id);
        vc.setMain(ArrayOrListComposite.create(arrayType, id));
        vc.setType(arrayType.getComponentType());
        return (T) GeneratorUtil.instantiate(cl, vc.create());
    }
}
