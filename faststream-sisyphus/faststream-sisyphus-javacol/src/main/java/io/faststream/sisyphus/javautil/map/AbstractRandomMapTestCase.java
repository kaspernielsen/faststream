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

import java.util.Map;

import org.junit.Assume;

import io.faststream.sisyphus.generators.ElementGenerator;
import io.faststream.sisyphus.generators.EntryGenerator;
import io.faststream.sisyphus.javautil.MapRandomTestBuilder.NonStandardOption;
import io.faststream.sisyphus.spi.TestCase;

/**
 * An abstract test step for {@link Map}.
 * 
 * @param <K>
 *            the type of keys we test
 * @param <V>
 *            the type of values we test
 * @author Kasper Nielsen
 */
public abstract class AbstractRandomMapTestCase<K, V> extends TestCase<ExpectedMap<K, V>, Map<K, V>> {

    private void assume(NonStandardOption option, boolean present) {
        Assume.assumeTrue(expected().nonStandardOptions.contains(option) == present);
    }

    public void assumeIllegalKeysExist() {
        Assume.assumeTrue(illegalKeys() != null);
    }

    public void assumeIllegalValuesExist() {
        Assume.assumeTrue(illegalValues() != null);
    }

    public void assumeIncompatibleKeysExist() {
        Assume.assumeTrue(incompatibleKeys() != null);
    }

    public void assumeIncompatibleValuesExist() {
        Assume.assumeTrue(incompatibleValues() != null);
    }

    public void assumeNullKeysUnsupported() {
        assume(NonStandardOption.NULL_KEYS_UNSUPPORTED, true);
    }

    public void assumeNullQueriesUnsupported() {
        assume(NonStandardOption.NULL_QUERIES_UNSUPPORTED, true);
    }

    public void assumeNullValuesUnsupported() {
        assume(NonStandardOption.NULL_VALUES_UNSUPPORTED, true);
    }

    public void assumePutSupported() {
        assumePutSupported(false);
        assume(NonStandardOption.PUT_UNSUPPORTED, false);
    }

    public void assumePutSupported(boolean supported) {
        assume(NonStandardOption.PUT_UNSUPPORTED, !supported);
    }

    public void assumeRemoveSupported() {
        assume(NonStandardOption.REMOVE_UNSUPPORTED, false);
    }

    public void assumeRemoveUnsupported() {
        assume(NonStandardOption.REMOVE_UNSUPPORTED, true);
    }

    protected ElementGenerator<K> illegalKeys() {
        return expected().illegalKeys;
    }

    protected ElementGenerator<V> illegalValues() {
        return expected().illegalValues;
    }

    protected ElementGenerator<Object> incompatibleKeys() {
        return expected().incompatibleKeyTypes;
    }

    protected ElementGenerator<Object> incompatibleValues() {
        return expected().incompatibleValueTypes;
    }

    protected boolean isOrdered() {
        return false;
    }

    protected EntryGenerator<K, V> mix() {
        return expected().mix;
    }
}
