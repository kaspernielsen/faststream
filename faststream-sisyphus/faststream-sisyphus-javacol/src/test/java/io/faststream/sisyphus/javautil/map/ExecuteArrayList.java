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
package io.faststream.sisyphus.javautil.map;

import java.util.ArrayList;
import java.util.Collection;

import io.faststream.sisyphus.javautil.CollectionRandomTestBuilder;

/**
 * 
 * @author Kasper Nielsen
 */
public class ExecuteArrayList {

    public static void main(String[] args) {
        CollectionRandomTestBuilder<Integer> builder = new CollectionRandomTestBuilder<Integer>() {
            protected Collection<Integer> createActual(Collection<Integer> bootstrap) {
                // System.out.println(bootstrap);
                return new ArrayList<>(bootstrap);
            }
        };

        builder.setReproduce(true);
        builder.setInitialSeed(106);
        builder.setExecutor(4);
        try {
            builder.start(100_000_000);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        // builder.create(106L).stream().mapToLong(e ->
        // e.hashCode()).parallel().sorted().filter(HashcodeModPredicate.from(2)).skip(2147483647).allMatch(HashcodeModPredicate.from(2));
        // XXXXXXXXX 106 461526
        // System.out.println(TestCaseContext.current().getI());
    }

    public static void madin(String[] args) {
        int min = -1;
        int minVal = 10_000_000;
        for (int i = 104; i < 100000; i++) {
            CollectionRandomTestBuilder<Integer> builder = new CollectionRandomTestBuilder<Integer>() {
                protected Collection<Integer> createActual(Collection<Integer> bootstrap) {
                    return new ArrayList<>(bootstrap);
                }
            };

            builder.setReproduce(true);
            builder.setInitialSeed(i);
            builder.setExecutor(1);
            try {
                builder.start(minVal + 5);
            } catch (Throwable t) {
                // t.printStackTrace();
            }
            // System.out.println("XXXXXXXXX " + i + " " + TestCaseContext.current().getI());
            // if (TestCaseContext.current().getI() < minVal) {
            // minVal = TestCaseContext.current().getI();
            // min = i;
            // }
            System.out.println("XXXXXXMIN " + min + " " + minVal);
        }

    }
}
