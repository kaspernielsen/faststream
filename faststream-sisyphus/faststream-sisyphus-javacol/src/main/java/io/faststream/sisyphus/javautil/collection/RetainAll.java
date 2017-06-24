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
import java.util.Iterator;

import io.faststream.sisyphus.annotations.CustomWeight;
import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.LifecycleTestMethod;
import io.faststream.sisyphus.annotations.Remove;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Checks {@link Collection#retainAll(Collection)}.
 * 
 * @param <E>
 *            the elements in the collection
 * 
 * @author Kasper Nielsen
 */
public class RetainAll<E> extends AbstractRandomCollectionTestCase<E> {

    @Remove
    @RndTest
    @LifecycleTestMethod
    public void retainAll() {
        assumeRemoveSupported();

        Collection<E> e = mix().nextList(1, 10).create();
        boolean result = actual().retainAll(e);// If lifecyletest, actual must fail without executing expected
        assertEquals(retainAll(e), result);
    }

    private boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> it = expected().iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    // ************************* CORNER CASES *************************

    @FailWith(NullPointerException.class)
    public void retainAllNullArgument() {
        assumeRemoveSupported();
        assumeNullUnsupported();

        actual().retainAll(null);
    }

    @CustomWeight(0.001)
    public void retainAllNullInList() {
        assumeRemoveSupported();
        assumeNullUnsupported();

        try {
            actual().removeAll(mix().nextList(1, 10).includeNull());
            fail("Should fail with NullPointerException");
        } catch (NullPointerException ok) {
            startNewBatch(); // we do not know if it added some valid elements, before failing
        }
    }

    @FailWith(UnsupportedOperationException.class)
    public void retainAllUnsupported() {
        assumeRemoveUnsupported();

        actual().removeAll(mix().nextList(1, 5).create());
    }

    @FailWith(ClassCastException.class)
    public void retainAllIncompatibleType() {
        assumeRemoveSupported();
        assumeIncompatibleTypesExist();

        actual().retainAll(incompatibleTypes().nextList().singleton());
    }

    @CustomWeight(0.001)
    public void retainAllIncompatibleTypeList() {
        assumeRemoveSupported();
        assumeIncompatibleTypesExist();

        try {
            actual().retainAll(mix().nextList(1, 10).includeSome(incompatibleTypes()));
            fail("Should fail with ClassCastException");
        } catch (ClassCastException ok) {
            startNewBatch(); // we do not know if it added some valid elements, before failing
        }
    }
}
