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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.faststream.sisyphus.javautil.MapRandomTestBuilder;

/**
 * 
 * @author Kasper Nielsen
 */
public class MapTest {

    @Test
    public void test() {
        MapRandomTestBuilder<String, Integer> ctc = new MapRandomTestBuilder<String, Integer>() {
            protected Map<String, Integer> createActual(Map<String, Integer> bootstrap) {
                System.out.println(bootstrap);
                return new HashMap<>(bootstrap);
            }
        };
        ctc.setExecutor(1);
        // TODO virker ikke med seed 1236.
        ctc.setInitialSeed(1233);
        ctc.start(10000);
    }
}
