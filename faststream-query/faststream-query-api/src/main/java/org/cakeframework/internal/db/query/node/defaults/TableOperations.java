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
package org.cakeframework.internal.db.query.node.defaults;

import static org.cakeframework.internal.db.query.node.Operation.of;

import org.cakeframework.internal.db.query.node.Operation;

/**
 * Well no s
 *
 * @author Kasper Nielsen
 */
public interface TableOperations {
    public static final Operation T = of("T");
    // NumberOfRows
    public static final Operation TT_SIZE = of("TT_SIZE", T);

}
