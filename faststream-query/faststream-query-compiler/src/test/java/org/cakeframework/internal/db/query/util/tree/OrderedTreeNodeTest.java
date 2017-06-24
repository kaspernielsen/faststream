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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link OrderedTreeNode}.
 * 
 * @author Kasper Nielsen
 */
public class OrderedTreeNodeTest extends AbstractTreeTest {

    TestTree tree;
    TestNode n;
    TestNode n1;
    TestNode n2;

    TestNode s;
    TestNode ss;
    TestNode sss;

    TestNode z;
    TestNode z1;
    TestNode z2;
    TestNode z2z;
    TestNode z3;

    TestNode t;

    @Before
    public void setup() {
        tree = new TestTree(t = new TestNode("root"));
        n = new TestNode("n");
        n1 = new TestNode("n1");
        n2 = new TestNode("n2");
        s = new TestNode("s");
        ss = s.addChild(new TestNode("ss"));
        sss = ss.addChild(new TestNode("sss"));
        z = new TestNode("z");
        z1 = z.addChild(new TestNode("z1"));
        z2 = z.addChild(new TestNode("z2"));
        z2z = z2.addChild(new TestNode("z2z"));
        z3 = z.addChild(new TestNode("z3"));
    }

    @Test
    public void addChild() {
        n.addChild(n1);
        verifyNode(null, n, n1);
    }

    @Test
    public void addChildWithExistingParent() {
        n.addChild(n1);
        verifyNode(null, null, n);
        n.addChild(ss);
        verifyNode(null, null, s);
        verifyNode(null, null, n);
        verifyNode(null, n, ss);
        assertTrue(s.isLeaf());
        assertSame(n, ss.parent);
    }

    @Test
    public void addChildTree() {
        TestTree otherTree = new TestTree(s);
        verifyTree(otherTree);
        t.addChild(ss);
        verifyTree(otherTree);
        verifyTree(tree);
        assertEquals(1, otherTree.modCount);
        assertEquals(1, tree.modCount);
    }

    @Test
    public void addChildModCount() {
        assertEquals(0, tree.modCount);
        t.addChild(n);
        assertEquals(1, tree.modCount);
        t.addChild(s);
        assertEquals(2, tree.modCount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChildSelf() {
        n.addChild(n);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChildParent() {
        ss.addChild(s);
    }

    @Test
    public void children() {
        assertEquals(s.children, s.children());
        assertEquals(z.children, z.children());
    }

    @Test
    public void getDepth() {
        assertEquals(0, t.getDepth());
        assertEquals(0, n.getDepth());
        assertEquals(0, s.getDepth());
        assertEquals(1, ss.getDepth());
        assertEquals(2, sss.getDepth());
    }

    @Test
    public void insertBefore() {
        ss.insertBeforeThis(n);
        verifyNode(null, s, n);
        assertSame(n, s.firstChild());
        assertSame(ss, n.next());

        tree.setRoot(z);
        ss.insertBeforeThis(z2);
        assertEquals(2, tree.modCount);
        assertSame(z2, n.next());
        verifyTree(tree);
        verifyNode(null, null, s);
    }

    @Test
    public void insertBeforeTree() {
        TestTree otherTree = new TestTree(s);
        verifyTree(otherTree);
        t.addChild(n);
        n.insertBeforeThis(ss);
        verifyTree(otherTree);
        verifyTree(tree);
        assertEquals(1, otherTree.modCount);
        assertEquals(2, tree.modCount);
        assertEquals(4, tree.size());

        n.insertBeforeThis(z2);
        verifyTree(tree);
        assertEquals(6, tree.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertBeforeRootIAE() {
        n.insertBeforeThis(ss);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertBeforeSelfIAE() {
        n.insertBeforeThis(n);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertBeforeChildIAE() {
        sss.insertBeforeThis(s);
    }

    @Test
    public void remove() {
        z1.remove();
        assertNull(z1.parent);
        assertSame(z2, z.firstChild());
        assertNull(z2.previous());
        assertEquals(4, z.size());

        setup();
        z2.remove();
        verifyNode(null, null, z);
        assertNull(z2.parent);
        assertSame(z1, z3.previous());
        assertSame(z3, z1.next());
        assertEquals(3, z.size());

        setup();
        z3.remove();
        verifyNode(null, null, z);
        assertNull(z3.parent);
        assertNull(z2.next());
        assertEquals(4, z.size());
    }

    @Test
    public void removeTree() {
        t.addChild(s);
        verifyTree(tree);
        assertEquals(1, tree.modCount);
        ss.remove();
        assertEquals(2, tree.modCount);
        verifyTree(tree);
        assertSame(t, s.parent);
        assertNull(ss.tree);
        assertNull(ss.parent);
        verifyNode(null, ss, sss);
    }

    @Test(expected = IllegalStateException.class)
    public void removeRootISE() {
        n.remove();
    }

    @Test
    public void walkToRoot() {
        testIterable(z.walkToRoot(false));
        testIterable(z.walkToRoot(true), z);

        testIterable(z2.walkToRoot(false), z1, z);
        testIterable(z2.walkToRoot(true), z2, z1, z);

        testIterable(z2z.walkToRoot(false), z2, z1, z);
        testIterable(z2z.walkToRoot(true), z2z, z2, z1, z);

        testIterable(z3.walkToRoot(false), z2, z1, z);
        testIterable(z3.walkToRoot(true), z3, z2, z1, z);
    }
}
