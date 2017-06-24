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
package io.faststream.sisyphus.javautil.stream.object;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

import io.faststream.sisyphus.annotations.FailWith;

/**
 * Tests various null arguments to streams.
 * 
 * @author Kasper Nielsen
 */
public class IllegalArguments<E> extends AbstractRandomStreamTestCase<E> {

    @FailWith(NullPointerException.class)
    public void allMatchNPE() {
        actual().allMatch(null);
    }

    @FailWith(NullPointerException.class)
    public void anyCollect1NPE() {
        actual().collect(null, (e, a) -> {} , (e, a) -> {});
    }

    @FailWith(NullPointerException.class)
    public void anyCollect2NPE() {
        actual().collect(() -> "R", null, (e, a) -> {});
    }

    @FailWith(NullPointerException.class)
    public void anyCollect3NPE() {
        actual().collect(() -> "R", (e, a) -> {} , null);
    }

    @FailWith(NullPointerException.class)
    public void anyMatchNPE() {
        actual().anyMatch(null);
    }

    @FailWith(NullPointerException.class)
    public void collectNPE() {
        actual().collect(null);
    }

    @FailWith(NullPointerException.class)
    public void filterNPE() {
        actual().filter(null);
    }

    @FailWith(NullPointerException.class)
    public void flatMapNPE() {
        actual().flatMap(null);
    }

    @FailWith(NullPointerException.class)
    public void flatMapToDoubleNPE() {
        actual().flatMapToDouble(null);
    }

    @FailWith(NullPointerException.class)
    public void flatMapToIntNPE() {
        actual().flatMapToInt(null);
    }

    @FailWith(NullPointerException.class)
    public void flatMapToLongNPE() {
        actual().flatMapToLong(null);
    }

    // @Test(expected = NullPointerException.class)
    // public void forEachNPE() {
    // actual().forEach(null);
    // }
    //
    // @Test(expected = NullPointerException.class)
    // public void forEachOrderedNPE() {
    // actual().forEachOrdered(null);
    // }

    @FailWith(IllegalArgumentException.class)
    public void limitNPE() {
        actual().limit(-1);
    }

    @FailWith(NullPointerException.class)
    public void mapNPE() {
        actual().map(null);
    }

    @FailWith(NullPointerException.class)
    public void mapToDoubleNPE() {
        actual().mapToDouble(null);
    }

    @FailWith(NullPointerException.class)
    public void mapToIntNPE() {
        actual().mapToInt(null);
    }

    @FailWith(NullPointerException.class)
    public void mapToLongNPE() {
        actual().mapToLong(null);
    }

    @FailWith(NullPointerException.class)
    public void maxNPE() {
        actual().max(null);
    }

    @FailWith(NullPointerException.class)
    public void minNPE() {
        actual().min(null);
    }

    @FailWith(NullPointerException.class)
    public void noneMatchNPE() {
        actual().noneMatch(null);
    }

    // @Test(expected = NullPointerException.class)
    // public void onCloseNPE() {
    // actual().onClose(null);
    // }

    @FailWith(NullPointerException.class)
    public void peekNPE() {
        actual().peek(null);
    }

    @FailWith(NullPointerException.class)
    public void reduce2NPE() {
        actual().reduce(null, null);
    }

    @FailWith(NullPointerException.class)
    public void reduce31NPE() {
        actual().reduce(null, null, (a, b) -> a);
    }

    @FailWith(NullPointerException.class)
    public void reduce32NPE() {
        actual().reduce(null, (a, b) -> a, null);
    }

    @FailWith(NullPointerException.class)
    public void reduceNPE() {
        actual().reduce(null);
    }

    @FailWith(IllegalArgumentException.class)
    public void skipNPE() {
        actual().skip(-1);
    }

    // @Test(expected = NullPointerException.class)
    // public void sortedNPE() {
    // actual().sorted(null);
    // }

    // @Test(expected = NullPointerException.class)
    // public void toArrayNPE() {
    // actual().toArray(null);
    // }

    public static void madin(String[] args) {
        Method[] ms = Stream.class.getMethods();
        Arrays.sort(ms, (a, b) -> a.getName().compareTo(b.getName()));

        for (Method m : ms) {
            if (m.getParameters().length == 1 && !Modifier.isStatic(m.getModifiers())) {
                System.out.println("@Test(expected = NullPointerException.class)");
                System.out.println("public void " + m.getName() + "NPE() {");
                System.out.println("actual()." + m.getName() + "(null);");
                System.out.println("}");
            }
        }
        for (Method m : ms) {
            if (m.getParameters().length > 1 && !Modifier.isStatic(m.getModifiers())) {
                // System.err.println(m);
            }
        }

    }
}
