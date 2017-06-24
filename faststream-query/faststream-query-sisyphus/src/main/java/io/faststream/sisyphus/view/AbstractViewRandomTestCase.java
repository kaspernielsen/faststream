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
package io.faststream.sisyphus.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.faststream.sisyphus.view.spi.ExpectedCollectionView;
import org.faststream.sisyphus.view.spi.ExpectedMapView;
import org.faststream.sisyphus.view.spi.ExpectedMultimapView;

import io.faststream.query.util.Multimap;
import io.faststream.query.util.view.CollectionView;
import io.faststream.query.util.view.MapView;
import io.faststream.query.util.view.MultimapView;
import io.faststream.sisyphus.expected.Expected;
import io.faststream.sisyphus.spi.TestCase;

/**
 *
 * @param <E>
 *            the expected result
 * @param <A>
 *            the actual result
 *
 * @author Kasper Nielsen
 */
public abstract class AbstractViewRandomTestCase<E extends Expected, A> extends TestCase<E, A> {

    public <T> void setNext(List<T> expected, CollectionView<T> actual, boolean isOrdered) {
        setNested(new ExpectedCollectionView<>(expected, isOrdered), actual);
    }

    public <T> void setNested(ExpectedCollectionView<T> expected, CollectionView<T> actual) {
        nested(expected, actual);
    }

    public <K, V> void setNext(ExpectedMapView<K, V> expected, MapView<K, V> actual) {
        nested(expected, actual);
    }

    public <K, V> void setNext(Map<K, V> expected, MapView<K, V> actual, boolean isOrdered) {
        setNext(new ExpectedMapView<>(expected, isOrdered), actual);
    }

    public <K, V> void setNext(Multimap<K, V> expected, MultimapView<K, V> actual, boolean isKeysOrdered,
            boolean isValuesOrdered) {
        setNext(new ExpectedMultimapView<>(expected, isKeysOrdered, isValuesOrdered), actual);
    }

    public <K, V> void setNext(ExpectedMultimapView<K, V> expected, MultimapView<K, V> actual) {
        nested(expected, actual);
    }

    /** Special values for which the take() operation might fail. */
    static long[] TAKE_SPECIAL_VALUES = new long[] { Long.MAX_VALUE, Long.MAX_VALUE - 1L, Long.MAX_VALUE + 1L,
            Integer.MAX_VALUE, Integer.MAX_VALUE + 1L, Integer.MAX_VALUE - 1L, Integer.MIN_VALUE,
        Integer.MIN_VALUE + 1L, Integer.MIN_VALUE - 1L };

    /** The default list of types used when testing. */
    static final List<Class<? extends Number>> TYPES = Arrays.<Class<? extends Number>> asList(AtomicInteger.class,
            AtomicLong.class, Byte.class, Double.class, Float.class, Integer.class, Long.class, Short.class);

    /**
     * Select a random parameter for the {@link CollectionView#take(long)}, {@link MapView#take(long)},
     * {@link MultimapView#take(long)} or {@link MultimapView#takeValues(long)} operation.
     * <p>
     * The idea is to find a value that exposes +1 errors and corner cases.
     *
     * @param elementCount
     *            the number of expected elements in the current
     * @return the parameter for the take command
     */
    public long randomTakeCount(int elementCount) {
        double d = random().nextDouble();
        if (d < .2) {
            return TAKE_SPECIAL_VALUES[random().nextInt(0, TAKE_SPECIAL_VALUES.length)];
        } else if (d < .25) {
            return elementCount + 1;
        } else if (d < .3) {
            return Math.max(1, elementCount);
        } else if (d < .35) {
            return Math.max(1, elementCount - 1);
        } else if (d < .4) {
            return Math.min(-1, -elementCount + 1);
        } else if (d < .45) {
            return Math.min(-1, -elementCount);
        } else if (d < .5) {
            return Math.min(-1, -elementCount - 1);
        } else {
            // a random integer between -elementCount and elementCount
            return random().nextInt(1, Math.max(2, elementCount)) * (random().nextBoolean() ? 1 : -1);
        }
    }

    public Class<?> randomType(Iterable<?> expected) {
        // We are going to specify a type of an element that already exists with 95 %
        if (random().nextDouble() < .95) {
            List<Class<?>> l = new ArrayList<>(getTypesOf(expected));
            if (l.size() > 0) {
                Collections.shuffle(l, random());
                Class<?> c = l.iterator().next();
                if (Map.Entry.class.isAssignableFrom(c)) {
                    return Map.Entry.class;
                }
                return c;
            }
        }
        // just return are random type, there might be no elements of the specified type
        return randomTypes()[0];
    }

    private static <T> Set<Class<?>> getTypesOf(Iterable<T> objects) {
        LinkedHashSet<Class<?>> set = new LinkedHashSet<>();
        for (T t : objects) {
            if (t != null) {
                set.add(t.getClass());
            }
        }
        return set;
    }

    /**
     * @return an array of random types from {@link #TYPES}. The size of the array is between <tt>1</tt> and
     *         <tt>TYPES.length</tt>.
     */
    @SuppressWarnings("unchecked")
    Class<? extends Number>[] randomTypes() {
        List<Class<? extends Number>> l = new ArrayList<>(TYPES);
        Collections.shuffle(l, random());
        return l.subList(0, random().nextInt(1, l.size())).toArray(new Class[0]);
    }
}
