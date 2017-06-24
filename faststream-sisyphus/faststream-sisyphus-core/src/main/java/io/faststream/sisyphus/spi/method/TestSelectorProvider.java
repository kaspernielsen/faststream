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
package io.faststream.sisyphus.spi.method;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collection;

import io.faststream.sisyphus.TestScale;
import io.faststream.sisyphus.expected.Expected;
import io.faststream.sisyphus.util.RandomSource;

/**
 * 
 * @author Kasper Nielsen
 */
public abstract class TestSelectorProvider {

    public static final TestSelectorProvider DEFAULT = same(TestSelector.DEFAULT);

    public static final TestSelectorProvider ADD_REMOVE_BALANCE = new TestSelectorProvider() {
        public TestSelector getSelector(RandomSource rnd, Expected expected) {
            long s = expected.size();
            if (s < TestScale.defaultScale().getScale() / 2) {
                return expected.setTestSelector(TestSelector.RARELY_REMOVE);
            } else if (s > TestScale.defaultScale().getScale() * 2) {
                return expected.setTestSelector(TestSelector.DEFAULT);
            }
            return expected.getTestSelectorOr(TestSelector.DEFAULT);
        }
    };

    public abstract TestSelector getSelector(RandomSource rnd, Expected expected);

    public static TestSelectorProvider same(TestSelector selector) {
        requireNonNull(selector);
        return new TestSelectorProvider() {
            public TestSelector getSelector(RandomSource rnd, Expected expected) {
                return selector;
            }
        };
    }

    public static TestSelectorProvider restrictTests(Class<?>... classes) {
        final Collection<Class<?>> c = Arrays.asList(classes);
        return same(new TestSelector() {
            /** {@inheritDoc} */
            @Override
            public double calculateTestWeight(TestMethod method) {
                Class<?> clz = method.getMethod().getDeclaringClass();
                if (c.contains(clz)) {
                    return super.calculateTestWeight(method);
                }
                return 0;
            }
        });
    }
}
