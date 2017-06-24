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

import java.util.Collection;
import java.util.List;

import io.faststream.sisyphus.annotations.FailWith;
import io.faststream.sisyphus.annotations.LifecycleTestMethod;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Checks {@link Collection#containsAll(Collection)}.
 * 
 * @param <E>
 *            the elements in the collection
 * 
 * @author Kasper Nielsen
 */
public class ContainsAll<E> extends AbstractRandomCollectionTestCase<E> {

    @RndTest
    @LifecycleTestMethod(false)
    public void containsAll() {
        List<E> e = mix().nextList(0, 10).create();
        assertEquals(containsAll(e), actual().containsAll(e));
    }

    private boolean containsAll(Collection<?> col) {
        for (Object e : col) {
            if (!expected().contains(e)) {
                return false;
            }
        }
        return true;
    }

    // ************************* CORNER CASES *************************

    @FailWith(NullPointerException.class)
    public void containsAllNullArgument() {
        actual().containsAll(null);
    }

    @FailWith(NullPointerException.class)
    public void containsAllNullInList() {
        assumeNullQueriesUnsupported();

        actual().containsAll(mix().nextList(1, 10).includeNull());
    }

    @FailWith(ClassCastException.class)
    public void containsAllIncompatibleType() {
        assumeIncompatibleTypesExist();

        actual().contains(mix().nextList(1, 10).includeSome(incompatibleTypes()));
    }
}
