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
package org.cakeframework.internal.view.compiler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 * @author Kasper Nielsen
 */
public class TestTakeConstruct {

    @Test
    public void test() {
        calc(0, 10, 5L, 0, 5);
        calc(0, 10, 9L, 0, 9);
        calc(0, 10, 10L, 0, 10);
        calc(0, 10, 40L, 0, 10);
        calc(0, 10, -5L, 5, 10);
        calc(0, 10, -9L, 1, 10);
        calc(0, 10, -10L, 0, 10);
        calc(0, 10, -40L, 0, 10);

        calc(4, 7, 5L, 4, 7);
        calc(4, 7, 2L, 4, 6);
        calc(4, 7, 52L, 4, 7);
        calc(4, 7, -5L, 4, 7);
        calc(4, 7, -2L, 5, 7);
        calc(4, 7, -52L, 4, 7);

        calc(0, 0, 40L, 0, 0);
        calc(0, 0, -40L, 0, 0);

        calc(5, 5, 40L, 5, 5);
        calc(5, 5, 1L, 5, 5);
        calc(5, 5, -1L, 5, 5);
        calc(5, 5, Long.MAX_VALUE, 5, 5);
        calc(5, 5, Long.MIN_VALUE, 5, 5);

        calc(0, Integer.MAX_VALUE, -1L, Integer.MAX_VALUE - 1, Integer.MAX_VALUE);
        calc(1, Integer.MAX_VALUE, -Integer.MAX_VALUE - 1, 1, Integer.MAX_VALUE);

    }

    public void calc(int lowerBound, int upperBound, long numberOfElementsToTake, int expectedNewLowerBound,
            int expectedNewUpperBound) {
        int startIndex = lowerBound;
        int stopIndex = upperBound;
        if (stopIndex - 1 != startIndex) {
            if (numberOfElementsToTake > 0) {
                if (numberOfElementsToTake < stopIndex - startIndex) {
                    stopIndex = startIndex + (int) numberOfElementsToTake;
                }
            } else if (numberOfElementsToTake > startIndex - stopIndex) {
                startIndex = stopIndex + (int) numberOfElementsToTake;
            }
        }
        assertEquals(expectedNewLowerBound, startIndex);
        assertEquals(expectedNewUpperBound, stopIndex);
    }
}
