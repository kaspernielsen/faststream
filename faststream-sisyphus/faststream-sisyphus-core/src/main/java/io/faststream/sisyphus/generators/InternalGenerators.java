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
package io.faststream.sisyphus.generators;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertTrue;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import io.faststream.sisyphus.util.ClassTestUtil;
import io.faststream.sisyphus.util.RandomSource;

/**
 * 
 * @author Kasper Nielsen
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class InternalGenerators {

    static InternalGenerators INCREMENTAL = new InternalGenerators();
    static InternalGenerators RANDOM = new InternalGenerators();

    static {
        // @formatter:off
        RANDOM.add(new ElementGenerator<Boolean>() {public Boolean get() {return RandomSource.current().nextBoolean();}}, Boolean.class);
        RANDOM.add(new ElementGenerator<Byte>() {public Byte get() {return (byte) RandomSource.current().nextInt();}}, Byte.class);
        RANDOM.add(new ElementGenerator<Character>() {public Character get() {return (char) RandomSource.current().nextInt();}}, Character.class);
        RANDOM.add(new ElementGenerator<Double>() {public Double get() {return RandomSource.current().nextDouble();}}, Double.class);
        RANDOM.add(new ElementGenerator<Long>() {public Long get() {return RandomSource.current().nextLong();}}, Long.class);
        RANDOM.add(new ElementGenerator<Integer>() {public Integer get() {return RandomSource.current().nextInt();}}, Integer.class);
        RANDOM.add(new ElementGenerator<Float>() {public Float get() {return RandomSource.current().nextFloat();}}, Float.class);
        RANDOM.add(new ElementGenerator<Short>() {public Short get() {return (short) RandomSource.current().nextInt();}}, Short.class);
        RANDOM.add(new ElementGenerator<String>() {public String get() {return RandomSource.current().nextString(6);}}, String.class);
        RANDOM.add(new ElementGenerator<Map.Entry<Integer, String>>() {public Map.Entry<Integer, String> get() {
            return new AbstractMap.SimpleImmutableEntry<>(RandomSource.current().nextInt(), RandomSource.current().nextString(6));}},(Class) Map.Entry.class);


        final AtomicLong dLong = new AtomicLong();
        final AtomicLong lLong = new AtomicLong();
        final AtomicInteger iInteger = new AtomicInteger();
        final AtomicInteger iFloat = new AtomicInteger();
        INCREMENTAL.add(new ElementGenerator<Double>() {public Double get() {return Double.longBitsToDouble(dLong.incrementAndGet());}}, Double.class);
        INCREMENTAL.add(new ElementGenerator<Long>() {public Long get() {return lLong.incrementAndGet();}}, Long.class);
        INCREMENTAL.add(new ElementGenerator<Integer>() {public Integer get() {return iInteger.incrementAndGet();}}, Integer.class);
        INCREMENTAL.add(new ElementGenerator<Float>() {public Float get() {return Float.intBitsToFloat(iFloat.incrementAndGet());}}, Float.class);
        // @formatter:on
    }

    volatile List<Wrapper> generators = new ArrayList<>();

    private final ConcurrentHashMap<Class<?>, List<Wrapper>> wrappersForType = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<?>, List<Class<?>>> assignableTo = new ConcurrentHashMap<>();

    synchronized <T> void add(ElementGenerator<T> generator, Class<T> type) {
        Wrapper wrapper = new Wrapper(generator, boxClass(type));
        List<Wrapper> wrappers = new ArrayList<>(generators);
        for (Wrapper w : wrappers) {
            if (w.type == wrapper.type) {
                throw new IllegalArgumentException("A generator for the specified type has already been added, type = " + type);
            }
        }
        wrappers.add(wrapper);
        generators = wrappers;
        wrappersForType.clear();// Clear cache
    }

    Wrapper getWrapper(Class<?> type) {
        assert!type.isPrimitive();
        List<Wrapper> l = wrappersForType.get(type);
        if (l == null) {
            l = new ArrayList<>();
            for (Wrapper t : generators) {
                if (t.assignableTo.contains(type)) {
                    l.add(t);
                }
            }
            assertTrue("Do not know how to create instances of " + type, l.size() > 0);
            wrappersForType.put(type, l);
        }
        return RandomSource.current().nextElement(l);
    }

    /**
     * Returns a random object that is not assignable to {@link #getContainerType()}.
     * 
     * @return
     */
    final ElementGenerator<Object> incompatibleType(final Class<?> type) {
        assert!type.isPrimitive();
        if (type == Object.class) {
            throw new AssertionError("Object.class is compatible with all objects");
        }
        return new ElementGenerator<Object>() {
            @Override
            public Object get() {
                for (int i = 0; i < 10000; i++) {
                    Wrapper w = RandomSource.current().nextElement(generators);
                    if (!w.type.isAssignableFrom(type)) {
                        w.factory.get();
                    }
                }
                throw new AssertionError("Looks like the test is running an infinite loop");
            }
        };
    }

    final <T> Class<? super T> randomAssignableType(final Class<T> type) {
        List<Class<?>> list = assignableTo.computeIfAbsent(type, new Function<Class<?>, List<Class<?>>>() {
            public List<Class<?>> apply(Class<?> t) {
                return Collections.unmodifiableList(new ArrayList<>(ClassTestUtil.findAllInterfacesAndSuperClasses(type)));
            }
        });
        return (Class<? super T>) RandomSource.current().nextElement(list);
    }

    <T> ElementGenerator<T> randomGenerator(Class<T> type) {
        return (ElementGenerator<T>) getWrapper(type).factory;
    }

    static <T> Class<T> boxClass(Class<T> type) {
        if (type == boolean.class) {
            return (Class<T>) Boolean.class;
        } else if (type == byte.class) {
            return (Class<T>) Byte.class;
        } else if (type == char.class) {
            return (Class<T>) Character.class;
        } else if (type == double.class) {
            return (Class<T>) Double.class;
        } else if (type == float.class) {
            return (Class<T>) Float.class;
        } else if (type == int.class) {
            return (Class<T>) Integer.class;
        } else if (type == long.class) {
            return (Class<T>) Long.class;
        } else if (type == short.class) {
            return (Class<T>) Short.class;
        } else {
            return type;
        }
    }

    static class Wrapper {
        final List<Class<?>> assignableTo;
        final ElementGenerator<?> factory;
        final Class<?> type;

        Wrapper(ElementGenerator<?> factory, Class<?> type) {
            this.factory = requireNonNull(factory);
            this.type = requireNonNull(type);
            assignableTo = Collections.unmodifiableList(new ArrayList<>(ClassTestUtil.findAllInterfacesAndSuperClasses(type)));
        }
    }
}
