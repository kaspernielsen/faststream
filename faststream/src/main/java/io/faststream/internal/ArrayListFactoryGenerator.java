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

import io.faststream.ArrayListFactory;
import io.faststream.ArrayListFactoryBuilder;
import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenClass;
import io.faststream.codegen.core.CodegenMethod;
import io.faststream.codegen.model.util.Identifier;
import io.faststream.query.db.nodes.stream.stream.AbstractStream;
import io.faststream.query.db.query.compiler.anew.CompiledCollectionConfiguration;
import io.faststream.query.db.query.compiler.datasource.ArrayOrListComposite;

import java.util.List;
import java.util.RandomAccess;
import java.util.stream.Stream;

/**
 *
 * @author Kasper Nielsen
 */
public class ArrayListFactoryGenerator {

    private final CodegenClass c = new CodegenClass();

    String elementType = "Object";

    private void add() {
        CodegenMethod m = c.addMethod("public boolean add(Object e)");
        m.add("growIfNeeded(size + 1);");
        m.add("a[size++] = e;");
        m.add("return true;");
    }

    void build() {
        c.addImport(AbstractList.class, List.class, RandomAccess.class);
        c.setDefinition("public class ArrayList extends AbstractList implements ", List.class, ", ", RandomAccess.class);
        fields();
        constructors();
        get();
        add();
        growIfNeeded();

        stream();
        new ArrayListFactoryGeneratorSupport(c).build();

    }

    private void constructors() {
        CodegenMethod con = c.addMethod("public ()");
        con.add("a = new ", elementType, "[10];");
        con = c.addMethod("public (int initialCapacity)");
        con.add("a = new ", elementType, "[checkInitialCapacity(initialCapacity)];");

        con = c.addMethod("public (Collection c)");
        con.add("a = new ", elementType, "[c.size()];");
        con.add("addAll(c);");
    }

    private void fields() {
        c.addField("public ", elementType, "[] a;");
        c.addField("public int size;");
        c.addImport(ArrayListFactoryGenerator.class);

        c.addField("ArrayListInfo info;");

        c.addImport(Stream.class);
    }

    private void get() {
        CodegenMethod get = c.addMethod("public Object get(int index)");
        get.add("return a[index];");

        CodegenMethod size = c.addMethod("public int size()");
        size.add("return size;");
    }

    private void growIfNeeded() {
        CodegenMethod m = c.addMethod("void growIfNeeded(int minimumCapacity)");
        m.add("if (minimumCapacity - a.length > 0) {");
        m.add("a = grow(a, minimumCapacity);");
        m.add("}");
    }

    private void stream() {
        CodegenMethod m = c.addMethod("public Stream stream()");
        m.add("ArrayListInfo i = info;");
        m.addImport(AbstractStream.class);
        m.add("return ", AbstractStream.class, ".from(i == null ? (info = new ArrayListInfo(this)) : i);");
    }

    @SuppressWarnings({ "unchecked" })
    public static <T> ArrayListFactory<T> build(ArrayListFactoryBuilder builder) {
        ArrayListFactoryGenerator b = new ArrayListFactoryGenerator();
        b.build();

        Codegen codegen = GeneratorUtil.newCodegen(builder);
        codegen.addClass(b.c);
        Class<Object> cl = b.c.compile();

        Class<?> proc = GeneratorUtil.getDeclaredClass(cl, e -> e.getName().contains("Processor"));

        CompiledCollectionConfiguration<?> vc = new CompiledCollectionConfiguration<>(codegen, proc);
        Identifier id = new Identifier(Object[].class, "ar");
        Identifier size = new Identifier(int.class, "listSize");
        vc.addParameter(id);
        vc.addParameter(size);
        vc.setMain(ArrayOrListComposite.create(Object[].class, id).withUpperBounds(size));

        Class<?> f = GeneratorUtil.getDeclaredClass(cl, cc -> ArrayListFactory.class.isAssignableFrom(cc));
        return (ArrayListFactory<T>) GeneratorUtil.instantiate(f, vc.create());
    }
}
