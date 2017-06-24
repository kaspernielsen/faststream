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
package org.cakeframework.test.random.view;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.function.Function;

import org.cakeframework.test.random.view.spi.ExpectedMapView;
import org.cakeframework.util.view.MapView;

import io.faststream.sisyphus.generators.EntryGenerator;
import io.faststream.sisyphus.javautil.spi.AbstractMapLikeTestBuilder;

/**
 * @param <K>
 *            the type of keys
 * @param <V>
 *            the type of values
 * 
 * @author Kasper Nielsen
 */
public abstract class MapViewRandomTestBuilder<K, V> extends
        AbstractMapLikeTestBuilder<ExpectedMapView<K, V>, MapView<K, V>, K, V> {

    private Function<EntryGenerator<K, V>, ? extends Map<K, V>> testMapper = EntryGenerator.randomMap();

    protected abstract MapView<K, V> createTestStructure(Map<K, V> bootstrap);

    @Override
    protected final void createTestSet(Bootstrap<ExpectedMapView<K, V>, MapView<K, V>> factory) {
        Map<K, V> bootstrap = testMapper.apply(getGenerator());
        factory.setActual(createTestStructure(bootstrap));
        factory.setExpected(new ExpectedMapView<>(bootstrap, false));
    }

    public void setTestDataGenerator(Function<EntryGenerator<K, V>, ? extends Map<K, V>> testMapper) {
        this.testMapper = requireNonNull(testMapper);
    }
}
