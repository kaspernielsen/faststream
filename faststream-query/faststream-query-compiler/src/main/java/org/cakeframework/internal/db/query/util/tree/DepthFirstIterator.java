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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 *
 * @author Kasper Nielsen
 */
class DepthFirstIterator<T extends OrderedTree<T, N>, N extends OrderedTreeNode<T, N>> extends AbstractIterator<N> {
    private final N root;
    private final Set<N> visited = Collections.newSetFromMap(new IdentityHashMap<N, Boolean>());

    DepthFirstIterator(N root) {
        super(root);
        this.root = root;
    }

    /** {@inheritDoc} */
    @Override
    public N next0(N next) {
        N n = next00(next);
        while (n != null) {
            if (visited.contains(n)) {
                n = next00(next);
            } else {
                visited.add(n);
                break;
            }
        }
        return n;

    }

    private N next00(N next) {
        N firstChild = next.firstChild();
        if (firstChild != null) {
            return firstChild;
        } else if (next == root) {
            return null;
        }
        N n = next.next();
        while (n == null) {
            next = next.parent;
            if (next == null || next == root) {
                return null;
            }
            n = next.next();
        }
        return n;
    }

    static <T extends OrderedTree<T, N>, N extends OrderedTreeNode<T, N>> Iterable<N> newIterable(final N node) {
        return new DepthFirstIterator<>(node).asIterable();
    }
}
