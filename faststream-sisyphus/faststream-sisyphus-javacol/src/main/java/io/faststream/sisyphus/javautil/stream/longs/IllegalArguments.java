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
package io.faststream.sisyphus.javautil.stream.longs;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.LongStream;

import io.faststream.sisyphus.annotations.FailWith;

/**
 *
 * @author Kasper Nielsen
 */
public class IllegalArguments extends AbstractRandomLongStreamTestCase {
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

    // @Test(expected = NullPointerException.class)
    // public void anyCollect3NPE() {
    // actual().collect(() -> "R", (e, a) -> {}, null);
    // }

    @FailWith(NullPointerException.class)
    public void anyMatchNPE() {
        actual().anyMatch(null);
    }

    @FailWith(NullPointerException.class)
    public void filterNPE() {
        actual().filter(null);
    }

    // @Test(expected = NullPointerException.class)
    // public void flatMapNPE() {
    // actual().flatMap(null);
    // }

    @FailWith(NullPointerException.class)
    public void forEachNPE() {
        actual().forEach(null);
    }

    @FailWith(NullPointerException.class)
    public void forEachOrderedNPE() {
        actual().forEachOrdered(null);
    }

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
    public void mapToObjNPE() {
        actual().mapToObj(null);
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
    public void reduce1NPE() {
        actual().reduce(0, null);
    }

    @FailWith(NullPointerException.class)
    public void reduceNPE() {
        actual().reduce(null);
    }

    @FailWith(IllegalArgumentException.class)
    public void skipNPE() {
        actual().skip(-1);
    }

    public static void main(String[] args) {
        Method[] ms = LongStream.class.getMethods();
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

        for (Method m : ms) {
            if (!Modifier.isStatic(m.getModifiers())) {
                System.out.println(m);
            }
        }
    }
}
