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
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import io.faststream.query.db.nodes.view.multimapview.MultimapViewVisitable;
import io.faststream.query.db.nodes.view.multimapview.UVT_IsEmpty;
import io.faststream.query.db.nodes.view.multimapview.UVT_Size;
import io.faststream.query.db.nodes.view.multimapview.UVT_To;
import io.faststream.query.db.nodes.view.multimapview.UVT_ToMultimap;
import io.faststream.query.db.nodes.view.multimapview.UV_Any;
import io.faststream.query.db.nodes.view.multimapview.UV_Count;
import io.faststream.query.db.nodes.view.multimapview.UV_Filter;
import io.faststream.query.db.nodes.view.multimapview.UV_FilterNullValues;
import io.faststream.query.db.nodes.view.multimapview.UV_FilterOnKey;
import io.faststream.query.db.nodes.view.multimapview.UV_FilterOnKeyType;
import io.faststream.query.db.nodes.view.multimapview.UV_FilterOnValue;
import io.faststream.query.db.nodes.view.multimapview.UV_FilterOnValueType;
import io.faststream.query.db.nodes.view.multimapview.UV_Head;
import io.faststream.query.db.nodes.view.multimapview.UV_Keys;
import io.faststream.query.db.nodes.view.multimapview.UV_Map;
import io.faststream.query.db.nodes.view.multimapview.UV_MapKey;
import io.faststream.query.db.nodes.view.multimapview.UV_MapValue;
import io.faststream.query.db.nodes.view.multimapview.UV_One;
import io.faststream.query.db.nodes.view.multimapview.UV_Reduce;
import io.faststream.query.db.nodes.view.multimapview.UV_SortedKeys;
import io.faststream.query.db.nodes.view.multimapview.UV_SortedKeysAscending;
import io.faststream.query.db.nodes.view.multimapview.UV_SortedKeysDescending;
import io.faststream.query.db.nodes.view.multimapview.UV_SortedValues;
import io.faststream.query.db.nodes.view.multimapview.UV_SortedValuesAscending;
import io.faststream.query.db.nodes.view.multimapview.UV_SortedValuesDescending;
import io.faststream.query.db.nodes.view.multimapview.UV_Tail;
import io.faststream.query.db.nodes.view.multimapview.UV_Take;
import io.faststream.query.db.nodes.view.multimapview.UV_TakeValues;
import io.faststream.query.db.nodes.view.multimapview.UV_Unique;
import io.faststream.query.db.nodes.view.multimapview.UV_Values;
import io.faststream.query.db.query.node.QueryOperationNode;
import io.faststream.query.util.Multimap;

@SuppressWarnings({ "rawtypes", "unchecked" })
class SerialMultimapMapViewProcessor extends AbstractSerialArrayViewProcessor<MutableMultimapEntry> implements
MultimapViewVisitable.MultimapViewVisitor {

    public SerialMultimapMapViewProcessor(MutableMultimapEntry[] e) {
        super(e, true);
    }

    public void any(UV_Any node) {
        Map.Entry[] e = new SimpleImmutableEntry[hi - lo];
        for (int i = lo; i < hi; i++) {
            MutableMultimapEntry entry = a[i];
            e[i - lo] = new SimpleImmutableEntry(entry.key, entry.values[entry.offset]);
        }
        setNext(new SerialMapViewProcessor(e));
    }

    public void count(UV_Count node) {
        Map.Entry[] e = new SimpleImmutableEntry[hi - lo];
        for (int i = lo; i < hi; i++) {
            e[i - lo] = new SimpleImmutableEntry(a[i].key, Long.valueOf(a[i].size()));
        }
        setNext(new SerialMapViewProcessor(e));
    }

    private void filter(BiPredicate<? super Object, ? super Object> predicate) {
        int adjustment = 0;
        for (int i = lo; i < hi; i++) {
            MutableMultimapEntry e = a[i];
            e.filter(predicate);
            if (e.isEmpty()) {
                adjustment++;
            } else if (adjustment > 0) {
                a[i - adjustment] = e; // swap entry down
            }
        }
        if (adjustment > 0) {
            // fill out with blanks
            for (int i = hi - adjustment; i < hi; i++) {
                a[i] = null;
            }
            hi -= adjustment;
        }
    }

    public void filter(UV_Filter node) {
        filter(node.getBiPredicate());
    }

    @Override
    public void filterNullValues(UV_FilterNullValues node) {
        filter((k, v) -> v != null);
    }

    private void filterOnKey(Predicate<? extends Object> predicate) {
        filter((k, v) -> ((Predicate) predicate).test(k));
    }

    @Override
    public void filterOnKey(UV_FilterOnKey node) {
        filterOnKey(node.getPredicate());
    }

    private void filterOnKeyType(Class<?> type) {
        filterOnKey(e -> type.isInstance(e));
    }

    @Override
    public void filterOnKeyType(UV_FilterOnKeyType node) {
        filterOnKeyType(node.getType());
    }

    @Override
    public void filterOnValue(UV_FilterOnValue node) {
        filterOnValue(node.getPredicate());
    }

    private void filterOnValue(Predicate<? extends Object> predicate) {
        filter((k, v) -> ((Predicate) predicate).test(v));
    }

    private void filterOnValueType(Class<?> type) {
        filterOnValue(e -> type.isInstance(e));
    }

    @Override
    public void filterOnValueType(UV_FilterOnValueType node) {
        filterOnValueType(node.getType());
    }

    public void head(UV_Head node) {
        Map.Entry[] e = new SimpleImmutableEntry[hi - lo];
        for (int i = lo; i < hi; i++) {
            MutableMultimapEntry entry = a[i];
            e[i - lo] = new SimpleImmutableEntry(entry.key, entry.values[entry.offset]);
        }
        setNext(new SerialMapViewProcessor(e));
    }

    public void isEmpty(UVT_IsEmpty node) {
        isEmpty();
    }

    public void keys(UV_Keys node) {
        Object[] o = new Object[hi - lo];
        for (int i = lo; i < hi; i++) {
            o[i - lo] = a[i].key;
        }
        setNext(SerialCollectionViewProcessor.createUsingHandoff(o));
    }

    public void map(UV_Map node) {
        map(node.getMapper());
    }

    private void map(BiFunction<? super Object, ? super Object, ?> mapper) {
        int size = 0;
        for (int i = lo; i < hi; i++) {
            size += a[i].size();
        }
        Object[] newData = new Object[size];
        int offset = 0;
        for (int i = lo; i < hi; i++) {
            MutableMultimapEntry e = a[i];
            for (int j = e.offset; j < e.length; j++) {
                newData[offset++] = mapper.apply(e.key, e.values[j]);
            }
        }
        setNext(SerialCollectionViewProcessor.createUsingHandoff(newData));
    }

    public void mapKey(UV_MapKey node) {
        mapKey(node.getMapper());
    }

    public void mapKey(Function mapper) {
        HashMap map = new HashMap<>();
        for (int i = lo; i < hi; i++) {
            MutableMultimapEntry o = a[i];
            Object newKey = mapper.apply(o.key);
            Object prev = map.get(newKey);
            if (prev == null) {
                map.put(newKey, o);
            } else {
                if (prev instanceof ArrayList) {
                    ((ArrayList) prev).add(o);
                } else {
                    ArrayList list = new ArrayList(2);
                    list.add(prev);
                    list.add(o);
                    map.put(newKey, list);
                }
            }
        }
        int i = 0;
        for (Map.Entry e : (Set<Map.Entry>) map.entrySet()) {
            Object key = e.getKey();
            Object value = e.getValue();
            if (value instanceof ArrayList) {
                ArrayList<MutableMultimapEntry> list = (ArrayList<MutableMultimapEntry>) value;
                int size = 0;
                for (MutableMultimapEntry me : list) {
                    size += me.size();
                }
                Object[] newData = new Object[size];
                int offset = 0;
                for (MutableMultimapEntry me : list) {
                    offset = me.addValuesTo(newData, offset);
                }
                a[i++] = new MutableMultimapEntry(key, newData);
            } else {
                MutableMultimapEntry me = (MutableMultimapEntry) value;
                me.key = key;
                a[i++] = me;
            }
        }
        lo = 0;
        hi = i;
    }

    public void mapValue(UV_MapValue node) {
        mapValue(node.getMapper());
    }

    private void mapValue(Function<? super Object, ?> op) {
        for (int i = lo; i < hi; i++) {
            MutableMultimapEntry e = a[i];
            Object[] data = e.values;
            int length = e.length;
            for (int j = e.offset; j < length; j++) {
                data[j] = op.apply(data[j]);
            }
        }
    }

    public void multimapTake(long elementsToTake) {
        super.take(elementsToTake);
    }

    public MutableMultimapEntry[] newArray(int size) {
        return new MutableMultimapEntry[size];
    }

    public void one(UV_One node) {
        Map.Entry[] e = new SimpleImmutableEntry[hi - lo];
        for (int i = lo; i < hi; i++) {
            MutableMultimapEntry entry = a[i];
            if (entry.size() != 1) {
                throw new IllegalStateException("More than one value exists for key [key= " + entry.key + ", values= "
                        + entry.valuesToString() + "]");
            }
            e[i - lo] = new SimpleImmutableEntry(entry.key, entry.values[entry.offset]);
        }
        setNext(new SerialMapViewProcessor(e));
    }

    private void sortedKeys(final Comparator comparator) {
        sort(new Comparator<MutableMultimapEntry>() {
            public int compare(MutableMultimapEntry o1, MutableMultimapEntry o2) {
                return comparator.compare(o1.key, o2.key);
            }
        });
    }

    @Override
    public void sortedKeysAscending(UV_SortedKeysAscending node) {
        sortedKeys(Comparator.naturalOrder());
    }

    public void sortedKeys(UV_SortedKeys node) {
        sortedKeys(node.getComparator());
    }

    @Override
    public void sortedKeysDescending(UV_SortedKeysDescending node) {
        sortedKeys(Comparator.reverseOrder());
    }

    private void sortedValues(Comparator comparator) {
        for (int i = lo; i < hi; i++) {
            a[i].sort(comparator);
        }
    }

    @Override
    public void sortedValuesAscending(UV_SortedValuesAscending node) {
        sortedValues(Comparator.naturalOrder());
    }

    public void sortedValues(UV_SortedValues node) {
        sortedValues(node.getComparator());
    }

    @Override
    public void sortedValuesDescending(UV_SortedValuesDescending node) {
        sortedValues(Comparator.reverseOrder());
    }

    public void process(QueryOperationNode node) {
        ((MultimapViewVisitable) node).accept(this);
    }

    public void reduce(UV_Reduce node) {
        reduce(node.getReducer());
    }

    public void reduce(BinaryOperator<? super Object> reducer) {
        Map.Entry[] entries = new Map.Entry[hi - lo];
        for (int i = lo; i < hi; i++) {
            MutableMultimapEntry me = a[i];
            entries[i - lo] = new SimpleImmutableEntry(me.getKey(), me.reduce(reducer));
        }
        setNext(new SerialMapViewProcessor(entries));
    }

    public void size(UVT_Size node) {
        setResult(totalSize());
    }

    public void tail(UV_Tail node) {
        Map.Entry[] e = new SimpleImmutableEntry[hi - lo];
        for (int i = lo; i < hi; i++) {
            MutableMultimapEntry entry = a[i];
            e[i - lo] = new SimpleImmutableEntry(entry.key, entry.values[entry.length - 1]);
        }
        setNext(new SerialMapViewProcessor(e));
    }

    public void take(UV_Take node) {
        take(node.getNumberOfKeysToTake());
    }

    public void takeValues(long elementsToTake) {
        for (int i = lo; i < hi; i++) {
            a[i].take(elementsToTake);
        }
    }

    public void takeValues(UV_TakeValues node) {
        takeValues(node.getNumberOfValuesToTake());

    }

    private void to(Class<?> type) {
        setResult(MutableMultimapEntry.from(/* type, */a, lo, hi));
    }

    public void to(UVT_To node) {
        to(node.getType());
    }

    private long totalSize() {
        long size = 0;
        for (int i = lo; i < hi; i++) {
            MutableMultimapEntry e = a[i];
            size += e.size();
        }
        return size;
    }

    public void unique(UV_Unique node) {
        for (int i = lo; i < hi; i++) {
            a[i].unique();
        }
    }

    public void values(UV_Values node) {
        int size = (int) totalSize();
        Object[] o = new Object[size];
        int k = 0;
        for (int i = lo; i < hi; i++) {
            MutableMultimapEntry e = a[i];
            Object[] data = e.values;
            int length = e.length;
            for (int j = e.offset; j < length; j++) {
                o[k++] = data[j];
            }
        }
        if (size != k) {
            throw new Error();
        }
        setNext(SerialCollectionViewProcessor.createUsingHandoff(o));
    }

    /** {@inheritDoc} */
    @Override
    public void toMultimap(UVT_ToMultimap node) {
        to(Multimap.class);
    }

}
