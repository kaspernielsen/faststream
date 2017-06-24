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
package io.faststream.query.db.queryold.node;

import static io.faststream.query.db.queryold.node.QueryOperationTestDefinitions.DEF1;
import static io.faststream.query.db.queryold.node.QueryOperationTestDefinitions.DEF2;
import static io.faststream.query.db.queryold.node.QueryOperationTestDefinitions.DEF3;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import io.faststream.query.db.query.node.AbstractTerminalQueryOperationProcessor;
import io.faststream.query.db.query.node.QueryOperationNode;
import io.faststream.query.db.query.node.QueryOperationNodeDefinition;
import io.faststream.query.db.query.node.TerminalQueryOperationNode;
import io.faststream.query.db.query.node.TerminalQueryOperationNodeProcessor;

/**
 * Tests {@link QueryOperationNode}.
 *
 * @author Kasper Nielsen
 */
public class QueryOperationTest {

    @Test
    public void root() {
        final AtomicReference<Object> ar = new AtomicReference<>();
        final TerminalQueryOperationNodeProcessor vp = new AbstractTerminalQueryOperationProcessor() {
            @Override
            public Object process(TerminalQueryOperationNode view) {
                assertNull(ar.get());
                ar.set(view);
                return "goo";
            }
        };
        Root root = new Root(vp);
        assertNull(root.previous());
        assertEquals(1234, root.getNodeId());
        assertEquals("", root.toString());
        assertEquals("", root.toFileName());
        // 1 Child
        ar.set(null);

        Child child = new Child(root);
        assertSame(root, child.previous());
        assertEquals(2345, child.getNodeId());
        assertEquals("goo", child.process());
        assertArrayEquals(new QueryOperationNode[] { child }, child.operations());
        assertEquals("Child()", child.toString());
        assertEquals("Child", child.toFileName());

        // 2 Child
        ar.set(null);

        Child2 child2 = new Child2(child);
        assertSame(child, child2.previous());
        assertEquals("firstParameter", child2.getFirstParameter());
        assertEquals(3456, child2.getNodeId());
        assertEquals("goo", child2.process());
        assertArrayEquals(new QueryOperationNode[] { child, child2 }, child2.operations());
        assertEquals("Child().Child2()", child2.toString());
        assertEquals("ChildChild2", child2.toFileName());
    }

    @SuppressWarnings("serial")
    static class Root extends QueryOperationNode {

        public Root(TerminalQueryOperationNodeProcessor processor) {
            super(processor);
        }

        /** {@inheritDoc} */
        @Override
        public int getNodeId() {
            return 1234;
        }

        /** {@inheritDoc} */
        @Override
        public int getNodeType() {
            return 0;
        }

        /** {@inheritDoc} */
        @Override
        public QueryOperationNodeDefinition getOperationPackage() {
            return DEF1;
        }

        /** {@inheritDoc} */
        @Override
        protected String name() {
            return "root";
        }
    }

    @SuppressWarnings("serial")
    static class Child extends TerminalQueryOperationNode {

        protected Child(QueryOperationNode previous) {
            super(previous);
        }

        @Override
        public QueryOperationNodeDefinition getOperationPackage() {
            return DEF2;
        }

        /** {@inheritDoc} */
        @Override
        public int getNodeType() {
            return 0;
        }

        @Override
        public int getNodeId() {
            return 2345;
        }

        /** {@inheritDoc} */
        @Override
        protected String name() {
            return "Child";
        }
    }

    @SuppressWarnings("serial")
    static class Child2 extends TerminalQueryOperationNode {

        @Override
        public Object getFirstParameter() {
            return "firstParameter";
        }

        protected Child2(QueryOperationNode previous) {
            super(previous);
        }

        /** {@inheritDoc} */
        @Override
        public int getNodeType() {
            return 0;
        }

        @Override
        public QueryOperationNodeDefinition getOperationPackage() {
            return DEF3;
        }

        @Override
        public int getNodeId() {
            return 3456;
        }

        /** {@inheritDoc} */
        @Override
        protected String name() {
            return "Child2";
        }
    }
}
