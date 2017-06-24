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

import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;

import io.faststream.codegen.core.CodegenUtil;

/**
 * @param <T>
 *            the type of the tree
 * @param <N>
 *            the type of the nodes in the tree
 * @author Kasper Nielsen
 */
public class OrderedTree<T extends OrderedTree<T, N>, N extends OrderedTreeNode<T, N>> {

    /** The number of times the plan has been structurally modified. */
    public int modCount;

    /** The root of the tree. */
    N root;

    public OrderedTree() {}

    public OrderedTree(N root) {
        setRoot(root);
        modCount = 0;
    }

    public final boolean contains(N node) {
        return node.tree == this;
    }

    /**
     * The returned iterable is not reusable.
     *
     */
    public final Iterable<N> depthFirstTraversal() {
        N root = this.root;
        return root == null ? Collections.<N> emptyList() : root.depthFirstTraversal();
    }

    public final Iterable<N> depthFirstTraversal(Predicate<? super N> predicate) {
        return root == null ? Collections.<N> emptyList() : root.depthFirstTraversal(predicate);
    }

    public void print(Function<N, String> f) {
        for (N n : depthFirstTraversal()) {
            System.out.println(CodegenUtil.spaces(2 * n.getDepth()) + f.apply(n));
        }
    }

    //
    // public final void depthFirstTraversal(Procedure<N> procedure) {
    // requireNonNull(procedure, "procedure is null");
    // if (root != null) {
    // OrderedTreeNode.depthFirstTraversal(procedure, root);
    // }
    // }

    /**
     * Returns the height of the tree. Warning: This operation is O(N) to the number of nodes in the tree.
     *
     * @return the height of the tree
     */
    public final int getHeight() {
        N root = this.root;
        return root == null ? -1 : root.getHeight();
    }

    public final N getRoot() {
        return root;
    }

    public final void setRoot(N newRoot) {
        // parent
        if (newRoot.parent != null) {
            newRoot.parent.children.remove(newRoot);
            newRoot.parent = null;
        }

        // tree
        if (root != null) {
            setTreeRecursive(root, null);
        }
        root = newRoot;
        if (newRoot.tree != this) {
            if (newRoot.tree != null) {
                if (newRoot.tree.root == newRoot) {
                    newRoot.tree.root = null;
                }
                newRoot.tree.modCount++;
            }
            setTreeRecursive(newRoot, this);
        }

        modCount++;
    }

    public int size() {
        N root = this.root;
        return root == null ? 0 : root.size();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    static void setTreeRecursive(OrderedTreeNode node, OrderedTree tree) {
        node.tree = tree;
        for (Object n : node.children) {
            setTreeRecursive((OrderedTreeNode) n, tree);
        }
    }
}
