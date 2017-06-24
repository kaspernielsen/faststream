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

import static io.faststream.sisyphus.util.IteratorTestUtil.testIterable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * 
 * @author Kasper Nielsen
 */
abstract class AbstractTreeTest {

    void testDepthFirst(TestNode n, TestNode... result) {
        testIterable(DepthFirstIterator.newIterable(n), (Object[]) result);
        // if (n != null) {
        // List<TestNode> l = new ArrayList<>();
        // n.depthFirstTraversal(TestProcedures.into(l));
        // assertEquals(Arrays.asList(result), l);
        // }
    }

    void verifyTree(TestTree tree) {
        if (tree.getRoot() == null) {
            assertEquals(-1, tree.getHeight());
        } else {
            verifyNode(tree, null, tree.getRoot());
        }
    }

    @SuppressWarnings("null")
    void verifyNode(TestTree tree, TestNode parent, TestNode node) {
        assertSame(tree, node.getTree());
        assertSame(parent, node.parent);
        assertEquals(node.children.isEmpty(), node.isLeaf());

        assertEquals(node.children.isEmpty() ? null : node.children.get(0), node.firstChild());
        if (tree != null) {
            assertTrue(tree.contains(node));
            assertEquals(tree.root == node, node.isRoot());
        }

        if (parent == null) {
            assertFalse(node.hasNext());
            assertFalse(node.hasPrevious());
            assertNull(node.next());
            assertNull(node.previous());
        } else {
            assertTrue(parent.children.contains(node));
        }
        assertSame(tree, node.tree);
        for (TestNode n : node.children) {
            verifyNode(tree, node, n);
        }

        if (node.children.size() > 0) {
            TestNode previous = null;
            for (int i = 0; i < node.children.size(); i++) {
                TestNode n = node.children.get(i);
                assertEquals(i != 0, n.hasPrevious());
                assertEquals(i != node.children.size() - 1, n.hasNext());
                assertEquals(previous, n.previous());
                if (i > 0) {
                    assertEquals(n, previous.next());
                }
                if (i == node.children.size() - 1) {
                    TestNode nnn = n.next();
                    assertNull("expected next = null, but was " + nnn + ", current = " + n, nnn);
                }
                previous = n;
            }

        }
    }
}
