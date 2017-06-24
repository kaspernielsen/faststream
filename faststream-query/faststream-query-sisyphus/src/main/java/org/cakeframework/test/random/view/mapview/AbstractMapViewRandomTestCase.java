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
package org.cakeframework.test.random.view.mapview;

import org.cakeframework.test.random.view.AbstractViewRandomTestCase;
import org.cakeframework.test.random.view.spi.ExpectedMapView;
import org.cakeframework.util.view.MapView;

/**
 * An abstract test step for {@link MapView}.
 *
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
abstract class AbstractMapViewRandomTestCase<K, V> extends
AbstractViewRandomTestCase<ExpectedMapView<K, V>, MapView<K, V>> {

    public final boolean isOrdered() {
        return expected().isOrdered();
    }
}
