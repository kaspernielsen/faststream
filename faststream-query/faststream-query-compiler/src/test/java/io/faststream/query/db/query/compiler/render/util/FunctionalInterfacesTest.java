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
package io.faststream.query.db.query.compiler.render.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import org.junit.Test;

import io.faststream.query.db.query.compiler.render.util.FunctionalInterfaces;
import io.faststream.sisyphus.util.MoreAsserts;

/**
 * Tests {@link FunctionalInterfaces}
 * 
 * @author Kasper Nielsen
 */
public class FunctionalInterfacesTest {

    @Test
    public void constructor() {
        MoreAsserts.assertUtilityClass(FunctionalInterfaces.class);
    }

    @Test
    public void getName() {
        assertEquals("accept", FunctionalInterfaces.methodNameOf(Consumer.class));
        assertEquals("apply", FunctionalInterfaces.methodNameOf(Function.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNameNoneExisting() {
        FunctionalInterfaces.methodNameOf(HashMap.class);
    }

    @Test
    public void returnType() {
        assertEquals(void.class, FunctionalInterfaces.returnTypeOf(Consumer.class));
        assertEquals(Object.class, FunctionalInterfaces.returnTypeOf(Function.class));
        assertEquals(int.class, FunctionalInterfaces.returnTypeOf(ToIntFunction.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void returnTypeNoneExisting() {
        FunctionalInterfaces.returnTypeOf(HashMap.class);
    }
}
