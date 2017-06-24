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
package org.cakeframework.test.random.view.mapview;

import io.faststream.sisyphus.annotations.FailWith;

/**
 *
 * @author Kasper Nielsen
 */
public class IllegalArguments<K, V> extends AbstractMapViewRandomTestCase<K, V> {

   @FailWith(NullPointerException.class)
    public void filterNPE() {
        actual().filter(null);
    }

   @FailWith(NullPointerException.class)
    public void filterOnKeyNPE() {
        actual().filterOnKey(null);
    }

   @FailWith(NullPointerException.class)
    public void filterOnKeyTypeNPE() {
        actual().filterOnKeyType(null);
    }

   @FailWith(NullPointerException.class)
    public void filterOnValueNPE() {
        actual().filterOnValue(null);
    }

   @FailWith(NullPointerException.class)
    public void filterOnValueTypeNPE() {
        actual().filterOnValueType(null);
    }

   @FailWith(NullPointerException.class)
    public void mapKeyNPE() {
        actual().mapKey(null);
    }

   @FailWith(NullPointerException.class)
    public void mapNPE() {
        actual().map(null);
    }

   @FailWith(NullPointerException.class)
    public void mapValueNPE() {
        actual().mapValue(null);
    }

   @FailWith(NullPointerException.class)
    public void sortByKeyNPE() {
        actual().sortedByKey(null);
    }

   @FailWith(NullPointerException.class)
    public void sortByValueNPE() {
        actual().sortedByValue(null);
    }

   @FailWith(NullPointerException.class)
    public void forEachNPE() {
        actual().forEach(null);
    }

   @FailWith(NullPointerException.class)
    public void sortNPE() {
        actual().sorted(null);
    }

   @FailWith(NullPointerException.class)
    public void toNullArgument() {
        actual().to(null);
    }

    @FailWith(IllegalArgumentException.class)
    public void takeZero() {
        actual().take(0);
    }
}
