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
public final class ListFactoryBuilder extends AbstractBuilder {

    public <T> ListFactory<T> build() {
        return ArrayListFactoryGenerator.build(this);
    }

    /**
     * List that are created by the factory being build will not keep track of mod count.
     * 
     * @return
     */
    public ListFactoryBuilder disableModCount() {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ListFactoryBuilder addCodeWriter(PrintStream stream) {
        return (ListFactoryBuilder) super.addCodeWriter(stream);
    }

    /** {@inheritDoc} */
    @Override
    public ListFactoryBuilder addCodeWriter(PrintWriter writer) {
        return (ListFactoryBuilder) super.addCodeWriter(writer);
    }

    /** {@inheritDoc} */
    @Override
    public ListFactoryBuilder setClassLoaderParent(ClassLoader parentClassLoader) {
        return (ListFactoryBuilder) super.setClassLoaderParent(parentClassLoader);
    }

    /** {@inheritDoc} */
    @Override
    public ListFactoryBuilder setPackage(String defaultPackage) {
        return (ListFactoryBuilder) super.setPackage(defaultPackage);
    }

    /** {@inheritDoc} */
    @Override
    public ListFactoryBuilder setSourcePath(Path directory) {
        return (ListFactoryBuilder) super.setSourcePath(directory);
    }

    /** {@inheritDoc} */
    @Override
    public ListFactoryBuilder setSourcePath(String directory) {
        return (ListFactoryBuilder) super.setSourcePath(directory);
    }
}
