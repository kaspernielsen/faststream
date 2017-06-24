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

import java.util.Collections;

import org.junit.Test;

/**
 * 
 * @author Kasper Nielsen
 */
public class OrderedTreeTest extends AbstractTreeTest {

    @Test
    public void noRoot() {
        TestTree tt = new TestTree();
        assertEquals(0, tt.modCount);
        assertFalse(tt.contains(new TestNode("")));
        // tt.depthFirstTraversal(TestProcedures.<TestNode> failing());
        assertEquals(-1, tt.getHeight());
        assertEquals(0, tt.size());
        assertNull(tt.getRoot());
    }

    @Test
    public void withRoot() {
        TestNode n = new TestNode("n");
        TestTree tt = new TestTree(n);
        assertEquals(0, tt.modCount);
        assertFalse(tt.contains(new TestNode("")));
        assertTrue(tt.contains(n));
        assertEquals(1, tt.size());
        // List<TestNode> list = new ArrayList<>();
        // tt.depthFirstTraversal(TestProcedures.into(list));
        // assertEquals(Arrays.asList(n), list);
        assertEquals(0, tt.getHeight());
        assertSame(n, tt.getRoot());
        verifyTree(tt);
    }

    @Test
    public void setRoot() {
        TestNode n = new TestNode("n");
        n.addChild(new TestNode(""));
        TestTree tt = new TestTree(n);

        TestNode t = new TestNode("t");
        tt.setRoot(t);
        assertSame(t, tt.getRoot());
        verifyTree(tt);
        verifyNode(null, null, n);
    }

    @Test
    public void takeRoot() {
        TestNode n = new TestNode("n");
        TestNode n1 = n.addChild(new TestNode("n1"));
        TestNode n2 = n1.addChild(new TestNode("n2"));
        TestTree t1 = new TestTree(n);
        assertEquals(3, t1.size());

        TestTree t2 = new TestTree();
        t2.setRoot(n);
        assertEquals(-1, t1.getHeight());
        assertEquals(2, t2.getHeight());
        assertNull(t1.getRoot());
        verifyTree(t1);
        verifyTree(t2);
        verifyNode(t2, null, n);

        t1.setRoot(n1);
        assertTrue(n.isLeaf());
        assertEquals(1, t1.getHeight());
        assertEquals(0, t2.getHeight());
        verifyTree(t1);
        verifyTree(t2);

        t1.setRoot(n2);
        assertEquals(0, t1.getHeight());
        verifyNode(t2, null, n);
        verifyNode(null, null, n1);
        verifyNode(t1, null, n2);

    }

    @Test
    public void depthFirstTraversal() {
        TestTree t = new TestTree();
        assertSame(Collections.emptyList(), t.depthFirstTraversal());

        t.setRoot(new TestNode("root"));
        testIterable(t.depthFirstTraversal(), t.getRoot());
    }

    @Test
    public void depthFirstTraversalPredicate() {
        TestTree t = new TestTree();
        testIterable(t.depthFirstTraversal(e -> true));

        t.setRoot(new TestNode("root"));
        testIterable(t.depthFirstTraversal(e -> true), t.getRoot());
        testIterable(t.depthFirstTraversal(e -> false));
    }
}
