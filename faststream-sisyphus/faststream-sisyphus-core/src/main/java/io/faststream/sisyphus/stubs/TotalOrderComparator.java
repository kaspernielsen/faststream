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
package io.faststream.sisyphus.stubs;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A versatile comparator that defines an total order on all elements (in the same JVM) except identical elements. The
 * comparator works even with elements that are not mutually comparable.
 * 
 * @param <T>
 *            the type of objects that may be compared by this comparator
 * 
 * @author Kasper Nielsen
 */
public final class TotalOrderComparator<T> implements Comparator<T> {

    @SuppressWarnings("rawtypes")
    public static final TotalOrderComparator COMPARATOR = new TotalOrderComparator(false);

    @SuppressWarnings("rawtypes")
    static final TotalOrderComparator NO_IDENTITY_COMPARATOR = new TotalOrderComparator(false);

    /** Use for id'ing equivalent (but non-identical) object that has the same System.identityHashCode(). */
    static final AtomicLong SYSTEM_HASHCODE_COLLISION_COUNTER = new AtomicLong();

    // Nice try but turned out to be a bad idea in practice.
    // The objects that are in expected and in actual are not necessarily the same
    // in which case it makes no sense
    // private static final LoadingCache<Object, Long> SYSTEM_HASHCODE_COLLISION = CacheBuilder.newBuilder().weakKeys()
    // .build(new CacheLoader<Object, Long>() {
    // public Long load(Object ignore) {
    // return new Long(SYSTEM_HASHCODE_COLLISION_COUNTER.incrementAndGet());
    // }
    // });

    private final boolean useObjectIdentity;

    private TotalOrderComparator(boolean useObjectIdentity) {
        this.useObjectIdentity = useObjectIdentity;
    }

    @Override
    public int compare(T o1, T o2) {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null || o2 == null) {
            return o1 == null ? 1 : -1;
        }
        Class<?> o1c = o1.getClass();
        Class<?> o2c = o2.getClass();
        if (o1c == o2c) {
            return compareSameType(o1c, o1, o2);
        } else {
            // We use toString() representation instead of the class this should be
            // stable across invocations
            int diff = o1c.toString().compareTo(o2c.toString());
            // System.hashCode is pretty bad on some platforms, so we might get collision
            // In which case we find a unique id in CLASS_HASHCODE_COLLISION
            if (diff == 0) {
                // HMM different name, but same digg, anonymous class??
                throw new Error(o1c.toString() + ", " + o2c.toString().hashCode());
            }
            return diff;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private int compareSameType(Class<?> type, Object o1, Object o2) {
        if (Comparable.class.isAssignableFrom(type)) {
            return checkReturn(((Comparable) o1).compareTo(o2), o1, o2);
        } else if (Map.Entry.class.isAssignableFrom(type)) {
            Map.Entry<?, ?> me1 = (Map.Entry<?, ?>) o1;
            Map.Entry<?, ?> me2 = (Map.Entry<?, ?>) o2;
            int diff = compareTwo(me1.getKey(), me2.getKey(), me1.getValue(), me2.getValue());
            return checkReturn(diff, o1, o2);
        } else {
            throw new Error("Dont know anything about type " + type);
        }
    }

    private int checkReturn(int diff, Object o1, Object o2) {
        if (diff == 0 && useObjectIdentity) {
            int h1 = System.identityHashCode(o1);
            int h2 = System.identityHashCode(o2);
            if (h1 != h2) {
                return h1 - h2;
            }
            throw new Error();
            // return SYSTEM_HASHCODE_COLLISION.getUnchecked(o1) - SYSTEM_HASHCODE_COLLISION.getUnchecked(o2) > 0 ? 0 :
            // 1;
        }
        return diff;
    }

    @SuppressWarnings("unchecked")
    public static <V> Comparator<V> instance() {
        return COMPARATOR;
    }

    @SuppressWarnings("unchecked")
    public static int compareMapEntry(Map.Entry<?, ?> e1, Map.Entry<?, ?> e2, boolean keyFirst) {
        if (e1 == e2) {
            return 0;
        } else if (e1 == null || e2 == null) {
            return e1 == null ? 1 : -1;
        }
        // we dont use system hashcode at first, maybe values are different
        Object o1 = keyFirst ? e1.getKey() : e1.getValue();
        Object o2 = keyFirst ? e2.getKey() : e2.getValue();
        int diff = NO_IDENTITY_COMPARATOR.compare(o1, o2);
        if (diff == 0) {
            diff = COMPARATOR.compare(keyFirst ? e1.getValue() : e1.getKey(), keyFirst ? e2.getValue() : e2.getKey());
            if (diff == 0) {
                diff = COMPARATOR.compare(o1, o2);
                if (diff == 0) {
                    return COMPARATOR.checkReturn(diff, e1, e2);
                }
            }
        }
        return diff;
    }

    @SuppressWarnings("unchecked")
    public static <F, S> int compareTwo(F first1, F first2, S second1, S second2) {
        // We resort to IDENTITY comparison only as a last resort if BOTH first and second are equals
        int diff = NO_IDENTITY_COMPARATOR.compare(first1, first2);
        if (diff == 0) {
            if ((diff = NO_IDENTITY_COMPARATOR.compare(second1, second2)) == 0) {
                diff = COMPARATOR.checkReturn(diff, first1, first2);
                diff = COMPARATOR.checkReturn(diff, second1, second2);
            }
        }
        return diff;
    }

    public String toString() {
        return "TotalOrderComparator.COMPARATOR";// : "TotalOrderComparator.NO_IDENTITY_COMPARATOR";
    }
}
