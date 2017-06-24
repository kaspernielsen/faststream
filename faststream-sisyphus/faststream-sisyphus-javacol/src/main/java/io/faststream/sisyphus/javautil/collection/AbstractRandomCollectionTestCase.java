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
package io.faststream.sisyphus.javautil.collection;

import java.util.Collection;

import org.junit.Assume;

import io.faststream.sisyphus.generators.ElementGenerator;
import io.faststream.sisyphus.generators.TypedElementSupplier;
import io.faststream.sisyphus.javautil.CollectionRandomTestBuilder.NonStandardOption;
import io.faststream.sisyphus.spi.TestCase;

/**
 * An abstract test step for {@link Collection}.
 * 
 * @param <E>
 *            the elements in the collection
 * 
 * @author Kasper Nielsen
 */
public abstract class AbstractRandomCollectionTestCase<E> extends TestCase<ExpectedCollection<E>, Collection<E>> {

    private void assume(NonStandardOption option, boolean present) {
        Assume.assumeTrue(expected().nonStandardOptions.contains(option) == present);
    }

    protected void assumeAddSupported() {
        assume(NonStandardOption.ADD_UNSUPPORTED, false);
    }

    protected void assumeAddUnsupported() {
        assume(NonStandardOption.ADD_UNSUPPORTED, true);
    }

    protected void assumeIllegalTypesExist() {
        Assume.assumeTrue(illegalElements() != null);
    }

    protected void assumeIncompatibleTypesExist() {
        Assume.assumeTrue(incompatibleTypes() != null);
    }

    protected void assumeNullQueriesUnsupported() {
        assume(NonStandardOption.NULL_QUERIES_UNSUPPORTED, true);
    }

    protected void assumeNullUnsupported() {
        assume(NonStandardOption.NULL_UNSUPPORTED, true);
    }

    protected void assumeRemoveSupported() {
        assume(NonStandardOption.REMOVE_UNSUPPORTED, false);
    }

    protected void assumeRemoveUnsupported() {
        assume(NonStandardOption.REMOVE_UNSUPPORTED, true);
    }

    protected ElementGenerator<E> illegalElements() {
        return expected().illegalElements;
    }

    protected ElementGenerator<Object> incompatibleTypes() {
        return expected().incompatibleTypes;
    }

    protected boolean isOrdered() {
        return false;
    }

    protected ElementGenerator<E> mix() {
        return expected().mix;
    }

    protected TypedElementSupplier<E> source() {
        return TypedElementSupplier.random(expected().type);
    }
}
