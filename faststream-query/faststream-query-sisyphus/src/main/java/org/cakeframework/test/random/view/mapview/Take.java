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

import static io.faststream.sisyphus.util.MoreAsserts.assertMapIsMapSubset;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.cakeframework.util.view.MapView;

import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link MapView#take(long)} operation.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class Take<K, V> extends AbstractMapViewRandomTestCase<K, V> {

    @RndTest
    public void take() {
        long count = randomTakeCount(expected().size());
        if (isOrdered()) {
            @SuppressWarnings("unchecked")
            Map.Entry<K, V>[] entries = expected().entrySet().toArray(new Map.Entry[0]);
            LinkedHashMap<K, V> m = new LinkedHashMap<>();
            if (count > 0) {
                for (int i = 0; i < (count > entries.length ? entries.length : (int) count); i++) {
                    m.put(entries[i].getKey(), entries[i].getValue());
                }
            } else {
                int startIndex = (count + entries.length < 0) ? 0 : (int) (count + entries.length);
                for (int i = startIndex; i < entries.length; i++) {
                    m.put(entries[i].getKey(), entries[i].getValue());
                }
            }
            setNext(m, actual().take(count), isOrdered());
        } else {
            Map<K, V> l = actual().take(count).toMap();
            if (count > 0) {
                if (count >= expected().size()) {
                    assertEquals(expected().asMap(), l);
                } else {
                    assertEquals(count, l.size());
                    assertMapIsMapSubset(expected().asMap(), l);
                }
            } else {
                if (count <= -expected().size()) {
                    assertEquals(expected().asMap(), l);
                } else {
                    assertEquals(count, -l.size());
                    assertMapIsMapSubset(expected().asMap(), l);
                }
            }
        }
    }

}
