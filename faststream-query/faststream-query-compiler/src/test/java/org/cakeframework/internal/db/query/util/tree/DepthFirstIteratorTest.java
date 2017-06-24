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
package org.cakeframework.internal.db.query.util.tree;

import org.junit.Test;

/**
 * Tests {@link DepthFirstIterator}.
 * 
 * @author Kasper Nielsen
 */
public class DepthFirstIteratorTest extends AbstractTreeTest {

    @Test(expected = UnsupportedOperationException.class)
    public void remove() {
        new DepthFirstIterator<>(null).remove();
    }

    @Test
    public void test() {
        testDepthFirst(null);

        TestNode n = new TestNode("n");
        testDepthFirst(n, n);

        TestNode n1 = n.addChild(new TestNode("n1"));
        testDepthFirst(n, n, n1);
        testDepthFirst(n1, n1);

        TestNode n2 = n.addChild(new TestNode("n2"));
        testDepthFirst(n, n, n1, n2);
        testDepthFirst(n1, n1);
        testDepthFirst(n2, n2);

        TestNode n1n1 = n1.addChild(new TestNode("n1n1"));
        testDepthFirst(n, n, n1, n1n1, n2);
        testDepthFirst(n1, n1, n1n1);
        testDepthFirst(n1n1, n1n1);
        testDepthFirst(n2, n2);

        TestNode n1n0 = n1n1.insertBeforeThis(new TestNode("n1n0"));
        testDepthFirst(n, n, n1, n1n0, n1n1, n2);
        testDepthFirst(n1, n1, n1n0, n1n1);
        testDepthFirst(n1n0, n1n0);
        testDepthFirst(n1n1, n1n1);
        testDepthFirst(n2, n2);
    }

}
