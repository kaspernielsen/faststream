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

/**
 *
 * @author Kasper Nielsen
 */
public enum WalkOrder {

    PARENT {
        <T extends OrderedTree<T, N>, N extends OrderedTreeNode<T, N>> N find(OrderedTreeNode<T, N> n) {
            return n.getParent();
        }
    },

    PREVIOUS_OR_PARENT {
        <T extends OrderedTree<T, N>, N extends OrderedTreeNode<T, N>> N find(OrderedTreeNode<T, N> n) {
            if (n.hasPrevious()) {
                return n.previous();
            }
            return n.getParent();
        }
    },

    NEXT {
        <T extends OrderedTree<T, N>, N extends OrderedTreeNode<T, N>> N find(OrderedTreeNode<T, N> n) {
            if (n.hasNext()) {
                return n.next();
            }
            return null;
        }
    },
    DOWN_AND_IN {
        <T extends OrderedTree<T, N>, N extends OrderedTreeNode<T, N>> N find(OrderedTreeNode<T, N> n) {
            if (n.hasNext()) {
                return n.next();
            }
            if (n.children().size() > 0) {
                return n.firstChild();
            }
            return null;
        }
    },
    IN_AND_DOWN {
        <T extends OrderedTree<T, N>, N extends OrderedTreeNode<T, N>> N find(OrderedTreeNode<T, N> n) {
            if (n.children().size() > 0) {
                return n.firstChild();
            }
            if (n.hasNext()) {
                return n.next();
            }
            return null;
        }
    },
    IN {
        <T extends OrderedTree<T, N>, N extends OrderedTreeNode<T, N>> N find(OrderedTreeNode<T, N> n) {
            if (n.children().size() > 0) {
                return n.firstChild();
            }
            return null;
        }
    };
    abstract <T extends OrderedTree<T, N>, N extends OrderedTreeNode<T, N>> N find(OrderedTreeNode<T, N> n);
}
