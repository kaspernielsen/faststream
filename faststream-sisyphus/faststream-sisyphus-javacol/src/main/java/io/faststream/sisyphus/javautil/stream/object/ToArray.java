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
package io.faststream.sisyphus.javautil.stream.object;

import static io.faststream.sisyphus.util.MoreAsserts.assertArrayEquals;

import java.lang.reflect.Array;
import java.util.function.IntFunction;

import io.faststream.sisyphus.annotations.RndTest;
import io.faststream.sisyphus.util.ClassTestUtil;

/**
 *
 * @author Kasper Nielsen
 */
public class ToArray<E> extends AbstractRandomStreamTestCase<E> {

    @RndTest
    public void toArray() {
        assertArrayEquals(expected().toArray(), actual().toArray(), isOrdered());
        consumed();
    }

    @SuppressWarnings("unchecked")
    @RndTest
    public void toArrayFunction() {
        Class<?>[] arrayTypes = ClassTestUtil.commonPublicTypesOfObjects(expected()).toArray(new Class<?>[0]);
        Class<E> type = (Class<E>) arrayTypes[random().nextInt(arrayTypes.length)];
        IntFunction<E[]> f = e -> (E[]) Array.newInstance(type, e);

        assertArrayEquals(expected().toArray(), actual().toArray(f), isOrdered());
        consumed();
    }
}
