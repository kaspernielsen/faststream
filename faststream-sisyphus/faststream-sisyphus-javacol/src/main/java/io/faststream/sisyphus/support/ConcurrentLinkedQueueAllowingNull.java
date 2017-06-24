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
package io.faststream.sisyphus.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/**
 *
 * @author Kasper Nielsen
 */
class ConcurrentLinkedQueueAllowingNull<V> implements Consumer<V>, LongConsumer {
    private static final Object NULL_OBJECT = new Object();

    final ConcurrentLinkedQueue<Object> q = new ConcurrentLinkedQueue<>();// Does not allow null

    @SuppressWarnings("unchecked")
    public Collection<V> toCollection() {
        ArrayList<Object> list = new ArrayList<>(q);
        Collections.replaceAll(list, NULL_OBJECT, null);
        return (Collection<V>) list;
    }

    public void accept(V element) {
        q.add(element == null ? NULL_OBJECT : element);
    }

    /** {@inheritDoc} */
    @Override
    public void accept(long value) {

    }
}
