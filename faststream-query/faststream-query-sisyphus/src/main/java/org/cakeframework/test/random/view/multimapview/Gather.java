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
package org.cakeframework.test.random.view.multimapview;

import org.cakeframework.util.view.MultimapView;

import io.faststream.sisyphus.annotations.RndTest;

/**
 * Test the {@link MultimapView#gather(org.cakeframework.util.op.Generator)} operation.
 * 
 * @param <K>
 *            the type of keys tested
 * @param <V>
 *            the type of values tested
 * @author Kasper Nielsen
 */
public class Gather<K, V> extends AbstractMultimapViewRandomTestCase<K, V> {

    @RndTest
    public void gather() {
        // int procedures = (int) Math.max(1, Math.round(Math.log(expected.size())));
        // Hvordan sikre vi os at vi laver lige mange procedure????

        // TODO implement
        setNext(expected(), actual());
    }
}
