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
package io.faststream.sisyphus;

import io.faststream.sisyphus.builder.TestBuilder;

/**
 * 
 * @author Kasper Nielsen
 */
public class PerformanceRandomTestBuilder extends TestBuilder<ExpectedPerformance, PerformanceRandomTestObject> {

    @Override
    protected void createTestSet(Bootstrap<ExpectedPerformance, PerformanceRandomTestObject> factory) {
        factory.setActual(new PerformanceRandomTestObject() {});
        factory.setExpected(new ExpectedPerformance());
    }

    public static void main(String[] args) {
        PerformanceRandomTestBuilder tb = new PerformanceRandomTestBuilder();
        // /tb.setExecutor(new ForkJoinPool(1));
        tb.start(10000000L);
    }
}
