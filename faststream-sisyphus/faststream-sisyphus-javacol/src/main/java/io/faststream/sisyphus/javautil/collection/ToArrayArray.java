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

import static io.faststream.sisyphus.util.MoreAsserts.assertArrayEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.Collection;

import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.LifecycleTestMethod;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Checks {@link Collection#toArray(Object[])}.
 * 
 * @param <E>
 *            the elements in the collection
 * 
 * @author Kasper Nielsen
 */
public class ToArrayArray<E> extends AbstractRandomCollectionTestCase<E> {

    /** Tests an array with too little space */
    @RndTest
    @LifecycleTestMethod
    public void toArraySmall() {
        assumeTrue(expected().size() > 0);
        Object[] a = source().randomAssignableArray(random().nextInt(0, expected().size()));
        Object[] aa = actual().toArray(a);
        assertNotSame(a, aa); // A new array has been allocated by the implementation
        assertSame(a.getClass(), aa.getClass());// Same type for new array
        for (int i = 0; i < a.length; i++) {
            assertNull(a[i]);// Tests that no modification has been made to existing
        }
        assertArrayEquals(expected().toArray(), aa, isOrdered());
    }

    @RndTest
    @LifecycleTestMethod
    public void toArrayExactSize() {
        Object[] a = source().randomAssignableArray(expected().size());
        assertSame(actual().toArray(a), a);
        assertArrayEquals(expected().toArray(), a, isOrdered());
    }

    /** Tests an array with too much space */
    @RndTest
    @LifecycleTestMethod
    public void toArrayBig() {
        int size = expected().size();
        assumeTrue(expected().size() < (Integer.MAX_VALUE - 1000));
        Object[] a = source().randomAssignableArray(size + random().nextInt(1, 10));
        Object element = mix().get();
        Arrays.fill(a, element);
        assertSame(actual().toArray(a), a);
        assertArrayEquals(expected().toArray(), Arrays.copyOf(a, size), isOrdered());
        assertNull(a[size]);// element + 1 = null
        for (int i = size + 1; i < a.length; i++) {
            assertSame(element, a[i]);
        }
    }

    // ************************* CORNER CASES *************************

    @FailWith(NullPointerException.class)
    public void toArrayNull() {
        actual().toArray(null);
    }

    @FailWith(ArrayStoreException.class)
    public void toArrayIllegalArrayType() {
        assumeTrue(!expected().allIsNull()); // A list with only nulls will succeed
        actual().toArray(new RndTest[1]);
    }
}
