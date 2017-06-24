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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.faststream.query.util.view.MultimapView;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link MultimapView#count()} operation.
 * 
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class Count<K, V> extends AbstractMultimapViewRandomTestCase<K, V> {

    @RndTest
    public void count() {
        Map<K, Long> map = new HashMap<>();
        for (Entry<K, ? extends Collection<V>> e : expected().asMap().entrySet()) {
            map.put(e.getKey(), Long.valueOf(e.getValue().size()));
        }
        // Count does not preserve order
        setNext(map, actual().count(), false);
    }
}
