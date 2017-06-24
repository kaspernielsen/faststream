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

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Kasper Nielsen
 */
public class OrderedTreePath<T extends OrderedTree<T, N>, N extends OrderedTreeNode<T, N>> {

    private final List<N> nodes;

    OrderedTreePath(List<N> nodes) {
        this.nodes = requireNonNull(nodes);
    }

    public boolean allMatch(Predicate<? super N> predicate) {
        return nodes.stream().allMatch(predicate);
    }

    public boolean anyMatch(Predicate<? super N> predicate) {
        return nodes.stream().anyMatch(predicate);
    }

    public boolean noneMatch(Predicate<? super N> predicate) {
        return nodes.stream().noneMatch(predicate);
    }

    public void forEach(Consumer<? super N> consumer) {
        nodes.stream().forEach(consumer);
    }

    public Stream<N> stream() {
        return nodes.stream();
    }

    public String toString() {
        return stream().map(e -> e.toString()).collect(Collectors.joining(", "));
    }
}
