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
package org.cakeframework.test.random.view.collectionview;

import io.faststream.sisyphus.annotations.FailWith;

/**
 *
 * @author Kasper Nielsen
 */
public class IllegalArguments<E> extends AbstractCollectionViewRandomTestCase<E> {
   @FailWith(NullPointerException.class)
    public void applyNullArgument() {
        actual().forEach(null);
    }

   @FailWith(NullPointerException.class)
    public void filterNullArgument() {
        actual().filter(null);
    }

   @FailWith(NullPointerException.class)
    public void filterOnTypeNullArgument() {
        actual().filterOnType(null);
    }

   @FailWith(NullPointerException.class)
    public void gatherNPE() {
        actual().gather(null);
    }

   @FailWith(NullPointerException.class)
    public void mapNPE() {
        actual().map(null);
    }

   @FailWith(NullPointerException.class)
    public void sortNPE() {
        actual().sorted(null);
    }

    @FailWith(IllegalArgumentException.class)
    public void takeZero() {
        actual().take(0);
    }

   @FailWith(NullPointerException.class)
    public void toNPE() {
        actual().to(null);
    }

   @FailWith(NullPointerException.class)
    public void groupByNullArgument() {
        actual().groupBy(null);
    }

   @FailWith(NullPointerException.class)
    public void reduceNullArgument() {
        actual().reduce(null);
    }

   @FailWith(NullPointerException.class)
    public void reduceNullArgument2() {
        actual().reduce(getBase(), null);
    }
}
