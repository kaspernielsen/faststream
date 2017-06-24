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
package io.faststream.codegen.core;

import org.junit.Test;

import io.faststream.codegen.core.CodegenClass;

/**
 * 
 * @author Kasper Nielsen
 */
public class CodegenAbstractTest extends AbstractCodegenTest {

    @Test
    public void abstractClass() {
        c.newClass("public abstract class Test").compile();
    }

    @Test
    public void abstractMethod() {
        CodegenClass clz = c.newClass("public abstract class Test");
        clz.addMethod("public abstract void foo()");
        clz.compile();
    }
}
