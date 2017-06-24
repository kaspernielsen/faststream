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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.faststream.query.util.view.MultimapView;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link MultimapView#any()} operation.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class AnyHeadTailOne<K, V> extends AbstractMultimapViewRandomTestCase<K, V> {

    @RndTest
    public void any() {
        if (expected().size() == expected().keySet().size()) {
            // There is exactly one value per key. So we just reuse the one() tests
            one();
        } else {
            // Cant really continue testing because, an implementation is free to pick whatever element for any key
            // and we have no idea which element it picks
            Map<K, V> map = actual().any().toMap();
            assertEquals(expected().keySet().size(), map.size());
            for (Map.Entry<K, V> entry : map.entrySet()) {
                Collection<V> col = expected().asMap().get(entry.getKey());
                if (!col.contains(entry.getValue())) {
                    fail("Returned mapview for multimapView.any/head/tail() that is not corrent, mapView=" + map
                            + ", multiMapView" + actual().toMultimap());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @RndTest
    public void head() {
        if (isValuesOrdered()) {
            Map<K, V> map = isKeysOrdered() ? new LinkedHashMap<K, V>() : new HashMap<K, V>();
            for (Entry<K, Collection<V>> n : expected().asMap().entrySet()) {
                map.put(n.getKey(), n.getValue().iterator().next());
            }
            setNext(map, actual().head(), isKeysOrdered());
        } else {
            one();
        }
    }

    @RndTest
    public void one() {
        if (expected().size() == expected().keySet().size()) {
            HashMap<K, V> map = new HashMap<>();
            for (Map.Entry<K, V> e : expected().entries()) {
                map.put(e.getKey(), e.getValue());
            }
            setNext(map, actual().one(), false);// TODO keep ordered status?????
        } else {
            try {
                actual().one().toMap();
                fail("multimapView.one() should throw IllegalStateException when view does not contain exactly 1 value for all keys, elements in view= "
                        + actual().toMultimap());
            } catch (IllegalStateException ok) {}
        }
    }

    @SuppressWarnings("unused")
    @RndTest
    // Its called one to override super method
    public void tail() {
        if (isValuesOrdered()) {
            Map<K, V> map = isKeysOrdered() ? new LinkedHashMap<K, V>() : new HashMap<K, V>();
            for (Entry<K, Collection<V>> n : expected().asMap().entrySet()) {
                List<V> l = (List<V>) n.getValue();
                map.put(n.getKey(), l.get(l.size() - 1));
            }
            setNext(map, actual().tail(), isKeysOrdered());
        } else {
            one();
        }
    }
}
