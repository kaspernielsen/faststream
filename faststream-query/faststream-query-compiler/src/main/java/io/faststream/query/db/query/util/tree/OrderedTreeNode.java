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
package io.faststream.query.db.query.util.tree;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * @param <T>
 *            the type of the tree
 * @param <N>
 *            the type of the nodes in the tree
 *
 * @author Kasper Nielsen
 */
public class OrderedTreeNode<T extends OrderedTree<T, N>, N extends OrderedTreeNode<T, N>> {

    /** An array of children. */
    final ArrayList<N> children = new ArrayList<>(1);

    /** The parent node. */
    N parent;

    /** The tree this node belongs to. */
    T tree;

    /**
     * Adds the specified child as the last child of this node (or the first if this node does not have any children)
     *
     * @param child
     *            the child to add
     * @return the specified child
     * @throws IllegalArgumentException
     *             if the specified child is a descended of this node
     * @throws NullPointerException
     *             if the specified child is null
     */
    public <S extends N> S addChild(S child) {
        requireNonNull(child);
        if (isDescendantOf(child)) {
            throw new IllegalArgumentException("Specified child is a descendant of this node");
        }
        // parent-child
        if (child.parent != null) {
            child.parent.children.remove(child);
        }
        child.parent = thisn();
        children.add(child);

        // tree
        if (child.tree != tree) {
            if (child.tree != null) {
                child.tree.modCount++;
            }
            OrderedTree.setTreeRecursive(child, tree);
        }
        if (tree != null) {
            tree.modCount++;
        }
        return child;
    }

    /**
     * Returns an ordered iterable of the children of this node.
     *
     * @return the children of this node
     */
    public List<N> children() {
        return Collections.unmodifiableList(children);
    }

    public List<N> childrenReverse() {
        List<N> l = new ArrayList<>(children);
        Collections.reverse(l);
        return Collections.unmodifiableList(l);
    }

    public final Iterable<N> depthFirstTraversal() {
        return DepthFirstIterator.newIterable(thisn());
    }

    public final Iterable<N> depthFirstTraversal(Predicate<? super N> predicate) {
        return new DepthFirstIterator<>(thisn()).filtered(predicate).asIterable();
    }

    @SuppressWarnings("unchecked")
    public final <S extends N> Iterable<S> depthFirstTraversal(Class<S> type, Predicate<? super S> predicate) {
        return (Iterable<S>) new DepthFirstIterator<>(thisn()).filtered(
                e -> type.isAssignableFrom(e.getClass()) && predicate.test((S) e)).asIterable();
    }

    public With find(WalkOrder o) {
        return new With(o);
    }

    @SuppressWarnings("unchecked")
    public final <S extends OrderedTreeNode<T, N>> S findFirst(WalkOrder o, Class<S> cl, Predicate<? super S> p) {
        return (S) findFirst(o, e -> cl.isInstance(e) && p.test((S) e));
    }

    @SuppressWarnings("unchecked")
    public final <S extends OrderedTreeNode<T, N>> S findFirst(WalkOrder o, Class<S> cl) {
        return (S) findFirst(o, e -> cl.isInstance(e));
    }

    @SuppressWarnings("unchecked")
    public final N findFirst(WalkOrder o, Predicate<? super N> p) {
        N n = (N) this;
        while (n != null) {
            n = o.find(n);
            if (n != null && p.test(n)) {
                return n;
            }
        }
        return null;
    }

    public final OrderedTreeNode<T, N> findFirstIncludeThis(WalkOrder o, Predicate<? super OrderedTreeNode<T, N>> p) {
        if (p.test(this)) {
            return this;
        }
        return findFirst(o, p);
    }

    /**
     * Returns the first child of this node. Or {@code null} if this node does not have any children
     *
     * @return the first child of this node
     */
    public N firstChild() {
        return children.size() == 0 ? null : children.get(0);
    }

    public final int getDepth() {
        int depth = 0;
        OrderedTreeNode<?, ?> node = this;
        while (node.parent != null) {
            depth++;
            node = node.parent;
        }
        return depth;
    }

    final int getHeight() {
        return getHeight(thisn(), 0);
    }

    private int getHeight(N node, int current) {
        int height = current;
        for (N n : node.children) {
            height = Math.max(height, getHeight(n, current + 1));
        }
        return height;
    }

    public N getParent() {
        return parent;
    }

    /**
     * Returns the tree that this node is a part of. Or {@code null} if this node is not part of any tree.
     *
     * @return the tree that this node is a part of.
     */
    public T getTree() {
        return tree;
    }

    public boolean hasNext() {
        return parent == null ? false : indexInParentOfThis() < parent.children.size() - 1;
    }

    public boolean hasPrevious() {
        return parent == null ? false : indexInParentOfThis() > 0;
    }

    int indexInParentOfThis() {
        return parent.children.indexOf(this); // we use equals here, just sayign
    }

    public <P extends N> P insertAfterThis(P siebling) {
        requireNonNull(siebling);
        if (isRoot()) {
            throw new IllegalArgumentException("cannot insert before root");
        } else if (isDescendantOf(siebling)) {
            throw new IllegalArgumentException("Specified siebling is a descendant of this node");
        }
        // parent-child
        if (siebling.parent != null) {
            siebling.parent.children.remove(siebling);
        }
        siebling.parent = parent;
        int index = indexInParentOfThis() + 1;
        if (index > parent.children.size()) {
            parent.children.add(siebling);
        } else {
            parent.children.add(index, siebling);
        }

        // tree
        if (siebling.tree != tree) {
            if (siebling.tree != null) {
                siebling.tree.modCount++;
            }
            OrderedTree.setTreeRecursive(siebling, tree);
        }
        if (tree != null) {
            tree.modCount++;
        }
        return siebling;
    }

    public <P extends N> P insertBeforeThis(P siebling) {
        requireNonNull(siebling);
        if (isRoot()) {
            throw new IllegalArgumentException("cannot insert before root");
        } else if (isDescendantOf(siebling)) {
            throw new IllegalArgumentException("Specified siebling is a descendant of this node");
        }
        // parent-child
        if (siebling.parent != null) {
            siebling.parent.children.remove(siebling);
        }
        siebling.parent = parent;
        parent.children.add(indexInParentOfThis(), siebling);

        // tree
        if (siebling.tree != tree) {
            if (siebling.tree != null) {
                siebling.tree.modCount++;
            }
            OrderedTree.setTreeRecursive(siebling, tree);
        }
        if (tree != null) {
            tree.modCount++;
        }
        return siebling;
    }

    @SuppressWarnings("unchecked")
    public <P extends N> P insertNewParent(P newParent) {
        requireNonNull(newParent);
        insertAfterThis(newParent);
        this.remove();
        newParent.addChild((P) this);
        return newParent;
    }

    public <P extends N> P replace(P replaceWith) {
        if (this.isRoot()) {
            tree.setRoot(replaceWith);
        } else {
            insertAfterThis(replaceWith);
        }
        for (N child : new ArrayList<>(children)) {
            replaceWith.addChild(child);
        }
        if (!this.isRoot()) {
            this.remove();
        }
        return replaceWith;
    }

    public boolean isAncestorOf(N ancestor) {
        OrderedTreeNode<?, ?> node = requireNonNull(ancestor, "ancestor is null");
        while (node != null) {
            if (this == node) {
                return true;
            }
            node = node.parent;
        }
        return false;
    }

    public boolean isDescendantOf(N descendant) {
        return descendant.isAncestorOf(thisn());
    }

    /**
     * Returns <tt>true</tt> if this node has no children. Otherwise <tt>false</tt>.
     *
     * @return true if this node has no children. Otherwise false
     */
    public final boolean isLeaf() {
        return children.isEmpty();
    }

    public final boolean isRoot() {
        return parent == null;
    }

    public N lastBeforeRoot() {
        if (parent == null) {
            throw new IllegalStateException("cannot invoke this method on the root");
        }
        N n = thisn();
        while (n.parent.parent != null) {
            n = n.parent;
        }
        return n;
    }

    /**
     * Returns the first child of this node. Or {@code null} if this node does not have any children
     *
     * @return the first child of this node
     */
    public N lastChild() {
        return children.size() == 0 ? null : children.get(children.size() - 1);
    }

    public N next() {
        if (parent != null) {
            int i = indexInParentOfThis() + 1;
            return i < parent.children.size() ? parent.children.get(i) : null;
        }
        return null;
    }

    // public final OrderedTreeNode<T, N> findFirst(WalkOrder o, Predicate<? super OrderedTreeNode<T, N>> p) {
    // OrderedTreeNode<T, N> n = this;
    // while (n != null) {
    // n = o.find(n);
    // if (p.test(n)) {
    // return n;
    // }
    // }
    // return null;
    // }

    public N previous() {
        if (parent != null) {
            int i = indexInParentOfThis() - 1;
            return i >= 0 ? parent.children.get(i) : null;
        }
        return null;
    }

    public void removePullupChildren() {
        if (parent == null) {
            throw new IllegalStateException(
                    "cannot remove the root using this method. If this node is part of a tree use tree.setRoot(null)");
        }
        N n = (N) this;
        for (N c : new ArrayList<>(children)) {
            n.insertAfterThis(c);
            n = c;
        }
        remove();
    }

    public void remove() {
        if (parent == null) {
            throw new IllegalStateException(
                    "cannot remove the root using this method. If this node is part of a tree use tree.setRoot(null)");
        }
        parent.children.remove(this);
        parent = null;
        if (tree != null) {
            tree.modCount++;
            OrderedTree.setTreeRecursive(this, null);
        }
    }

    /**
     * Returns the number of descendants (including this).
     *
     * @return the number of descendants (including this)
     */
    public final int size() {
        int s = 1;
        for (N n : children) {
            s += n.size();
        }
        return s;
    }

    @SuppressWarnings("unchecked")
    private N thisn() {
        return (N) this;
    }

    public final Iterable<N> walkToRoot(boolean includeThis) {
        return walkToRootIterator(includeThis).asIterable();
    }

    // public N replaceWith(N replacement) {
    // if (requireNonNull(replacement, "replacement is null") == this) {
    // throw new IllegalArgumentException("cannot replace node with itself");
    // } else if (parent == null) {
    // throw new IllegalArgumentException("cannot replace the root");
    // } else if (isDescendantOf(replacement)) {
    // throw new IllegalArgumentException("Specified replacement is a descendant of this node");
    // }
    // // parent-child
    // if (replacement.parent != null) {
    // replacement.parent.children.remove(replacement);
    // }
    // replacement.parent = parent;
    // parent.children.set(parent.children.indexOf(this), replacement);
    // parent = null;
    // // tree
    // if (replacement.tree != tree) {
    // if (replacement.tree != null) {
    // replacement.tree.modCount++;
    // }
    // OrderedTree.setTreeRecursive(replacement, tree);
    // }
    // if (tree != null) {
    // OrderedTree.setTreeRecursive(this, null);
    // tree.modCount++;
    // }
    // return replacement;
    // }
    //
    //
    //
    // public final void depthFirstTraversal(Procedure<N> procedure) {
    // depthFirstTraversal(requireNonNull(procedure, "procedure is null"), thisn());
    // }
    //
    // @SuppressWarnings({ "rawtypes", "unchecked" })
    // static void depthFirstTraversal(Procedure procedure, OrderedTreeNode startingNode) {
    // procedure.apply(startingNode);
    // for (Object n : startingNode.children) {
    // depthFirstTraversal(procedure, (OrderedTreeNode) n);
    // }
    // }

    @SuppressWarnings("unchecked")
    public final <S extends N> Iterable<S> walkToRoot(boolean includeThis, Class<S> type) {
        return (Iterable<S>) walkToRootIterator(includeThis).filtered(e -> type.isInstance(e)).asIterable();
    }

    private AbstractIterator<N> walkToRootIterator(boolean includeThis) {
        AbstractIterator<N> i = new AbstractIterator<N>(thisn()) {
            protected N next0(N current) {
                N prev = current.previous();
                return prev != null ? prev : current.parent;
            }
        };
        if (!includeThis) {
            i.next();// skip this element
        }
        return i;
    }

    public class With {
        private final WalkOrder o;

        With(WalkOrder o) {
            this.o = requireNonNull(o);
        }

        public N first() {
            return first(e -> true);
        }

        @SuppressWarnings("unchecked")
        public <S extends OrderedTreeNode<T, N>> S first(Class<? extends S> c) {
            return (S) first(n -> c.isAssignableFrom(n.getClass()));
        }

        public N first(Predicate<? super N> p) {
            N n = n();
            while (n != null) {
                n = o.find(n);
                if (n != null && p.test(n)) {
                    return n;
                }
            }
            return null;
        }

        public List<N> all(Predicate<? super N> p) {
            ArrayList<N> result = new ArrayList<>();
            N n = n();
            while (n != null) {
                n = o.find(n);
                if (n != null && p.test(n)) {
                    result.add(n);
                }
            }
            return result;
        }

        public N last(Predicate<? super N> p) {
            List<N> l = all(p);
            return l.size() == 0 ? null : l.get(l.size() - 1);
        }

        public N last() {
            N n = n();
            for (;;) {
                N nn = o.find(n);
                if (nn == null) {
                    return n;
                }
                n = nn;
            }
        }

        @SuppressWarnings("unchecked")
        private N n() {
            return (N) OrderedTreeNode.this;
        }

        public OrderedTreePath<T, N> path(N node, boolean includeThis, boolean includeTarget) {
            return path(e -> e == node, includeThis, includeTarget);
        }

        public OrderedTreePath<T, N> path(Predicate<? super OrderedTreeNode<T, N>> p, boolean includeThis,
                boolean includeTarget) {
            ArrayList<N> list = new ArrayList<>();
            N n = n();
            if (includeThis) {
                list.add(n);
            }
            while ((n = o.find(n)) != null) {
                if (p.test(n)) {
                    if (includeTarget) {
                        list.add(n);
                    }
                    return new OrderedTreePath<>(list);
                }
                list.add(n);
            }
            throw new IllegalStateException("Could not find path");
        }

        public OrderedTreePath<T, N> pathBetween(Predicate<? super OrderedTreeNode<T, N>> p) {
            return path(p, false, false);
        }
    }
}
