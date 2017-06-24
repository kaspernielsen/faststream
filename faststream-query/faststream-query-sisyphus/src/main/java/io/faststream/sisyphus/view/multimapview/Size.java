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
package io.faststream.sisyphus.view.multimapview;

import static org.junit.Assert.fail;

import io.faststream.query.util.view.MultimapView;
import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link MultimapView#size()} operation.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class Size<K, V> extends AbstractMultimapViewRandomTestCase<K, V> {

    @RndTest
    public void size() {
        long actualSize = actual().size();
        long expectedSize = expected().size();
        if (expectedSize != actualSize) {
            fail("Expected a MapView with " + expectedSize + " elements but mapView.size() returned " + actualSize);
        }
    }

    @RndTest
    public void isEmpty() {
        boolean isEmpty = actual().isEmpty();
        if (expected().isEmpty() != isEmpty) {
            if (isEmpty) {
                fail("Found an empty MultimapView but expected a MultimapView with the following entries " + expected());
            } else {
                fail("Expected a MultimapView with no entries, but contained the following entries "
                        + actual().toMultimap());
            }
        }
    }
}
