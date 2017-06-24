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

import io.faststream.AbstractBuilder;
import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenConfiguration;

import java.io.PrintWriter;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.cakeframework.internal.db.query.compiler.anew.QueryCompiler;

/**
 *
 * @author Kasper Nielsen
 */
public class GeneratorUtil {
    private static final ThreadLocal<QueryCompiler<?>> TL = new ThreadLocal<>();

    public static Codegen newCodegen(AbstractBuilder builder) {
        CodegenConfiguration conf = new CodegenConfiguration();
        conf.setClassLoaderParent(builder.getClassLoaderParent());
        if (conf.getClassLoaderParent() == null) {
            // conf.setClassLoaderParent(AbstractSupport.class.getClassLoader());
        }
        conf.setDefaultPackage(builder.getPackage());
        conf.setSourcePath(builder.getSourcePath());
        for (PrintWriter pw : builder.getCodeWriters()) {
            conf.addCodeWriter(pw);
        }
        conf.setDefaultPackage("ddd");
        conf.addCodeWriter(System.out);
        return new Codegen(conf);
    }

    public static QueryCompiler<?> getIt() {
        return TL.get();
    }

    public static Object instantiate(Class<?> cl, QueryCompiler<?> s) {
        TL.set(s);
        try {
            return cl.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        } finally {
            TL.remove();
        }
    }

    public static Class<?> getDeclaredClass(Class<?> cl, Predicate<? super Class<?>> p) {
        return Stream.of(cl.getDeclaredClasses()).filter(p).findAny().orElseThrow(() -> new RuntimeException());
    }
}
