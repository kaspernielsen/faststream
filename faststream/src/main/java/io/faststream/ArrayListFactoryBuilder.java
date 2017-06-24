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
package io.faststream;

import io.faststream.internal.ArrayListFactoryGenerator;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 *
 * @author Kasper Nielsen
 */
public final class ArrayListFactoryBuilder extends AbstractBuilder {

    public <T> ArrayListFactory<T> build() {
        return ArrayListFactoryGenerator.build(this);
    }

    /**
     * List that are created by the factory being build will not keep track of mod count.
     * 
     * @return
     */
    public ArrayListFactoryBuilder disableModCount() {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ArrayListFactoryBuilder addCodeWriter(PrintStream stream) {
        return (ArrayListFactoryBuilder) super.addCodeWriter(stream);
    }

    /** {@inheritDoc} */
    @Override
    public ArrayListFactoryBuilder addCodeWriter(PrintWriter writer) {
        return (ArrayListFactoryBuilder) super.addCodeWriter(writer);
    }

    /** {@inheritDoc} */
    @Override
    public ArrayListFactoryBuilder setClassLoaderParent(ClassLoader parentClassLoader) {
        return (ArrayListFactoryBuilder) super.setClassLoaderParent(parentClassLoader);
    }

    /** {@inheritDoc} */
    @Override
    public ArrayListFactoryBuilder setPackage(String defaultPackage) {
        return (ArrayListFactoryBuilder) super.setPackage(defaultPackage);
    }

    /** {@inheritDoc} */
    @Override
    public ArrayListFactoryBuilder setSourcePath(Path directory) {
        return (ArrayListFactoryBuilder) super.setSourcePath(directory);
    }

    /** {@inheritDoc} */
    @Override
    public ArrayListFactoryBuilder setSourcePath(String directory) {
        return (ArrayListFactoryBuilder) super.setSourcePath(directory);
    }
}
