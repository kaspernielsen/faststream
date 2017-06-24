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
package org.cakeframework.internal.db.query.compiler.anew;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.cakeframework.internal.db.nodes.Constants;
import org.cakeframework.internal.db.query.node.QueryOperationNode;
import org.cakeframework.internal.db.query.node.TerminalQueryOperationNode;

/**
 *
 * @author Kasper Nielsen
 */
class QueryCache<T> extends AtomicReference<CachedNode[]> {
    static final int ID_MASK = 0xff0000ff;// keep left negatives bits
    // TODO we should use this for leafs, since we never write directly into an array
    // private static final CachedNode[] EMPTY_NODE = new CachedNode[LAST_NODE - FIRST_NODE + 1];
    private static final Object PROMISE = new Object();

    /** serialVersionUID. */
    private static final long serialVersionUID = -8941471052145713212L;

    QueryCache() {
        set(new CachedNode[Constants.TERMINAL_NODE_COUNT]);
    }

    private static int size(QueryOperationNode v) {
        // TODO fix return Constants.NODE_COUNT[v.getNodeType()];//
        return 30;

        // Constants.NODE_COUNT[v.getNodeType()];//
        // QueryOperationDefinitionSystem.getLastNodeIntConstant(v.definition().getFromType());
    }

    private static int id(int id) {
        return id & ID_MASK;
    }

    T lookup(TerminalQueryOperationNode topNode) {
        start: for (;;) {
            CachedNode c = get()[id(topNode.getNodeId())];
            QueryOperationNode n = topNode.previous();
            while (c != null) {
                int t = id(n.getNodeId());
                if (t < 0) {
                    Object vp = c.mapper;
                    if (vp == null || vp != PROMISE) {
                        return (T) vp;
                    } else {
                        synchronized (c) {
                            continue start;// some other thread is compiling the query, restart lookup
                        }
                    }
                } else {
                    try {
                        c = c.cache[t];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println(c.cache.length);
                        System.err.println(t);
                        System.err.println(topNode);
                        System.err.println(n.getOperationPackage().getOperation().toString());
                        throw e;
                    }
                    n = n.previous();
                }
            }
            return null;
        }
    }

    private CachedNode createNode(QueryOperationNode node, int type, Object mapper, CachedNode[] caches) {
        CachedNode c = caches[type];
        c = caches[type] = c == null ? new CachedNode(new CachedNode[size(node)], null) : new CachedNode(Arrays.copyOf(
                c.cache, c.cache.length), c.mapper);
        QueryOperationNode prev = node.previous();
        int prevType = id(prev.getNodeId());
        if (prevType < 0) {
            return caches[type] = new CachedNode(Arrays.copyOf(c.cache, c.cache.length), mapper);
        } else {
            return createNode(prev, prevType, mapper, c.cache);
        }
    }

    final T cache(Supplier<T> generator, TerminalQueryOperationNode topNode) {
        return cache(generator, topNode, topNode.operations());
    }

    T cache(Supplier<T> generator, TerminalQueryOperationNode topNode, QueryOperationNode[] nodes) {
        T vp = cache0(generator, topNode, nodes);
        if (vp != lookup(topNode)) {
            throw new Error();
        }
        return vp;
    }

    private T cache0(Supplier<T> generator, TerminalQueryOperationNode topNode, QueryOperationNode[] nodes) {
        for (;;) {
            CachedNode[] cache = get();
            CachedNode c = cache[id(topNode.getNodeId())];
            QueryOperationNode n = topNode.previous();
            while (c != null) {
                int t = id(n.getNodeId());
                if (t < 0) {
                    Object vp = c.mapper;
                    if (vp == null) {
                        break;
                    } else if (vp != PROMISE) {
                        return (T) vp;
                    } else {
                        synchronized (c) {
                            cache = null;
                            break;
                        }
                    }
                } else {
                    c = c.cache[t];
                    n = n.previous();
                }
            }
            if (cache != null) {// No promise
                // Lets first create a promise
                CachedNode[] newRoot = Arrays.copyOf(cache, cache.length);
                CachedNode cn = createNode(topNode, id(topNode.getNodeId()), PROMISE, newRoot);
                synchronized (cn) {// other threads will try to synchronize on cn for the same query
                    if (compareAndSet(cache, newRoot)) {// This thread wins
                        T e = generator.get();// compile
                        for (;;) {
                            newRoot = Arrays.copyOf(cache, cache.length);
                            cn = createNode(topNode, id(topNode.getNodeId()), e, newRoot);
                            if (compareAndSet(get(), newRoot)) {

                                return e;
                            }
                            Thread.yield();
                        }
                    }
                }
            }
        }
    }
}

class CachedNode {
    final CachedNode[] cache;
    final Object mapper;
    final Class<?>[] firstOperationType;

    CachedNode(CachedNode[] cache, Object mapper) {
        this(cache, mapper, null);
    }

    CachedNode(CachedNode[] cache, Object mapper, Class<?>[] firstOperationType) {
        this.cache = cache;
        this.mapper = mapper;
        this.firstOperationType = firstOperationType;
    }
}

class CachedSecondaryNode<T> extends CachedNode {
    final Class<?>[] firstOperationType;

    CachedSecondaryNode(CachedNode[] cache, final Class<?>[] firstOperationType) {
        super(cache, null);
        this.firstOperationType = firstOperationType;
    }
}
