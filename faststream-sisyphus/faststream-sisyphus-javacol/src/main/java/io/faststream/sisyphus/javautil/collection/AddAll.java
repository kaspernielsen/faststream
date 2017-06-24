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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;

import io.faststream.sisyphus.annotations.Create;
import io.faststream.sisyphus.annotations.CustomWeight;
import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.LifecycleTestMethod;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Checks {@link Collection#addAll(Collection)}.
 * 
 * @param <E>
 *            the elements in the collection
 * 
 * @author Kasper Nielsen
 */
public class AddAll<E> extends AbstractRandomCollectionTestCase<E> {

    @Create
    @RndTest
    @LifecycleTestMethod
    public void addAll() {
        assumeAddSupported();

        Collection<E> e = mix().nextList(0, 10).create();
        boolean result = actual().addAll(e);// If lifecyle test, actual can fail without executing expected
        assertEquals(expected().addAll(e), result);
    }

    // ************************* CORNER CASES *************************

    @FailWith(NullPointerException.class)
    public void addAllnullArgument() {
        assumeAddSupported();

        actual().addAll(null);
    }

    @FailWith(NullPointerException.class)
    public void addAllNullElement() {
        assumeAddSupported();
        assumeNullUnsupported();

        actual().addAll(Collections.singleton((E) null));
    }

    @CustomWeight(0.001)
    public void addAllNullElements() {
        assumeAddSupported();
        assumeNullUnsupported();

        startNewBatch();
        try {
            actual().addAll(mix().nextList(1, 10).includeNull());
            fail("Should fail with NullPointerException");
        } catch (NullPointerException ok) {
            startNewBatch(); // we do not know if it added some valid elements, before failing
        }
    }

    @FailWith(UnsupportedOperationException.class)
    public void addAllUnsupported() {
        assumeAddUnsupported();
        // Implementations relying on AbstractCollection#addAll will accept an empty collection
        actual().addAll(mix().nextList(1, 5).create());
    }

    @SuppressWarnings({ "unchecked" })
    @FailWith(ClassCastException.class)
    public void addAllIncompatibleType() {
        assumeAddSupported();
        assumeIncompatibleTypesExist();

        actual().addAll((Collection<? extends E>) incompatibleTypes().nextList().singleton());
    }

    @SuppressWarnings("unchecked")
    @CustomWeight(0.001)
    public void addAllIncompatibleTypeList() {
        assumeAddSupported();
        assumeIncompatibleTypesExist();

        try {
            actual().addAll((Collection<? extends E>) mix().nextList(1, 10).includeSome(incompatibleTypes()));
            fail("Should fail with ClassCastException");
        } catch (ClassCastException ok) {
            startNewBatch(); // we do not know if it added some valid elements, before failing
        }
    }

    @FailWith(IllegalArgumentException.class)
    public void addAllIllegalElementAdd() {
        assumeAddSupported();
        assumeIllegalTypesExist();

        actual().addAll(illegalElements().nextList().singleton());
    }

    @SuppressWarnings("unchecked")
    @CustomWeight(0.001)
    public void addAllIllegalElementsAdd() {
        assumeAddSupported();
        assumeIllegalTypesExist();

        try {
            actual().addAll((Collection<? extends E>) mix().nextList(1, 10).includeSome(illegalElements()));
            fail("Should fail with IllegalArgumentException");
        } catch (IllegalArgumentException ok) {
            startNewBatch(); // we do not know if it added some valid elements, before failing
        }
    }
}
