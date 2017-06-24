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

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import io.faststream.query.db.nodes.view.ViewHelpers;
import io.faststream.query.db.nodes.view.mapview.MVT_ForEach;
import io.faststream.query.db.nodes.view.mapview.MVT_IsEmpty;
import io.faststream.query.db.nodes.view.mapview.MVT_Size;
import io.faststream.query.db.nodes.view.mapview.MVT_To;
import io.faststream.query.db.nodes.view.mapview.MVT_ToMap;
import io.faststream.query.db.nodes.view.mapview.MV_Entries;
import io.faststream.query.db.nodes.view.mapview.MV_Filter;
import io.faststream.query.db.nodes.view.mapview.MV_FilterNullValues;
import io.faststream.query.db.nodes.view.mapview.MV_FilterOnKey;
import io.faststream.query.db.nodes.view.mapview.MV_FilterOnKeyType;
import io.faststream.query.db.nodes.view.mapview.MV_FilterOnValue;
import io.faststream.query.db.nodes.view.mapview.MV_FilterOnValueType;
import io.faststream.query.db.nodes.view.mapview.MV_Keys;
import io.faststream.query.db.nodes.view.mapview.MV_Map;
import io.faststream.query.db.nodes.view.mapview.MV_MapKey;
import io.faststream.query.db.nodes.view.mapview.MV_MapValue;
import io.faststream.query.db.nodes.view.mapview.MV_Sorted;
import io.faststream.query.db.nodes.view.mapview.MV_SortedByKey;
import io.faststream.query.db.nodes.view.mapview.MV_SortedByKeyAscending;
import io.faststream.query.db.nodes.view.mapview.MV_SortedByKeyDescending;
import io.faststream.query.db.nodes.view.mapview.MV_SortedByValue;
import io.faststream.query.db.nodes.view.mapview.MV_SortedByValueAscending;
import io.faststream.query.db.nodes.view.mapview.MV_SortedByValueDescending;
import io.faststream.query.db.nodes.view.mapview.MV_Take;
import io.faststream.query.db.nodes.view.mapview.MV_Values;
import io.faststream.query.db.nodes.view.mapview.MapViewVisitable;
import io.faststream.query.db.query.node.QueryOperationNode;
import io.faststream.query.util.BiComparator;

@SuppressWarnings({ "rawtypes", "unchecked" })
class SerialMapViewProcessor extends AbstractSerialArrayViewProcessor<Map.Entry> implements
MapViewVisitable.MapViewVisitor {

    SerialMapViewProcessor(Entry[] e) {
        super(e, true);
    }

    public SerialMapViewProcessor(Map m) {
        this((Entry[]) m.entrySet().toArray(new Map.Entry[m.size()]));
    }

    public void forEach(MVT_ForEach node) {
        forEach(node.getBiConsumer());
    }

    public void forEach(BiConsumer<? super Object, ? super Object> procedure) {
        for (int i = lo; i < hi; i++) {
            Map.Entry e = a[i];
            procedure.accept(e.getKey(), e.getValue());
        }
        setResult(null);
    }

    public void entries(MV_Entries node) {
        setNext(SerialCollectionViewProcessor.createUsingHandoff(toMap().entrySet().toArray()));
    }

    private void filter(final BiPredicate<? super Object, ? super Object> predicate) {
        filter(new Predicate<Map.Entry>() {
            public boolean test(Entry a) {
                return predicate.test(a.getKey(), a.getValue());
            }
        });
    }

    public void filter(MV_Filter node) {
        filter(node.getBiPredicate());
    }

    @Override
    public void filterNullValues(MV_FilterNullValues node) {
        filter((k, v) -> v != null);
    }

    @Override
    public void filterOnKey(MV_FilterOnKey node) {
        filterOnKey(node.getPredicate());
    }

    private void filterOnKey(Predicate<?> predicate) {
        filter((k, v) -> ((Predicate) predicate).test(k));
    }

    private void filterOnKeyType(Class<?> type) {
        filterOnKey(e -> type.isInstance(e));
    }

    @Override
    public void filterOnKeyType(MV_FilterOnKeyType node) {
        filterOnKeyType(node.getType());
    }

    @Override
    public void filterOnValue(MV_FilterOnValue node) {
        filterOnValue(node.getPredicate());
    }

    private void filterOnValue(Predicate<?> predicate) {
        filter((k, v) -> ((Predicate) predicate).test(v));
    }

    private void filterOnValueType(Class<?> type) {
        filterOnValue(e -> type.isInstance(e));
    }

    @Override
    public void filterOnValueType(MV_FilterOnValueType node) {
        filterOnValueType(node.getType());
    }

    @Override
    public void isEmpty(MVT_IsEmpty node) {
        isEmpty();
    }

    public void keys(MV_Keys node) {
        setNext(SerialCollectionViewProcessor.createUsingHandoff(toMap().keySet().toArray()));
    }

    public void map(MV_Map node) {
        map(node.getMapper());
    }

    public void map(BiFunction<? super Object, ? super Object, ?> mapper) {

        Object[] o = new Object[hi - lo];
        for (int i = 0; i < o.length; i++) {
            Map.Entry e = a[i + lo];
            o[i] = mapper.apply(e.getKey(), e.getValue());
        }
        setNext(SerialCollectionViewProcessor.createUsingHandoff(o));
    }

    public void mapKey(MV_MapKey node) {
        mapKey(node.getMapper());
    }

    public void mapKey(Function<? super Object, ?> mapper) {
        HashMap<Object, ArrayList> map = new HashMap<>();
        for (int i = lo; i < hi; i++) {
            Map.Entry e = a[i];
            Object key = mapper.apply(e.getKey());
            ArrayList al = map.get(key);
            if (al == null) {
                al = new ArrayList(2);
                map.put(key, al);
            }
            al.add(e.getValue());
        }
        MutableMultimapEntry[] me = new MutableMultimapEntry[map.size()];
        Iterator<Map.Entry<Object, ArrayList>> iter = map.entrySet().iterator();
        for (int i = 0; i < me.length; i++) {
            Map.Entry<Object, ArrayList> entry = iter.next();
            me[i] = new MutableMultimapEntry(entry.getKey(), entry.getValue().toArray());
        }
        setNext(new SerialMultimapMapViewProcessor(me));
    }

    public void mapValue(MV_MapValue node) {
        mapValue(node.getMapper());
    }

    public void mapValue(Function<? super Object, ?> mapper) {
        for (int i = lo; i < hi; i++) {
            a[i] = new SimpleImmutableEntry(a[i].getKey(), mapper.apply(a[i].getValue()));
        }

    }

    public Entry[] newArray(int size) {
        return new Map.Entry[size];
    }

    public void sorted(final BiComparator<? super Object, ? super Object> binaryComparator) {
        sort(new Comparator<Entry>() {
            public int compare(Entry o1, Entry o2) {
                return binaryComparator.compare(o1.getKey(), o1.getValue(), o2.getKey(), o2.getValue());
            }
        });
    }

    public void sorted(MV_Sorted node) {
        sorted(node.getBiComparator());
    }

    @Override
    public void sortedByKeyAscending(MV_SortedByKeyAscending node) {
        sortedByKey(Comparator.naturalOrder());
    }

    @Override
    public void sortedByKey(MV_SortedByKey node) {
        sortedByKey(node.getComparator());
    }

    private void sortedByKey(Comparator<?> comparator) {
        sorted((k1, v1, k2, v2) -> ((Comparator) comparator).compare(k1, k2));
    }

    @Override
    public void sortedByKeyDescending(MV_SortedByKeyDescending node) {
        sortedByKey(Comparator.reverseOrder());
    }

    @Override
    public void sortedByValueAscending(MV_SortedByValueAscending node) {
        sortedByValue(Comparator.naturalOrder());
    }

    @Override
    public void sortedByValue(MV_SortedByValue node) {
        sortedByValue(node.getComparator());
    }

    @Override
    public void sortedByValueDescending(MV_SortedByValueDescending node) {
        sortedByValue(Comparator.reverseOrder());
    }

    private void sortedByValue(Comparator<?> comparator) {
        sorted((k1, v1, k2, v2) -> ((Comparator) comparator).compare(v1, v2));
    }

    public void process(QueryOperationNode node) {
        ((MapViewVisitable) node).accept(this);
    }

    public void size(MVT_Size node) {
        size();
    }

    public void take(MV_Take node) {
        take(node.getNumberOfEntriesToTake());
    }

    private void to(Class<?> type) {
        setResult(ViewHelpers.mapFrom(type, a, lo, hi));
    }

    public void to(MVT_To node) {
        to(node.getType());
    }

    public Map toMap() {
        LinkedHashMap m = new LinkedHashMap<>();
        for (int i = lo; i < hi; i++) {
            m.put(a[i].getKey(), a[i].getValue());
        }
        return m;
    }

    public void values(MV_Values node) {
        setNext(SerialCollectionViewProcessor.createUsingHandoff(toMap().values().toArray()));
    }

    /** {@inheritDoc} */
    @Override
    public void toMap(MVT_ToMap node) {
        to(Map.class);
    }
}
