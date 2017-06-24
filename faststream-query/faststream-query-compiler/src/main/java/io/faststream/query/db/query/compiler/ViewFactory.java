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
package io.faststream.query.db.query.compiler;

/**
 * Okay der er problemer med ting der ikke bare er iterable... men f.eks. har en private size felt. Vi kan ikke definere
 * views'ene i samme pakke som hoved datastrukturen fordi saa kan den ikke unloades. Tror vi maa lave en abstract
 * klasser der kan laese felterne
 *
 * @author Kasper Nielsen
 */
public interface ViewFactory<T> {

    /**
     * @param sourceInstances
     * @return
     */
    T create(Object... sourceInstances);

}
