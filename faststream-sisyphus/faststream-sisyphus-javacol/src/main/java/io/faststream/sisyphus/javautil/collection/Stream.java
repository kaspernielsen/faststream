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

import io.faststream.sisyphus.annotations.CustomWeight;
import io.faststream.sisyphus.annotations.LifecycleTestMethod;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * @param <E>
 *            the elements in the collection
 * 
 * @author Kasper Nielsen
 */
public class Stream<E> extends AbstractRandomCollectionTestCase<E> {

    @RndTest
    @LifecycleTestMethod(false)
    @CustomWeight(1000000000)
    public void stream() {
        // boolean val = actual().stream().mapToLong(e -> e.hashCode()).sorted().parallel().parallel()
        // .filter(HashcodeModPredicate.from(2)).skip(2147483647).unordered().distinct()
        // .filter(HashcodeModPredicate.from(6)).filter(HashcodeModPredicate.from(1))
        // .allMatch(HashcodeModPredicate.from(1));
        // // boolean val = actual().stream().mapToLong(e -> e.hashCode()).parallel().sorted()
        // // .filter(HashcodeModPredicate.from(2)).skip(2147483647).allMatch(HashcodeModPredicate.from(2));
        // if (!val) {
        // }
        nested(expected().toStream(), actual().stream());
        streamStart();
    }
}
