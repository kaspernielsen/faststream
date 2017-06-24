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
package io.faststream.codegen.core.helper;

import java.lang.ref.Reference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

/**
 *
 * @author Kasper Nielsen
 */
public class TestUtil {
    /**
     * A best effort tet
     *
     * @param text
     *            the error text in case the reference was not garbage collected.
     * @param ref
     *            the reference to test
     */
    public static void assertReferenceIsGCed(String text, Reference<?> ref) {
        List<byte[]> alloc = new ArrayList<>();
        int size = 100000;
        for (int i = 0; i < 50; i++) {
            if (ref.get() == null) {
                return;
            }
            try {
                System.gc();
            } catch (OutOfMemoryError ignore) {}

            try {
                System.runFinalization();
            } catch (OutOfMemoryError ignore) {}

            try {
                alloc.add(new byte[size]);
                size = (int) (size * 1.3d);
            } catch (OutOfMemoryError ignore) {
                size = size / 2;
            }

            try {
                if (i % 3 == 0) {
                    Thread.sleep(i * 5);
                }
            } catch (InterruptedException ignore) {}
        }
        Assert.fail(text);
    }

    /**
     * Returns a dummy stub of the specified type
     *
     * @param type
     *            the type of stub to return
     * @return a dummy stub of the specified type
     */
    @SuppressWarnings("unchecked")
    public static <V> V dummy(Class<V> type) {
        // return new Mockery().mock(type);
        return (V) Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();
                if ("equals".equals(name)) {
                    return proxy == args[0];
                } else if ("hashCode".equals(name)) {
                    return System.identityHashCode(proxy);
                } else if ("toString".equals(name)) {
                    return proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy))
                            + ", via InvocationHandler " + this;
                } else {
                    throw new AssertionError("Cannot invoke methods on this dummy " + name);
                }
            }
        });
    }
}
