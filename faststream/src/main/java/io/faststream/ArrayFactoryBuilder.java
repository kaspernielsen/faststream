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

import io.faststream.internal.ArrayFactoryGenerator;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.cakeframework.internal.db.nodes.stream.doublestream.AbstractDoubleStream;
import org.cakeframework.internal.db.nodes.stream.intstream.AbstractIntStream;
import org.cakeframework.internal.db.nodes.stream.longstream.AbstractLongStream;
import org.cakeframework.internal.db.nodes.stream.stream.AbstractStream;

/**
 *
 * @author Kasper Nielsen
 */
public class ArrayFactoryBuilder extends AbstractBuilder {

    /** {@inheritDoc} */
    @Override
    public ArrayFactoryBuilder addCodeWriter(PrintStream stream) {
        return (ArrayFactoryBuilder) super.addCodeWriter(stream);
    }

    /** {@inheritDoc} */
    @Override
    public ArrayFactoryBuilder addCodeWriter(PrintWriter writer) {
        return (ArrayFactoryBuilder) super.addCodeWriter(writer);
    }

    public ArrayFactory build() {
        return ArrayFactoryGenerator.newFactory(this, ArrayFactory.class, Object[].class, Stream.class,
                AbstractStream.class);
    }

    public ArrayFactory.OfDouble buildOfDouble() {
        return ArrayFactoryGenerator.newFactory(this, ArrayFactory.OfDouble.class, double[].class, DoubleStream.class,
                AbstractDoubleStream.class);
    }

    public ArrayFactory.OfInt buildOfInt() {
        return ArrayFactoryGenerator.newFactory(this, ArrayFactory.OfInt.class, int[].class, IntStream.class,
                AbstractIntStream.class);
    }

    public ArrayFactory.OfLong buildOfLong() {
        return ArrayFactoryGenerator.newFactory(this, ArrayFactory.OfLong.class, long[].class, LongStream.class,
                AbstractLongStream.class);
    }

    /** {@inheritDoc} */
    @Override
    public ArrayFactoryBuilder setClassLoaderParent(ClassLoader parentClassLoader) {
        return (ArrayFactoryBuilder) super.setClassLoaderParent(parentClassLoader);
    }

    /** {@inheritDoc} */
    @Override
    public ArrayFactoryBuilder setPackage(String defaultPackage) {
        return (ArrayFactoryBuilder) super.setPackage(defaultPackage);
    }

    /** {@inheritDoc} */
    @Override
    public ArrayFactoryBuilder setSourcePath(Path directory) {
        return (ArrayFactoryBuilder) super.setSourcePath(directory);
    }

    /** {@inheritDoc} */
    @Override
    public ArrayFactoryBuilder setSourcePath(String directory) {
        return (ArrayFactoryBuilder) super.setSourcePath(directory);
    }
}
