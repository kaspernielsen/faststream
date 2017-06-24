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

import io.faststream.sisyphus.annotations.Create;
import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.LifecycleTestMethod;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * A test step for {@link Map#put(Object, Object)}.
 * 
 * @param <K>
 *            the type of keys we test
 * @param <V>
 *            the type of values we test
 * @author Kasper Nielsen
 */
public class Put<K, V> extends AbstractRandomMapTestCase<K, V> {

    /** Test a various mix of keys that already exist and keys that does not. */
    @Create
    @LifecycleTestMethod
    @RndTest
    public void put() {
        assumePutSupported();

        Entry<K, V> e = mix().get();
        assertEquals(expected().put(e.getKey(), e.getValue()), actual().put(e.getKey(), e.getValue()));
    }

    // ************************* CORNER CASES *************************

    /** Tests that null keys are not accepted, if the map does not support them. */
    @FailWith(NullPointerException.class)
    public void putNullKey() {
        assumePutSupported();
        assumeNullKeysUnsupported();

        actual().put(null, mix().get().getValue());
    }

    /** Tests that null values are not accepted, if the map does not support them. */
    @FailWith(NullPointerException.class)
    public void putNullValue() {
        assumePutSupported();
        assumeNullValuesUnsupported();

        actual().put(mix().get().getKey(), null);
    }

    /** Tests that {@link UnsupportedOperationException} is thrown if the put method is not supported */
    @FailWith(UnsupportedOperationException.class)
    public void putUnsupported() {
        assumePutSupported(false);

        Entry<K, V> e = mix().get();
        actual().put(e.getKey(), e.getValue());
    }

    /** Tests that maps that place restrictions of the type of keys throws {@link ClassCastException}. */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @FailWith(ClassCastException.class)
    public void putIncompatibleKey() {
        assumePutSupported();
        assumeIncompatibleKeysExist();

        ((Map) actual()).put(incompatibleKeys().get(), mix().get().getValue());
    }

    /** Tests that maps that place restrictions of the type of values throws {@link ClassCastException}. */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @FailWith(ClassCastException.class)
    public void putIncompatibleValue() {
        assumePutSupported();
        assumeIncompatibleValuesExist();

        ((Map) actual()).put(mix().get().getKey(), incompatibleValues().get());
    }

    /** Tests that maps that place restrictions of some property of the keys throws {@link IllegalArgumentException}. */
    @FailWith(IllegalArgumentException.class)
    public void putIllegalKey() {
        assumePutSupported();
        assumeIllegalKeysExist();

        actual().put(illegalKeys().get(), mix().get().getValue());
    }

    /**
     * Tests that maps that place restrictions of some property of the values throws {@link IllegalArgumentException}.
     */
    @FailWith(IllegalArgumentException.class)
    public void putIllegalValue() {
        assumePutSupported();
        assumeIllegalValuesExist();

        actual().put(mix().get().getKey(), illegalValues().get());
    }
}
