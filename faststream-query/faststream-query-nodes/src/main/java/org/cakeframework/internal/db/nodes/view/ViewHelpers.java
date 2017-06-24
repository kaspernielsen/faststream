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
package org.cakeframework.internal.db.nodes.view;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Kasper Nielsen
 */
public class ViewHelpers {
    public static final Set<Class<?>> MAP_VIEW_ALLOWED_CLASSES = Collections.unmodifiableSet(new HashSet<>(Arrays
            .asList(Map.class, HashMap.class, ConcurrentHashMap.class, LinkedHashMap.class)));

    public static final Set<Class<?>> COLLECTION_VIEW_ALLOWED_CLASSES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(List.class, Collection.class, Iterable.class, Deque.class, ArrayDeque.class,
                    LinkedList.class, ArrayList.class)));

    public static <T> Class<T> collectionViewCheckArgument(Class<T> toType) {
        if (!COLLECTION_VIEW_ALLOWED_CLASSES.contains(toType)) {
            if (toType.isArray()) {
                Class<?> componentType = toType.getComponentType();
                if (componentType.isPrimitive()) {
                    throw new IllegalArgumentException("Creation of primitive arrays of type " + toType.getSimpleName()
                            + "[] is not supported");
                }
            } else {
                throw new IllegalArgumentException(
                        "Do not know how to create instances of "
                                + toType.getName()
                                + ". The following types are supported: non-primitive array types (for example, String[] or Object[]), "
                                + "List, Collection, Iterable, Deque, ArrayDeque, LinkedList, ArrayList");
            }
        }
        return toType;
    }

    public static Object collectionFrom(Class<?> type, Collection<Object> list) {
        Object[] o = list.toArray();
        return ViewHelpers.collectionFrom(type, o, 0, o.length, true);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object collectionFrom(Class<?> type, Object[] array, int start, int stop, boolean isSafe) {
        // System.err.println(type);
        if (type == Object[].class) {
            if (!isSafe || array.getClass() != Object[].class || start != 0 || stop != array.length) {
                array = Arrays.copyOfRange(array, start, stop, Object[].class);
            }
            return array;
        } else if (type == List.class || type == Collection.class || type == Iterable.class) {
            if (!isSafe || start != 0 || stop != array.length) {
                array = Arrays.copyOfRange(array, start, stop, Object[].class);
            }
            return Arrays.asList(array);
        } else if (type.isArray()) {
            Class componentType = type.getComponentType();
            if (!isSafe || array.getClass() != componentType || start != 0 || stop != array.length) {
                // System.err.println(Arrays.toString(array));
                // System.err.println(array);
                // System.err.println(type);
                // for (int i = start; i < stop; i++) {
                // System.err.println(array[i].getClass());
                // }
                try {
                    array = Arrays.copyOfRange(array, start, stop, (Class) type);
                } catch (ArrayStoreException e) {
                    for (int i = start; i < stop; i++) {
                        System.err.println(array[i].getClass());
                    }
                    System.err.println(componentType);
                    System.err.println(type);
                    throw e;
                }
            }
            return array;
        } else if (type == ArrayDeque.class) {
            ArrayDeque<Object> result = new ArrayDeque<>(stop - start);
            for (int i = start; i < stop; i++) {
                result.add(array[i]);
            }
            return result;
        } else if (type == Deque.class || type == LinkedList.class) {
            LinkedList<Object> result = new LinkedList<>();
            for (int i = start; i < stop; i++) {
                result.add(array[i]);
            }
            return result;
        } else if (type == ArrayList.class) {
            ArrayList<Object> result = new ArrayList<>(stop - start);
            for (int i = start; i < stop; i++) {
                result.add(array[i]);
            }
            return result;
        }
        throw new Error();
    }

    public static Object mapFrom(Class<?> toType, Map<?, ?> map) {
        Entry<?, ?>[] e = map.entrySet().toArray(new Map.Entry[map.size()]);
        return mapFrom(toType, e, 0, e.length);
    }

    public static Object mapFrom(Class<?> toType, Entry<?, ?>[] entries, int start, int stop) {
        final Map<Object, Object> map;
        if (toType == ConcurrentHashMap.class) {
            map = new ConcurrentHashMap<>();
        } else {
            map = toType == HashMap.class ? new HashMap<>() : new LinkedHashMap<>();
        }
        for (int i = start; i < stop; i++) {
            Map.Entry<?, ?> e = entries[i];
            map.put(e.getKey(), e.getValue());
        }
        return map;
    }

    public static <T> Class<T> mapViewCheckArgument(Class<T> toType) {
        if (!MAP_VIEW_ALLOWED_CLASSES.contains(toType)) {
            throw new IllegalArgumentException("Do not know how to create instances of " + toType.getName()
                    + ". The following types are supported: " + "Map, HashMap, ConcurrentHashMap, LinkedHashMap");
        }
        return toType;
    }
}
