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
package io.faststream.sisyphus.expected;

import io.faststream.sisyphus.spi.method.TestSelector;

/**
 * The base class for expected values.
 * 
 * @author Kasper Nielsen
 */
public abstract class Expected {

    /** Should only be used by the builder */
    private TestSelector selector;

    /**
     * @param defaultSelector
     * @return the current selector. Or the specified selector if no selector is set
     */
    public final TestSelector getTestSelectorOr(TestSelector defaultSelector) {
        TestSelector s = this.selector;
        return s == null ? defaultSelector : s;
    }

    /**
     * Sets the current selector.
     * 
     * @param selector
     *            the selector to use
     * @return the specified selector
     */
    public final TestSelector setTestSelector(TestSelector selector) {
        this.selector = selector;
        return selector;
    }

    // this are final to avoid problems with users overriding them
    //
    /** this method is final to make sure users does not override it. */
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    /** this method is final to make sure users does not override it. */
    public final boolean equals(Object other) {
        throw new UnsupportedOperationException();
    }

    /** this method is final to make sure users does not override it. */
    public final String toString() {
        throw new UnsupportedOperationException("" + getClass());
    }

    public abstract int size();
}
