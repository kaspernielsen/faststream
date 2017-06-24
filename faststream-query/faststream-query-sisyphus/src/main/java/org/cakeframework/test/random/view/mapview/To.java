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
package org.cakeframework.test.random.view.mapview;

import static io.faststream.sisyphus.util.MoreAsserts.assertIterableEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.cakeframework.util.view.MapView;

import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link MapView#to(Class)} operation.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class To<K, V> extends AbstractMapViewRandomTestCase<K, V> {
    /** These are the legal values for {@link MapView#to(Class)}. */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final Class<? extends Map>[] MAP_VIEW_TO_LEGAL_TYPES = new Class[] { HashMap.class,
        LinkedHashMap.class, Map.class };

    Class<?> getTarget() {
        return MAP_VIEW_TO_LEGAL_TYPES[random().nextInt(MAP_VIEW_TO_LEGAL_TYPES.length)];
    }

    @SuppressWarnings("unchecked")
    @RndTest
    public void to() {
        Class<?> type = getTarget();
        Object result = actual().to(type);

        if (type.isInterface()) {
            assertTrue(type.isInstance(result));
        } else {
            assertSame(type, result.getClass());
        }
        if (result instanceof Map && isOrdered() && type != HashMap.class) {// HashMap is never ordered
            assertIterableEquals(expected().entrySet(), ((Map<K, V>) result).entrySet());
        } else {
            assertEquals(expected().asMap(), result);// ??
        }
    }

    @RndTest
    public void toMap() {
        if (isOrdered()) {
            assertIterableEquals(expected().entrySet(), new ArrayList<>(actual().toMap().entrySet()));
        } else {
            assertEquals(expected().asMap(), actual().toMap());
        }
    }
}
