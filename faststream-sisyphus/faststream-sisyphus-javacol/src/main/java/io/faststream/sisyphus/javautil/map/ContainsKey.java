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

import static org.junit.Assert.assertEquals;

import java.util.Map;

import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.LifecycleTestMethod;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Tests {@link Map#containsKey(Object)}.
 * 
 * @param <K>
 *            the type of keys we test
 * @param <V>
 *            the type of values we test
 * @author Kasper Nielsen
 */
public class ContainsKey<K, V> extends AbstractRandomMapTestCase<K, V> {

    @RndTest
    @LifecycleTestMethod
    public void containsKey() {
        K k = mix().get().getKey();
        assertEquals(expected().containsKey(k), actual().containsKey(k));
    }

    // ************************* CORNER CASES *************************

    @FailWith(NullPointerException.class)
    public void containsKeyNullArgument() {
        assumeNullQueriesUnsupported();

        actual().containsKey(null);
    }

    @FailWith(ClassCastException.class)
    public void containsKeyIncompatibleKey() {
        assumeIncompatibleKeysExist();

        actual().containsKey(incompatibleKeys().get());
    }
}
