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
package io.faststream.query.db.query.node;

import java.io.Serializable;

/**
 * A token representing an empty result. This is typically returned by queries instead of null.
 *
 * @author Kasper Nielsen
 */
public class EmptyResult implements Serializable {

    /** A token representing the empty result. */
    public static final Object EMPTY_RESULT = new EmptyResult();

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private EmptyResult() {}

    /** @return Preserves singleton property */
    private Object readResolve() {
        return EmptyResult.EMPTY_RESULT;
    }
}
