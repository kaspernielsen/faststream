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
package org.cakeframework.internal.db.query.common.nodes.render;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 *
 * @author Kasper Nielsen
 */
class PredicateContainer<K, V> {

    private final ArrayList<Node<K, V>> list = new ArrayList<>();

    public void add(Predicate<K> predicate, V v) {
        list.add(new Node<>(predicate, v));
    }

    public V findFirst(K k) {
        for (int i = 0; i < list.size(); i++) {
            Node<K, V> n = list.get(i);
            if (n.predicate.test(k)) {
                return n.v;
            }
        }
        return null;
    }

    static class Node<K, V> {

        final Predicate<K> predicate;
        final V v;

        /**
         * @param predicate
         * @param v
         */
        public Node(Predicate<K> predicate, V v) {
            this.predicate = requireNonNull(predicate);
            this.v = requireNonNull(v);
        }
    }
}
