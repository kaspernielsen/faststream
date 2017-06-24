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
package io.faststream.query.db.query.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import io.faststream.query.db.query.node.Operation;
import io.faststream.query.db.query.node.Operation.AggregatedOperation;

/**
 *
 * @author Kasper Nielsen
 */
public class OperationTest {

    static Operation LETTER = Operation.of("Letter");
    static Operation NUMBER = Operation.of("Number");

    static Operation A = Operation.of("A", LETTER);
    static Operation B = Operation.of("B", LETTER);

    static Operation ONE = Operation.of("1", NUMBER);
    static Operation TWO = Operation.of("2", NUMBER);

    static Operation ALL_LETTERS = Operation.aggregate(A, B);
    static Operation ALL_NUMBERS = Operation.aggregate("12", ONE, TWO);
    static Operation ALL = Operation.aggregate(ALL_LETTERS, ALL_NUMBERS);

    @Test
    public void isAnyOf() {
        assertTrue(LETTER.isAnyOf(LETTER));
        assertTrue(A.isAnyOf(B, LETTER));
        assertTrue(B.isAnyOf(ONE, LETTER));
        assertTrue(ONE.isAnyOf(LETTER, NUMBER));

        assertFalse(ONE.isAnyOf(LETTER));
        assertFalse(LETTER.isAnyOf(A, B));
        assertFalse(LETTER.isAnyOf(ONE, NUMBER, TWO));
    }

    @Test
    public void is() {
        assertTrue(LETTER.is(LETTER));
        assertTrue(A.is(LETTER));
        assertTrue(B.is(LETTER));

        assertFalse(ONE.is(LETTER));
        assertFalse(LETTER.is(A));
        assertFalse(LETTER.is(NUMBER));
    }

    @Test
    public void aggregate() {
        assertTrue(A.is(ALL_LETTERS));
        assertEquals(Arrays.asList(ALL_LETTERS, ALL_NUMBERS), ((AggregatedOperation) ALL).getElements());
        assertTrue(A.is(ALL));
        assertFalse(LETTER.is(ALL_LETTERS));

        assertTrue(ALL_LETTERS.is(ALL_LETTERS));
        assertTrue(ALL_LETTERS.is(ALL));

    }

    @Test
    public void toString0() {
        assertEquals("A", A.toString());
        assertEquals("12", ALL_NUMBERS.toString());
        assertEquals("aggregate", ALL_LETTERS.toString());
    }
}
