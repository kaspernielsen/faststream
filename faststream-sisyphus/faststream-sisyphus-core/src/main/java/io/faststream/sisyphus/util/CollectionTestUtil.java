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
package io.faststream.sisyphus.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author Kasper Nielsen
 */
public class CollectionTestUtil {
    //
    // public static <T> List<T> asList(Iterable<? extends T> iterable) {
    // Iterator<? extends T> iterator = iterable.iterator();
    // ArrayList<T> result = new ArrayList<>();
    // while (iterator.hasNext()) {
    // result.add(iterator.next());
    // }
    // return result;
    // }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> List<T> sort(List<T> list) {
        Collections.sort((List) list);
        return list;
    }

    public static <T> List<T> sort(List<T> list, Comparator<T> comparator) {
        list.sort(comparator);
        return list;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> List<T> sortReverse(List<T> list) {
        Collections.sort((List) list);
        Collections.reverse(list);
        return list;
    }

}
