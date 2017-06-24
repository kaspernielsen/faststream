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
package io.faststream.sisyphus.javautil.map;

import java.util.EnumSet;
import java.util.Map;

import io.faststream.sisyphus.annotations.CustomWeight;
import io.faststream.sisyphus.annotations.LifecycleTestMethod;
import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.javautil.CollectionRandomTestBuilder;
import io.faststream.sisyphus.javautil.MapRandomTestBuilder;
import io.faststream.sisyphus.javautil.collection.ExpectedCollection;

/**
 * A test step for {@link Map#entrySet()}.
 * 
 * @param <K>
 *            the type of keys we test
 * @param <V>
 *            the type of values we test
 * @author Kasper Nielsen
 */
public class EntrySet<K, V> extends AbstractRandomMapTestCase<K, V> {

    @RndTest
    @LifecycleTestMethod(false)
    @CustomWeight(0.01)
    public void keySet() {
        EnumSet<CollectionRandomTestBuilder.NonStandardOption> set = EnumSet.of(CollectionRandomTestBuilder.NonStandardOption.ADD_UNSUPPORTED);
        if (expected().nonStandardOptions.contains(MapRandomTestBuilder.NonStandardOption.NULL_VALUES_UNSUPPORTED)
                || expected().nonStandardOptions.contains(MapRandomTestBuilder.NonStandardOption.NULL_KEYS_UNSUPPORTED)) {
            set.add(CollectionRandomTestBuilder.NonStandardOption.NULL_UNSUPPORTED);
        }
        if (expected().nonStandardOptions.contains(MapRandomTestBuilder.NonStandardOption.NULL_QUERIES_UNSUPPORTED)) {
            set.add(CollectionRandomTestBuilder.NonStandardOption.NULL_QUERIES_UNSUPPORTED);
        }
        if (expected().nonStandardOptions.contains(MapRandomTestBuilder.NonStandardOption.REMOVE_UNSUPPORTED)) {
            set.add(CollectionRandomTestBuilder.NonStandardOption.REMOVE_UNSUPPORTED);
        }
        @SuppressWarnings({ "rawtypes", "unchecked" })
        ExpectedCollection<Map.Entry<K, V>> ec = new ExpectedCollection<>((Class) Map.Entry.class, expected().generator.entries(), expected().map.entrySet(),
                null, set);
        nested(ec, actual().entrySet());
    }
}
