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

import java.util.Random;

/**
 * 
 * @author Kasper Nielsen
 */
public enum TestScale {
    NONE(0), SMALL(10), NORMAL(100), BIG(1000), HUGE(10000);

    static final TestScale[] V = values();

    private final int scale;

    TestScale(int scale) {
        this.scale = scale;
    }

    public int getScale() {
        return scale;
    }

    public int getSize(Random random) {
        return scale == 0 ? 0 : random.nextInt(scale);
    }

    public static TestScale defaultScale() {
        return ExecutionEnvironment.CURRENT.scale;
    }
}
