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
package io.faststream.query.view.interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.faststream.query.db.nodes.view.ViewHelpers;
import io.faststream.query.db.nodes.view.collectionview.CVT_Any;
import io.faststream.query.db.nodes.view.collectionview.CVT_First;
import io.faststream.query.db.nodes.view.collectionview.CVT_ForEach;
import io.faststream.query.db.nodes.view.collectionview.CVT_IsEmpty;
import io.faststream.query.db.nodes.view.collectionview.CVT_Last;
import io.faststream.query.db.nodes.view.collectionview.CVT_One;
import io.faststream.query.db.nodes.view.collectionview.CVT_Reduce;
import io.faststream.query.db.nodes.view.collectionview.CVT_Size;
import io.faststream.query.db.nodes.view.collectionview.CVT_To;
import io.faststream.query.db.nodes.view.collectionview.CVT_ToList;
import io.faststream.query.db.nodes.view.collectionview.CV_AsStream;
import io.faststream.query.db.nodes.view.collectionview.CV_Count;
import io.faststream.query.db.nodes.view.collectionview.CV_Distinct;
import io.faststream.query.db.nodes.view.collectionview.CV_Filter;
import io.faststream.query.db.nodes.view.collectionview.CV_FilterNulls;
import io.faststream.query.db.nodes.view.collectionview.CV_FilterOnType;
import io.faststream.query.db.nodes.view.collectionview.CV_Gather;
import io.faststream.query.db.nodes.view.collectionview.CV_GroupBy;
import io.faststream.query.db.nodes.view.collectionview.CV_Map;
import io.faststream.query.db.nodes.view.collectionview.CV_MapToIndex;
import io.faststream.query.db.nodes.view.collectionview.CV_Reverse;
import io.faststream.query.db.nodes.view.collectionview.CV_Shuffle;
import io.faststream.query.db.nodes.view.collectionview.CV_Sorted;
import io.faststream.query.db.nodes.view.collectionview.CV_SortedAscending;
import io.faststream.query.db.nodes.view.collectionview.CV_SortedDescending;
import io.faststream.query.db.nodes.view.collectionview.CV_Take;
import io.faststream.query.db.nodes.view.collectionview.CollectionViewVisitable;
import io.faststream.query.db.query.node.EmptyResult;
import io.faststream.query.db.query.node.QueryOperationNode;

@SuppressWarnings({ "rawtypes", "unchecked" })
final class SerialCollectionViewProcessor<E> extends AbstractSerialArrayViewProcessor<Object> implements
CollectionViewVisitable.CollectionViewVisitor {

    private SerialCollectionViewProcessor(Object[] a, boolean isSafe) {
        super(a, isSafe);
    }

    @Override
    public void any(CVT_Any node) {
        any();
    }

    private void forEach(Consumer procedure) {
        Object[] a = this.a;
        int lo = this.lo;
        int i = lo;
        int hi = this.hi;
        while (i < hi) {
            procedure.accept(a[i++]);
        }
        setResult(null);
    }

    @Override
    public void forEach(CVT_ForEach node) {
        forEach(node.getConsumer());
    }

    /** {@inheritDoc} */
    @Override
    public void asStream(CV_AsStream node) {
        Stream s = Stream.of(Arrays.copyOfRange(a, lo, hi));
        setNext(new StreamProcessor(s));
    }

    public void count(CV_Count node) {
        Map<Object, Long> m = new HashMap<>();
        for (int i = lo; i < hi; i++) {
            Object e = a[i];
            Long current = m.get(e);
            m.put(e, current == null ? 1L : current + 1);
        }
        setNext(new SerialMapViewProcessor(m));
    }

    @Override
    public void distinct(CV_Distinct node) {
        HashSet set = new HashSet(Arrays.asList(Arrays.copyOfRange(a, lo, hi)));
        a = set.toArray();
        lo = 0;
        hi = a.length;
    }

    @Override
    public void filter(CV_Filter node) {
        filter(node.getPredicate());
    }

    public void filterNulls(CV_FilterNulls node) {
        filter(e -> e != null);
    }

    public void filterOnType(CV_FilterOnType node) {
        filter(e -> node.getClazz().isInstance(e));
    }

    @Override
    public void first(CVT_First node) {
        head();
    }

    public void gather(CV_Gather node) {
        gather(node.getGatherer());
    }

    public void gather(Supplier<?> generator) {
        Consumer p = (Consumer) generator.get(); // Perhaps we should create more then one
        for (int i = lo; i < hi; i++) {
            p.accept(a[i]);
        }
        setNext(createUsingHandoff(new Object[] { p }));
    }

    public void groupBy(CV_GroupBy node) {
        groupBy(node.getMapper());
    }

    public void groupBy(Function<? super Object, ?> mapper) {
        HashMap<Object, ArrayList> map = new HashMap<>();
        for (int i = lo; i < hi; i++) {
            Object o = a[i];
            Object key = mapper.apply(o);
            ArrayList al = map.get(key);
            if (al == null) {
                al = new ArrayList(2);
                map.put(key, al);
            }
            al.add(o);
        }
        MutableMultimapEntry[] me = new MutableMultimapEntry[map.size()];
        Iterator<Map.Entry<Object, ArrayList>> iter = map.entrySet().iterator();
        for (int i = 0; i < me.length; i++) {
            Map.Entry<Object, ArrayList> entry = iter.next();
            me[i] = new MutableMultimapEntry(entry.getKey(), entry.getValue().toArray());
        }
        setNext(new SerialMultimapMapViewProcessor(me));
    }

    @Override
    public void isEmpty(CVT_IsEmpty node) {
        isEmpty();
    }

    @Override
    public void last(CVT_Last node) {
        tail();
    }

    public void map(CV_Map node) {
        map(node.getMapper());
    }

    public void mapToIndex(CV_MapToIndex node) {
        LinkedHashMap<Long, Object> map = new LinkedHashMap<>();
        for (int i = lo; i < hi; i++) {
            map.put(Long.valueOf(i - lo), a[i]);
        }
        setNext(new SerialMapViewProcessor(map));
    }

    @Override
    public void one(CVT_One node) {
        one();
    }

    public void process(QueryOperationNode node) {
        ((CollectionViewVisitable) node).accept(this);
    }

    public void reduce(BinaryOperator<? super Object> reducer) {
        Object[] a = this.a;
        int lo = this.lo;
        int hi = this.hi;
        if (hi == lo) {
            setResult(EmptyResult.EMPTY_RESULT);
            return;
        }
        Object result = a[lo];
        int i = lo + 1;
        try {
            while (i < hi) {
                result = reducer.apply(result, a[i++]);
            }
        } finally {
            // stats.readObjectArray(i - lo);
            // stats.op(reducer, i - lo - 1);
        }
        setResult(result);
    }

    public void reduce(CVT_Reduce node) {
        reduce(node.getReducer());
    }

    @Override
    public void reverse(CV_Reverse node) {
        reverse();
    }

    @Override
    public void shuffle(CV_Shuffle node) {
        shuffle();
    }

    @Override
    public void size(CVT_Size node) {
        size();
    }

    @Override
    public void sorted(CV_Sorted node) {
        Comparator<? super Object> comparator = node.getComparator();
        sort(comparator);
    }

    @Override
    public void sortedAscending(CV_SortedAscending node) {
        sort(Comparator.naturalOrder());
    }

    // public void take(long position, long numberOfElements) {
    // if (startIndex == 0) {
    // return take(numberOfElementsToTake);
    // }
    // if (startIndex > 0) {
    // if (numberOfElementsToTake > 0) {
    // int index = (int) startIndex;
    // if (index >= s) {
    // return new DummyCollectionView<E>(new ArrayList<E>());
    // }
    // return new DummyCollectionView<E>(new ArrayList<E>(list.subList(index,
    // Math.min(s, index + (int) numberOfElementsToTake))));
    // } else {
    // return new DummyCollectionView<E>(new ArrayList<E>(list.subList(
    // Math.max(0, s - (int) numberOfElementsToTake), s - 1)));
    // }
    // }
    // throw new UnsupportedOperationException();
    // } \ \ bb

    @Override
    public void sortedDescending(CV_SortedDescending node) {
        sort(Comparator.reverseOrder());
    }

    @Override
    public void take(CV_Take node) {
        take(node.getNumberOfElementsToTake());
    }

    private void to(Class<?> type) {
        setResult(ViewHelpers.collectionFrom(type, a, lo, hi, true));
    }

    public void to(CVT_To node) {
        to(node.getType());

    }

    /** {@inheritDoc} */
    @Override
    public void toList(CVT_ToList node) {
        to(List.class);
    }

    public static <E> SerialCollectionViewProcessor<E> createUsingCopy(E[] source) {
        return new SerialCollectionViewProcessor<>(source, false);
    }

    public static <E> SerialCollectionViewProcessor<E> createUsingHandoff(E[] handoff) {
        return new SerialCollectionViewProcessor<>(handoff, true);
    }

}
