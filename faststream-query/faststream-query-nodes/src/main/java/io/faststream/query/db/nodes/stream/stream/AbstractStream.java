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
/* 
 * This class was automatically generated by cake.bootstrap.view.GenerateAll 
 * Available in the https://github.com/cakeframework/cake-developers/ project 
 */
package io.faststream.query.db.nodes.stream.stream;

/**
 * This class has been autogenerated
 *
 * @author Kasper Nielsen
 */
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.faststream.query.db.nodes.stream.StreamContext;
import io.faststream.query.db.query.node.EmptyResult;
import io.faststream.query.db.query.node.QueryOperationNode;
import io.faststream.query.db.query.node.QueryOperationNodeDefinition;
import io.faststream.query.db.query.node.TerminalQueryOperationNodeProcessor;

@SuppressWarnings({"rawtypes", "unchecked" })
public abstract class AbstractStream extends QueryOperationNode implements Stream {

    private static final long serialVersionUID = 1L;

    private final StreamContext context;

    public AbstractStream(QueryOperationNode previous, StreamContext context) {
        super(previous);
        this.context = context;
    }

    public AbstractStream(TerminalQueryOperationNodeProcessor processor) {
        super(processor);
        this.context = new StreamContext();
    }

    public boolean allMatch(Predicate predicate) {
        return (Boolean) new SOT_AllMatch(this, context, predicate).process();
    }

    public boolean anyMatch(Predicate predicate) {
        return (Boolean) new SOT_AnyMatch(this, context, predicate).process();
    }

    public void close() {
        context.close();
    }

    public Object collect(Collector collector) {
        return new SOT_Collect(this, context, collector).process();
    }

    public Object collect(Supplier supplier, BiConsumer accumulator, BiConsumer combiner) {
        return new SOT_CollectSupplier(this, context, supplier, accumulator, combiner).process();
    }

    public long count() {
        return (Long) new SOT_Count(this, context).process();
    }

    public Stream distinct() {
        return new SO_Distinct(this, context);
    }

    public Stream filter(Predicate predicate) {
        return new SO_Filter(this, context, predicate);
    }

    public Optional findAny() {
        Object result = new SOT_FindAny(this, context).process();
        if (result == EmptyResult.EMPTY_RESULT) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public Optional findFirst() {
        Object result = new SOT_FindFirst(this, context).process();
        if (result == EmptyResult.EMPTY_RESULT) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public Stream flatMap(Function mapper) {
        return new SO_FlatMap(this, context, mapper);
    }

    public DoubleStream flatMapToDouble(Function mapper) {
        return new SO_FlatMapToDouble(this, context, mapper);
    }

    public IntStream flatMapToInt(Function mapper) {
        return new SO_FlatMapToInt(this, context, mapper);
    }

    public LongStream flatMapToLong(Function mapper) {
        return new SO_FlatMapToLong(this, context, mapper);
    }

    public void forEach(Consumer action) {
        new SOT_ForEach(this, context, action).process();
    }

    public void forEachOrdered(Consumer action) {
        new SOT_ForEachOrdered(this, context, action).process();
    }

    public boolean isParallel() {
        return false;
    }

    public Iterator iterator() {
        return (Iterator) new SOT_Iterator(this, context).process();
    }

    public Stream limit(long maxSize) {
        return new SO_Limit(this, context, maxSize);
    }

    public Stream map(Function mapper) {
        return new SO_Map(this, context, mapper);
    }

    public DoubleStream mapToDouble(ToDoubleFunction mapper) {
        return new SO_MapToDouble(this, context, mapper);
    }

    public IntStream mapToInt(ToIntFunction mapper) {
        return new SO_MapToInt(this, context, mapper);
    }

    public LongStream mapToLong(ToLongFunction mapper) {
        return new SO_MapToLong(this, context, mapper);
    }

    public Optional max(Comparator comparator) {
        Object result = new SOT_MaxComparator(this, context, comparator).process();
        if (result == EmptyResult.EMPTY_RESULT) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public Optional min(Comparator comparator) {
        Object result = new SOT_MinComparator(this, context, comparator).process();
        if (result == EmptyResult.EMPTY_RESULT) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public boolean noneMatch(Predicate predicate) {
        return (Boolean) new SOT_NoneMatch(this, context, predicate).process();
    }

    public Stream onClose(Runnable runnable) {
        context.onClose(runnable);
        return this;
    }

    public Stream parallel() {
        return new SO_Parallel(this, context);
    }

    public Stream peek(Consumer consumer) {
        return new SO_Peek(this, context, consumer);
    }

    public Optional reduce(BinaryOperator accumulator) {
        Object result = new SOT_Reduce(this, context, accumulator).process();
        if (result == EmptyResult.EMPTY_RESULT) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public Object reduce(Object identity, BinaryOperator accumulator) {
        Object result = new SOT_Reduce(this, context, accumulator).process();
        if (result == EmptyResult.EMPTY_RESULT) {
            return identity;
        }
        return result;
    }

    public Object reduce(Object identity, BiFunction accumulator, BinaryOperator combiner) {
        return new SOT_ReduceCombiner(this, context, identity, accumulator, combiner).process();
    }

    public Stream sequential() {
        return new SO_Sequential(this, context);
    }

    public Stream skip(long n) {
        return new SO_Skip(this, context, n);
    }

    public Stream sorted() {
        return new SO_SortedAscending(this, context);
    }

    public Stream sorted(Comparator comparator) {
        return new SO_SortedComparator(this, context, comparator);
    }

    public Spliterator spliterator() {
        return (Spliterator) new SOT_Spliterator(this, context).process();
    }

    public Object[] toArray() {
        return (Object[]) new SOT_ToArray(this, context).process();
    }

    public Object[] toArray(IntFunction generator) {
        return (Object[]) new SOT_ToArrayFunction(this, context, generator).process();
    }

    public Stream unordered() {
        return new SO_Unordered(this, context);
    }

    public static <T> Stream<T> from(TerminalQueryOperationNodeProcessor processor) {
        return new DefaultStream(processor);
    }

    static final class DefaultStream extends AbstractStream {

        private static final long serialVersionUID = 1L;

        DefaultStream(TerminalQueryOperationNodeProcessor processor) {
            super(processor);
        }

        public final int getNodeId() {
            return -2;
        }

        @Override
        public final int getNodeType() {
            return 2;
        }

        @Override
        public final String name() {
            return "root";
        }

        @Override
        public final QueryOperationNodeDefinition getOperationPackage() {
            return null;
        }
    }
}