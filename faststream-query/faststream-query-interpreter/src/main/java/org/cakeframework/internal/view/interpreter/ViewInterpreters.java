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
package org.cakeframework.internal.view.interpreter;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.cakeframework.internal.db.nodes.view.collectionview.AbstractCollectionView;
import org.cakeframework.internal.db.nodes.view.mapview.AbstractMapView;
import org.cakeframework.internal.db.query.node.AbstractTerminalQueryOperationProcessor;
import org.cakeframework.internal.db.query.node.TerminalQueryOperationNode;
import org.cakeframework.internal.db.query.node.TerminalQueryOperationNodeProcessor;
import org.cakeframework.util.view.CollectionView;
import org.cakeframework.util.view.MapView;

/**
 *
 * @author Kasper Nielsen
 */
public class ViewInterpreters {

    /**
     * Creates a new {@link MapView} from the specified map.
     *
     * @param <K>
     *            the type of keys
     * @param <V>
     *            the type of values
     *
     * @param map
     *            the elements for the view
     * @return the new collection view
     */
    public static <K, V> MapView<K, V> createMapView(Map<K, V> map) {
        return AbstractMapView.from(createMapViewProcessor(map));
    }

    /**
     * Creates a new collection view from the specified array.
     *
     * @param <E>
     *            the type of elements
     *
     * @param array
     *            the elements for the view
     * @return the new collection view
     */
    public static <E> CollectionView<E> createCollectionView(E[] array) {
        return AbstractCollectionView.from(createCollectionViewProcessor(array));
    }

    /**
     * Creates a new collection view from the specified iterable.
     *
     * @param <E>
     *            the type of elements
     * @param iterable
     *            the elements for the view
     * @return the new collection view
     */
    public static <E> CollectionView<E> createCollectionView(Iterable<?> iterable) {
        return AbstractCollectionView.from(createCollectionViewProcessor(iterable));
    }

    public static TerminalQueryOperationNodeProcessor createCollectionViewProcessor(final Object[] array) {
        requireNonNull(array, "array is null");
        return new AbstractTerminalQueryOperationProcessor() {
            @Override
            public Object process(TerminalQueryOperationNode topNode) {
                return SerialCollectionViewProcessor.createUsingCopy(array).run(topNode);
            }
        };
    }

    public static TerminalQueryOperationNodeProcessor createCollectionViewProcessor(Iterable<?> iterable) {
        requireNonNull(iterable, "iterable is null");
        return new AbstractTerminalQueryOperationProcessor() {
            @Override
            public Object process(TerminalQueryOperationNode topNode) {
                return SerialCollectionViewProcessor.createUsingHandoff(convertIterableToArray(iterable)).run(topNode);
            }
        };
    }

    public static TerminalQueryOperationNodeProcessor createMapViewProcessor(final Map<?, ?> map) {
        requireNonNull(map, "map is null");
        return new AbstractTerminalQueryOperationProcessor() {
            @Override
            public Object process(TerminalQueryOperationNode topNode) {
                return new SerialMapViewProcessor(map).run(topNode);
            }
        };
    }

    static Object[] convertIterableToArray(Iterable<?> iterable) {
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).toArray();
        }
        ArrayList<Object> al = new ArrayList<>();
        for (Object o : iterable) {
            al.add(o);
        }
        return al.size() == 0 ? new Object[0] : al.toArray();
    }
}
