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
package io.faststream.query.db.nodes.simplecaching;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import io.faststream.query.db.nodes.Constants;
import io.faststream.query.db.query.node.AbstractTerminalQueryOperationProcessor;
import io.faststream.query.db.query.node.QueryOperationNode;
import io.faststream.query.db.query.node.TerminalQueryOperationNode;
import io.faststream.query.db.query.node.TerminalQueryOperationNodeProcessor;

/**
 *
 * @author Kasper Nielsen
 */
class QueryCache extends AtomicReference<CachedNode[]> {
    static final int ID_MASK = 0xff0000ff;// keep left negatives bits
    // TODO we should use this for leafs, since we never write directly into an array
    // private static final CachedNode[] EMPTY_NODE = new CachedNode[LAST_NODE - FIRST_NODE + 1];
    private static final TerminalQueryOperationNodeProcessor PROMISE = new AbstractTerminalQueryOperationProcessor() {
        public Object process(TerminalQueryOperationNode topNode) {
            throw new UnsupportedOperationException();
        }
    };

    /** serialVersionUID. */
    private static final long serialVersionUID = -8941471052145713212L;

    QueryCache() {
        set(new CachedNode[Constants.TERMINAL_NODE_COUNT]);
    }

    private static int size(QueryOperationNode v) {
        // System.out.println(v.getClass());
        // TODO fix return Constants.NODE_COUNT[v.getNodeType()];//
        return 30;

        // Constants.NODE_COUNT[v.getNodeType()];//
        // QueryOperationDefinitionSystem.getLastNodeIntConstant(v.definition().getFromType());
    }

    private static int id(int id) {
        return id & ID_MASK;
    }

    TerminalQueryOperationNodeProcessor lookup(TerminalQueryOperationNode topNode) {
        start: for (;;) {
            CachedNode c = get()[id(topNode.getNodeId())];
            QueryOperationNode n = topNode.previous();
            while (c != null) {
                int t = id(n.getNodeId());
                if (t < 0) {
                    TerminalQueryOperationNodeProcessor vp = c.mapper;
                    if (vp == null || vp != PROMISE) {
                        return vp;
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

    private CachedNode createNode(QueryOperationNode node, int type, TerminalQueryOperationNodeProcessor mapper,
            CachedNode[] caches) {
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

    final TerminalQueryOperationNodeProcessor cache(Supplier<TerminalQueryOperationNodeProcessor> generator,
            TerminalQueryOperationNode topNode) {
        return cache(generator, topNode, topNode.operations());
    }

    TerminalQueryOperationNodeProcessor cache(Supplier<TerminalQueryOperationNodeProcessor> generator,
            TerminalQueryOperationNode topNode, QueryOperationNode[] nodes) {
        TerminalQueryOperationNodeProcessor vp = cache0(generator, topNode, nodes);
        if (vp != lookup(topNode)) {
            throw new Error();
        }
        return vp;
    }

    private TerminalQueryOperationNodeProcessor cache0(Supplier<TerminalQueryOperationNodeProcessor> generator,
            TerminalQueryOperationNode topNode, QueryOperationNode[] nodes) {
        for (;;) {
            CachedNode[] cache = get();
            CachedNode c = cache[id(topNode.getNodeId())];
            QueryOperationNode n = topNode.previous();
            while (c != null) {
                int t = id(n.getNodeId());
                if (t < 0) {
                    TerminalQueryOperationNodeProcessor vp = c.mapper;
                    if (vp == null) {
                        break;
                    } else if (vp != PROMISE) {
                        return vp;
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
                        TerminalQueryOperationNodeProcessor e = generator.get();// compile
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
    final TerminalQueryOperationNodeProcessor mapper;
    final Class<?>[] firstOperationType;

    CachedNode(CachedNode[] cache, TerminalQueryOperationNodeProcessor mapper) {
        this(cache, mapper, null);
    }

    CachedNode(CachedNode[] cache, TerminalQueryOperationNodeProcessor mapper, Class<?>[] firstOperationType) {
        this.cache = cache;
        this.mapper = mapper;
        this.firstOperationType = firstOperationType;
    }
}

class CachedSecondaryNode extends CachedNode {
    final Class<?>[] firstOperationType;

    CachedSecondaryNode(CachedNode[] cache, final Class<?>[] firstOperationType) {
        super(cache, null);
        this.firstOperationType = firstOperationType;
    }
}
//
// // Tiltaenkt en server hvor man man ikke har parents
// ViewNodeProcessor lookup(ViewNode[] nodes) {
// throw new UnsupportedOperationException();
// }

//
//
// void size(CachedNode[] nodes, Set<ViewProcessor> set) {
// for (CachedNode cn : nodes) {
// if (cn != null) {
// if (cn.mapper != null) {
// set.add(cn.mapper);
// }
// if (cn.cache != null) {
// size(cn.cache, set);
// }
// }
// }
// }
//
// //
// public void debug() {
// System.out.println("---debug---");
// debug(get(), 0, FIRST_RESULT_NODE);
//
// }
//
// private void debug(CachedNode[] cc, int level, int offset) {
// String st = StringUtil.spaces(new StringBuilder(), level).toString();
// for (int i = 0; i < cc.length; i++) {
// CachedNode c = cc[i];
// if (c != null) {
// System.out.println(st + NodeType.from(i + offset) + (c.mapper == null ? "" : " Compiled"));
// debug(c.cache, level + 1, FIRST_NODE);
// }
// }
// }
// Set<ViewProcessor> before = new HashSet<ViewProcessor>();
// size(get(), before);
// if (before.size() == 17) {
// System.out.println("ss");
// }
// Set<ViewProcessor> after = new HashSet<ViewProcessor>();
// size(get(), after);
// if (before.size() != after.size() - 1) {
// for (ViewProcessor viewProcessor : before) {
// System.out.println(viewProcessor.getClass().getSimpleName());
// }
// System.out.println("xxx");
// for (ViewProcessor viewProcessor : after) {
// System.out.println(viewProcessor.getClass().getSimpleName());
// }
// throw new Error();
// }
// System.out.println("-------");
