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
package org.cakeframework.internal.db.query.common.nodes.elements;

import org.cakeframework.internal.db.query.compiler.datasource.ArrayOrListComposite;
import org.cakeframework.internal.db.query.compiler.datasource.Composite;
import org.cakeframework.internal.db.query.compiler.datasource.StreamingComposite;
import org.cakeframework.internal.db.query.compiler.render.util.StreamingGroup;

import io.faststream.codegen.model.expression.Expression;
import io.faststream.codegen.model.util.ForAllI;

/**
 *
 * @author Kasper Nielsen
 */
public class ForAll extends StreamingGroup {

    ForAllI i;

    /**
     * @return the i
     */
    public ForAllI getI() {
        return i;
    }

    /** {@inheritDoc} */
    @Override
    public void buildModel() {
        Composite v = sources().getMain();
        if (v instanceof ArrayOrListComposite) {
            ArrayOrListComposite composite = (ArrayOrListComposite) v;
            if (i == null) {
                i = ForAllI.create(composite.getLowerBound(), composite.getUpperBound());
                b().add(i.getForStatement());
            }

            Expression var = composite.getElement(i.getI());
            firstChild().sources().setMain(new StreamingComposite(composite.getComponentType(), var));

            children().forEach(c -> c.setIn(i.getBlock()));

        } else {
            throw new RuntimeException("" + v.getClass());
        }

        // QueryNode n = firstChild();
        // if (n.sources().getMain() == null) {
        // n.sources().setMain(this.sources().getMain());
        // }
        renderChildren();
    }
}
