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
package org.cakeframework.internal.db.query.compiler.render.util;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import org.cakeframework.internal.db.query.compiler.datasource.ArrayOrListComposite;
import org.cakeframework.internal.db.query.plan.QueryOperationParameterList.QueryOperationParameter;

import io.faststream.codegen.core.CodegenClass;
import io.faststream.codegen.core.CodegenMethod;
import io.faststream.codegen.core.CodegenUtil;
import io.faststream.codegen.model.expression.NameExpression;
import io.faststream.codegen.model.visitor.JavaRenderer;

/**
 *
 * @author Kasper Nielsen
 */
public class ReturnIteratorOrSpliteratorGenerator {
    final CodegenClass c;
    final StreamType t;
    final ReturnIteratorOrSpliterator ri;
    final boolean isIterator;

    final NameExpression start = new NameExpression("start");
    final NameExpression end = new NameExpression("end");

    ReturnIteratorOrSpliteratorGenerator(CodegenClass c, Class<?> type, ReturnIteratorOrSpliterator ri) {
        this.c = requireNonNull(c);
        this.t = StreamType.from(type);
        this.ri = ri;
        this.isIterator = ri.isIterator;

    }

    void generate(ArrayOrListComposite com) {
        List<QueryOperationParameter> l = ri.l;
        // c.addImport(Iterator.class);

        c.setDefinition("static class ", ri.className, " implements ",
                (isIterator ? t.getIteratorType() : t.getSpliteratorType()).getCanonicalName());

        c.addField("final ", com.getType(), " a;");
        c.addField("int start;");
        c.addField("int end;");
        c.addField("int cursor;");
        ArrayList<Object> arguments = new ArrayList<>();
        arguments.add(ri.className);
        arguments.add("(");
        arguments.add(com.getType());
        arguments.add(" a, int start, int end");

        for (QueryOperationParameter p : l) {
            arguments.add(", ");
            arguments.add(p.getType());// already imported
            arguments.add(" ");
            arguments.add(p.accessor().toString());
        }
        arguments.add(")");

        CodegenMethod m = c.addMethod(arguments.toArray());
        m.add("this.a = a;");
        m.add("this.start = start;");
        m.add("this.cursor = start;");

        for (QueryOperationParameter p : l) {
            c.addField("final ", p.getType(), " ", p.accessor(), ";");
            m.add("this.", p.accessor(), " = ", p.accessor(), ";");
        }

        if (isIterator) {
            m.add("this.cursor = start - 1;");
            m.add("this.end = end - 1;");

            c.addField(t.getType(), " next;");

            m.add("advance();");
            generateIterator();
        } else {
            // m.add("this.cursor = 0;");
            m.add("this.end = end;");
            // m.add("System.out.println(start + \"-\" + end);");
            generateSpliterator();
        }
    }

    void generateIterator() {

        CodegenMethod m = c.addMethod("public void advance()");

        // m.add("while (cursor < end) {");
        // m.add(com.getComponentType(), " o = a[++cursor];");
        m.add(JavaRenderer.renderBlock(ri.bs, 3));
        // m.add("}");
        // m.add("cursor = end + 1;");

        c.addMethod("public void remove()").throwNewUnsupportedOperationException("remove is not supported");

        c.addMethod("public boolean hasNext()").add("return cursor <= end;");

        m = c.addMethod("public ", t.getType(), " next", t.prepend(), "()");
        m.add("if (cursor > end) {");
        m.addImport(NoSuchElementException.class);
        m.add("throw new ", NoSuchElementException.class, "();");
        m.add("}");
        m.add(t.getType(), " next = this.next;");
        m.add("advance();");
        m.add("return next;");

        if (t.isPrimitive()) {
            m = c.addMethod("public ", CodegenUtil.boxClass(t.getType()), " next()");
            m.add("return next", t.prepend(), "();");
        }
    }

    void generateSpliterator() {
        c.addImport(t.getConsumerType(), Objects.class);
        CodegenMethod m = c.addMethod("public boolean tryAdvance(", t.getConsumerType(), " consumer)");
        m.add("Objects.requireNonNull(consumer, \"consumer is null\");");
        m.add(JavaRenderer.renderBlock(ri.bs, 3));
        m.add("return false;");

        m = c.addMethod("public ", t.getSpliteratorType().getCanonicalName(), " trySplit()");
        m.add("int mid = (cursor + end) >>> 1;");
        m.add("if (cursor >= mid) {");
        m.add("return null;");
        m.add("}");

        String arg = ri.l.stream().map(e -> ", " + e.accessor().toString()).collect(Collectors.joining(""));

        m.add("return new ", ri.className, "(a, cursor, cursor = mid", arg, ");");
        // int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
        // return (lo >= mid) ? null : // divide range in half unless too small
        // new ArrayListSpliterator<E>(list, lo, index = mid,
        // expectedModCount);

        c.addMethod("public long estimateSize()").add("return end - cursor;");
        c.addMethod("public long getExactSizeIfKnown()").add("return end - cursor;");
        c.addMethod("public int characteristics()").add("return SIZED | SUBSIZED;");

    }
}
