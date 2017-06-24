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
package io.faststream.sisyphus.stubs;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A dummy comparator.
 * 
 * @author Kasper Nielsen
 */
public class DummyComparator implements Comparator<DummyComparator.DummyObject>, Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    public int compare(DummyObject o1, DummyObject o2) {
        return o1.i < o2.i ? -1 : (o1.i == o2.i ? 0 : 1);
    }

    /** A dummy object. */
    public static final class DummyObject implements Serializable {
        public static final DummyObject D1 = new DummyObject(1);

        public static final DummyObject D2 = new DummyObject(2);

        public static final DummyObject D3 = new DummyObject(3);

        public static final DummyObject D4 = new DummyObject(4);

        public static final DummyObject D5 = new DummyObject(5);

        /** serialVersionUID */
        private static final long serialVersionUID = -3644310163377995269L;

        final int i;

        private DummyObject(int i) {
            this.i = i;
        }
    }

}
