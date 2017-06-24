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
package io.faststream.sisyphus.view.multimapview;

import java.util.function.BiConsumer;

import io.faststream.query.util.Multimap;
import io.faststream.query.util.view.MapView;
import io.faststream.query.util.view.MultimapView;
import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link MapView#apply(BiConsumer)} operation.
 * <p>
 * Basic idea is to throw to all arguments to {@link BiConsumer#accept(Object, Object)} into a concurrent queue and
 * compare with expected contents.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class To<K, V> extends AbstractMultimapViewRandomTestCase<K, V> {
    /** These are the legal values for {@link MultimapView#to(Class)}. */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final Class<? extends Multimap>[] MULTIMAP_VIEW_TO_LEGAL_TYPES = new Class[] { Multimap.class,
    /* HashMultimap.class, LinkedHashMultimap.class */};

    Class<?> getTarget() {
        return MULTIMAP_VIEW_TO_LEGAL_TYPES[random().nextInt(MULTIMAP_VIEW_TO_LEGAL_TYPES.length)];
    }

    @RndTest
    public void to() {
        @SuppressWarnings("unchecked")
        Multimap<K, V> col = (Multimap<K, V>) actual().to(getTarget());
        // TODO fix this when we have our own multimap
        // if (rnd.isInterface()) {
        // assertTrue(rnd.isInstance(col));
        // } else {
        // assertSame(rnd, col.getClass());
        // }
        assertMultimapEquals(expected().asMultimap(), col, isKeysOrdered(), isValuesOrdered());
        //
        // Class<?> type = getTarget( expected);
        // Object result = actual.to(type);
        //
        // if (type.isInterface()) {
        // assertTrue(type.isInstance(result));
        // } else {
        // assertSame(type, result.getClass());
        // }
        // if (result instanceof Map && isOrdered() && type != HashMap.class) {// HashMap is never ordered
        // assertIterableEquals(expected.entrySet(), ((Map<K, V>) result).entrySet());
        // } else {
        // assertEquals(expected, result);// ??
        // }
    }

   @FailWith(NullPointerException.class)
    public void toNullArgument() {
        actual().to(null);
    }

    @RndTest
    public void toMultimap() {
        assertMultimapEquals(expected().asMultimap(), actual().toMultimap(), isKeysOrdered(), isValuesOrdered());
    }
}
