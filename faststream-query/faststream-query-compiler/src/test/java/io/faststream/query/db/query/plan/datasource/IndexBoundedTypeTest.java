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
package io.faststream.query.db.query.plan.datasource;

/**
 * 
 * @author Kasper Nielsen
 */
public class IndexBoundedTypeTest {

    // @Test
    // public void boundsWithList() {
    // IndexBoundedComposite a = Composites.newIndexBoundedList();
    // assertTrue(a.hasDefaultLowerBound());
    // assertTrue(a.hasDefaultUpperBound());
    // assertTrue(a.hasDefaultBounds());
    //
    // assertEquals(newConstant(0), a.getLowerBound());
    // IndexBoundedComposite b = a.withLowerBound(newConstant(123));
    // assertEquals(newConstant(0), a.getLowerBound());
    // assertEquals(newConstant(123), b.getLowerBound());
    //
    // assertTrue(a.hasDefaultLowerBound());
    // assertTrue(a.hasDefaultUpperBound());
    // assertTrue(a.hasDefaultBounds());
    //
    // assertFalse(b.hasDefaultLowerBound());
    // assertTrue(b.hasDefaultUpperBound());
    // assertFalse(b.hasDefaultBounds());
    //
    // assertEquals(a.getIdentifier().invokeMethod(int.class, "size"), a.getUpperBound());
    // IndexBoundedComposite c = a.withUpperBound(newConstant(123));
    // assertEquals(a.getIdentifier().invokeMethod(int.class, "size"), a.getUpperBound());
    // assertEquals(newConstant(123), c.getUpperBound());
    //
    // assertTrue(a.hasDefaultLowerBound());
    // assertTrue(a.hasDefaultUpperBound());
    // assertTrue(a.hasDefaultBounds());
    //
    // assertTrue(c.hasDefaultLowerBound());
    // assertFalse(c.hasDefaultUpperBound());
    // assertFalse(c.hasDefaultBounds());
    //
    // IndexBoundedComposite d = a.withLowerBound(newConstant(12)).withUpperBound(newConstant(123));
    // assertEquals(newConstant(12), d.getLowerBound());
    // assertEquals(newConstant(123), d.getUpperBound());
    // assertFalse(d.hasDefaultLowerBound());
    // assertFalse(d.hasDefaultUpperBound());
    // assertFalse(d.hasDefaultBounds());
    // }
    //
    // @Test
    // public void sizeWithList() {
    // IndexBoundedComposite a = Composites.newIndexBoundedList();
    // assertEquals(a.getUpperBound(), a.size());
    // assertEval("123", a.withUpperBound(newConstant(123)).size());
    // assertEval("list.size() - 123", a.withLowerBound(newConstant(123)).size());
    // }
    //
    // @Test
    // public void isEmpty() {
    // IndexBoundedComposite a = Composites.newIndexBoundedList();
    // assertEval("list.isEmpty()", a.isEmpty());
    // assertEval("!list.isEmpty()", uNot(a.isEmpty()));
    // }
    //
    // static void assertEval(String expected, Expression e) {
    // assertEquals(expected, e.toString());
    // }
}
