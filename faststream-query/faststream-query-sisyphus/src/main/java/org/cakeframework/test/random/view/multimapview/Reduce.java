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
package org.cakeframework.test.random.view.multimapview;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;

import org.cakeframework.util.view.MultimapView;

import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.stubs.DefaultBinaryOperator;

/**
 * Test the {@link MultimapView#reduce(BinaryOperator)} operation.
 * 
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class Reduce<K, V> extends AbstractMultimapViewRandomTestCase<K, V> {

    @SuppressWarnings("unused")
    @RndTest
    public void reduce() {
        Map<K, V> map = isKeysOrdered() ? new LinkedHashMap<K, V>() : new HashMap<K, V>();
        BinaryOperator<V> reducer = DefaultBinaryOperator.reducer();
        for (Entry<K, Collection<V>> e : expected().asMap().entrySet()) {
            Iterator<V> iter = e.getValue().iterator();
            V reduced = iter.next();
            while (iter.hasNext()) {
                reduced = reducer.apply(reduced, iter.next());
            }
            map.put(e.getKey(), reduced);
        }
        setNext(map, actual().reduce(reducer), isKeysOrdered());
    }

   @FailWith(NullPointerException.class)
    public void reduceNullArgument() {
        actual().reduce(null);
    }
}
