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
import java.util.Map.Entry;

import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.LifecycleTestMethod;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * A test step for {@link Map#remove(Object)}.
 * 
 * @param <K>
 *            the type of keys we test
 * @param <V>
 *            the type of values we test
 * @author Kasper Nielsen
 */
public class Remove<K, V> extends AbstractRandomMapTestCase<K, V> {

    @io.faststream.sisyphus.annotations.Remove
    @RndTest
    @LifecycleTestMethod
    public void remove() {
        assumeRemoveSupported();

        Entry<K, V> e = mix().get();
        V result = actual().remove(e.getKey());// If lifecyletest, actual must fail without executing expected
        assertEquals(expected().remove(e.getKey()), result);
    }

    // ************************* CORNER CASES *************************

    @FailWith(NullPointerException.class)
    public void removeNull() {
        assumeRemoveSupported();
        assumeNullKeysUnsupported();

        actual().remove(null);
    }

    @FailWith(UnsupportedOperationException.class)
    public void unsupported() {
        assumeRemoveUnsupported();

        actual().remove(mix().get().getKey());
    }

    @FailWith(ClassCastException.class)
    public void restrictedElement() {
        assumeRemoveSupported();
        assumeIncompatibleKeysExist();

        actual().remove(incompatibleKeys().get());
    }
}
