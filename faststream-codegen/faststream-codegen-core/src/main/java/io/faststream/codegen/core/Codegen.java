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
package io.faststream.codegen.core;

import static io.faststream.codegen.core.CodegenUtil.toStringg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ConcurrentHashMap;

import io.faststream.codegen.janino.JavaSourceClassLoader;
import io.faststream.codegen.janino.util.resource.Resource;
import io.faststream.codegen.janino.util.resource.ResourceFinder;

/**
 *
 * @author Kasper Nielsen
 */
public class Codegen {

    final ConcurrentHashMap<CodegenClass, CodegenClass> classes = new ConcurrentHashMap<>();

    private volatile JavaSourceClassLoader classLoader;

    private final String defaultPackageName;

    private Object lateInitializer;

    final ClassLoader parent;

    final PrintWriter[] pws;

    final Path sourcePath;

    public Codegen() {
        this(new CodegenConfiguration());
    }

    public Codegen(Codegen p) {
        sourcePath = p.sourcePath;
        defaultPackageName = p.defaultPackageName;
        pws = p.pws;
        this.parent = p.classLoader;
        classLoader = AccessController.doPrivileged(new PrivilegedAction<JavaSourceClassLoader>() {
            public JavaSourceClassLoader run() {
                JavaSourceClassLoader l = new JavaSourceClassLoader(parent, new DefaultResourceFinder(), null);
                l.setDebuggingInfo(true, true, true);
                return l;
            }
        });
    }

    public Codegen(CodegenConfiguration configuration) {
        sourcePath = configuration.getSourcePath();
        defaultPackageName = configuration.getDefaultPackage();
        pws = configuration.getCodeWriters().toArray(new PrintWriter[0]);
        // pws = new PrintWriter[1];
        // pws[0] = new PrintWriter(System.out);
        final ClassLoader cl = configuration.getClassLoaderParent();
        parent = cl == null ? Thread.currentThread().getContextClassLoader() : cl;
        // TODO CHECK SECURITY
        classLoader = AccessController.doPrivileged(new PrivilegedAction<JavaSourceClassLoader>() {
            public JavaSourceClassLoader run() {
                JavaSourceClassLoader l = new JavaSourceClassLoader(cl == null ? Thread.currentThread()
                        .getContextClassLoader() : cl, new DefaultResourceFinder(), null);
                l.setDebuggingInfo(true, true, true);
                return l;
            }
        });
    }

    public <T extends CodegenClass> T addClass(T clazz) {
        if (clazz.getClassLoader() != null) {
            throw new IllegalArgumentException("clazz has already been added to a codegen");
        }
        if (clazz instanceof LazyInitializer) {
            create(clazz, this);
        }
        if (clazz.getPackage() == null) {
            clazz.setPackage(defaultPackageName);
        }
        clazz.setClassLoader(getClassLoader());
        classes.put(clazz, clazz);
        return clazz;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    final void create(CodegenClass claz, Codegen codegen) {
        ((LazyInitializer) claz).accept(lateInitializer);
        for (CodegenMethod a : claz.getMethods()) {
            if (a instanceof LazyInitializer) {
                LazyInitializer sm = (LazyInitializer) a;
                sm.accept(lateInitializer);
            }
        }
        if (claz.innerClasses != null) {
            for (CodegenClass cc : claz.innerClasses) {
                if (cc instanceof LazyInitializer) {
                    create(cc, codegen);
                }
            }
        }
    }

    ClassLoader getClassLoader() {
        // This is mainly done to test the speed of classloaders with to many classes.
        // if (ThreadLocalRandom.current().nextInt(500) == 0) {
        // // System.err.println(generatedClasses.get());
        // classLoader = newClassLoader(parent);
        // }
        return classLoader;
    }

    public byte[] load(String name) {
        return classLoader.load(name);
    }

    public CodegenClass newClass() {
        return addClass(new CodegenClass());
    }

    public CodegenClass newClass(Object... header) {
        return newClass(toStringg(header));
    }

    public CodegenClass newClass(String definition) {
        return addClass(new CodegenClass().setDefinition(definition));
    }

    JavaSourceClassLoader newClassLoader(ClassLoader parent) {
        JavaSourceClassLoader l = new JavaSourceClassLoader(parent, new DefaultResourceFinder(), null);
        l.setDebuggingInfo(true, true, true);
        return l;
    }

    public void setLateInitializerObject(Object o) {
        this.lateInitializer = o;
    }

    class DefaultResourceFinder extends ResourceFinder {

        @Override
        public Resource findResource(final String resourceName) {
            CodegenClass cc = null;
            for (CodegenClass c : classes.keySet()) {
                if (c.getJavaName().equals(resourceName)) {
                    cc = c;
                    classes.remove(cc);
                    break;
                }
            }
            // check if we don't have a class corresponding to the name.
            // In which case it should look at the current classloader
            if (cc == null) {
                return null;
            }
            CodegenClass ccc = cc;
            return new Resource() {
                public String getFileName() {
                    return resourceName;
                }

                public InputStream open() throws IOException {
                    StringBuilder s = ccc.toString(new StringBuilder());
                    for (PrintWriter pw : pws) {
                        pw.append(s).flush();
                    }
                    if (sourcePath != null) {
                        ccc.writeSource(s, sourcePath);
                    }
                    return new ByteArrayInputStream(s.toString().getBytes());
                }

                @Override
                public long lastModified() {
                    return 0;
                }
            };
        }
    }
}
