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
package io.faststream.query.db.query.compiler.render;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;

import io.faststream.codegen.core.Codegen;
import io.faststream.codegen.core.CodegenClass;

/**
 *
 * @author Kasper Nielsen
 */
public class ViewRender {
    final ViewRender parent;
    final CodegenClass cc;
    final Codegen codegen;

    final ArrayList<ViewRender> children = new ArrayList<>();
    final ArrayList<Runnable> renders = new ArrayList<>();

    private ViewRender(ViewRender parent) {
        this.codegen = parent.codegen;
        this.parent = parent;
        this.cc = parent.cc.addInnerClass();
        parent.children.add(this);
    }

    public ViewRender(Codegen codegen) {
        this.codegen = requireNonNull(codegen);
        this.cc = codegen.newClass();
        this.parent = null;
    }

    public CodegenClass cc() {
        return cc;
    }

    public void render() {
        renders.forEach(e -> e.run());
        children.forEach(e -> e.render());
    }

    public void addRender(Runnable r) {
        renders.add(requireNonNull(r));
    }

    public ViewRender newChild() {
        return new ViewRender(this);
    }
}
