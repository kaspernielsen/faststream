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
package org.cakeframework.test.random.view.collectionview;

import static io.faststream.sisyphus.util.MoreAsserts.assertArrayEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.cakeframework.util.view.CollectionView;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.util.ClassTestUtil;

/**
 * Test the {@link CollectionView#toList()} operation.
 *
 * @param <E>
 *            the type of elements tested
 * @author Kasper Nielsen
 */
public class To<E> extends AbstractCollectionViewRandomTestCase<E> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final Class<? extends Collection>[] LIST_VIEW_TO_LEGAL_TYPES = new Class[] { ArrayDeque.class,
            ArrayList.class, Collection.class, Deque.class, LinkedList.class, List.class };

    Class<?> getTarget(Iterable<E> expected) {
        if (random().nextBoolean()) {
            // Find a common component type for all elements
            Class<?>[] arrayTypes = ClassTestUtil.commonPublicTypesOfObjects(expected).toArray(new Class<?>[0]);
            return Array.newInstance(arrayTypes[random().nextInt(arrayTypes.length)], 0).getClass();
        } else {
            return LIST_VIEW_TO_LEGAL_TYPES[random().nextInt(LIST_VIEW_TO_LEGAL_TYPES.length)];
        }
    }

    @RndTest
    public void to() {
        Class<?> type = getTarget(expected());
        Object result = actual().to(type);
        if (type.isInterface()) {
            assertTrue(type.isInstance(result));
        } else {
            assertSame(type, result.getClass());
        }
        if (type.isArray()) {
            // TODO We should check type of array
            assertArrayEquals(expected().toArray(), (Object[]) result, isOrdered());
        } else if (result instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<E> col = (Collection<E>) result;
            expected().assertEqualsTo(col, isOrdered());
        } else {
            fail("Unexpected result type, type=" + result.getClass());
        }
    }

    @RndTest
    public void toList() {
        expected().assertEqualsTo(actual().toList(), isOrdered());
    }
}
