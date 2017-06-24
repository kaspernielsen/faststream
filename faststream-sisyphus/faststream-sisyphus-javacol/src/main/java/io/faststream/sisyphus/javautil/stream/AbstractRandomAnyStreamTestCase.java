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
package io.faststream.sisyphus.javautil.stream;

import java.math.BigDecimal;
import java.util.function.BinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.BaseStream;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.Assert;

import io.faststream.sisyphus.javautil.stream.object.ExpectedStream;
import io.faststream.sisyphus.spi.TestCase;

/**
 * @param <E>
 *            the type of elements in the stream
 * @param <S>
 *            the type of elements in the stream
 * @author Kasper Nielsen
 */
public abstract class AbstractRandomAnyStreamTestCase<E, S extends BaseStream<E, S>> extends TestCase<ExpectedStream<E>, S> {

    public static final double VAR = 0.001;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected final int reduceInt(int identity, IntBinaryOperator op) {
        return (Integer) reduce((E) Integer.valueOf(identity), (BinaryOperator) op);
    }

    protected final E reduce(E identity, BinaryOperator<E> op) {
        return expected().reduce(identity, op);
    }

    protected static void assertEqualsVar(double expected, double actual) {
        try {
            Assert.assertEquals(expected, actual, Math.abs(expected * 0.00001));
        } catch (Throwable cause) {
            System.out.println();
            System.out.println(new BigDecimal(expected));
            System.out.println(new BigDecimal(actual));
            System.out.println(new BigDecimal(expected * 0.00001));
            System.out.println(expected - actual);
            throw cause;
        }
    }

    public final boolean isOrdered() {
        return expected().isOrdered();
    }

    public final void consumed() {
        expected().context().verifyOnConsume();
        streamTerminate();
    }

    /** Special values for which the take() operation might fail. */
    static long[] SKIPLIMIT_SPECIAL_VALUES = new long[] { Long.MAX_VALUE, Long.MAX_VALUE - 1, Integer.MAX_VALUE, Integer.MAX_VALUE + 1L,
            Integer.MAX_VALUE - 1 };

    /**
     * Select a random parameter for the {@link IntStream#limit(long)}, {@link IntStream#skip(long)},
     * {@link LongStream#limit(long)}, {@link LongStream#skip(long)}, {@link DoubleStream#limit(long)},
     * {@link DoubleStream#skip(long)} ,{@link Stream#limit(long)} and {@link Stream#skip(long)}.
     * <p>
     * The idea is to find a value that exposes +1 errors and corner cases.
     *
     * @param elementCount
     *            the number of expected elements in the current
     * @return the parameter for the take command
     */
    public long nextSkipLimit() {
        int elementCount = expected().size();
        double d = random().nextDouble();
        if (d < .20) {
            return SKIPLIMIT_SPECIAL_VALUES[random().nextInt(0, SKIPLIMIT_SPECIAL_VALUES.length)];
        } else if (d < .25) {
            return 0;
        } else if (d < .30) {
            return elementCount + 1;
        } else {
            return random().nextInt(1, Math.max(2, elementCount));
        }
    }

    public int flatStreamSize() {
        return 1;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> T l(T function, String msg) {
        // @formatter:off

        if (function instanceof ToLongFunction) {
            ToLongFunction f = (ToLongFunction) function;
            return (T) new ToLongFunction() {
                public long applyAsLong(Object value) {return f.applyAsLong(value);}
                public String toString() {return msg;}
            };
        } else if (function instanceof LongFunction) {
            LongFunction f = (LongFunction) function;
            return (T) new LongFunction() {
                public Object apply(long value) {return f.apply(value);}
                public String toString() {return msg;}
            };
        } else if (function instanceof LongToIntFunction) {
            LongToIntFunction f = (LongToIntFunction) function;
            return (T) new LongToIntFunction() {
                public int applyAsInt(long value) {return f.applyAsInt(value);}
                public String toString() {return msg;}
            };
        } else if (function instanceof LongToDoubleFunction) {
            LongToDoubleFunction f = (LongToDoubleFunction) function;
            return (T) new LongToDoubleFunction() {
                public double applyAsDouble(long value) {return f.applyAsDouble(value);}
                public String toString() {return msg;}
            };
        } else if (function instanceof LongUnaryOperator) {
            LongUnaryOperator f = (LongUnaryOperator) function;
            return (T) new LongUnaryOperator() {
                public long applyAsLong(long value) {return f.applyAsLong(value);}
                public String toString() {return msg;}
            };
        }  else
        
        if (function instanceof ToDoubleFunction) {
            ToDoubleFunction f = (ToDoubleFunction) function;
            return (T) new ToDoubleFunction() {
                public double applyAsDouble(Object value) {return f.applyAsDouble(value);}
                public String toString() {return msg;}
            };
        } else if (function instanceof DoubleFunction) {
            DoubleFunction f = (DoubleFunction) function;
            return (T) new DoubleFunction() {
                public Object apply(double value) {return f.apply(value);}
                public String toString() {return msg;}
            };
        } else if (function instanceof DoubleToIntFunction) {
            DoubleToIntFunction f = (DoubleToIntFunction) function;
            return (T) new DoubleToIntFunction() {
                public int applyAsInt(double value) {return f.applyAsInt(value);}
                public String toString() {return msg;}
            };
        } else if (function instanceof DoubleToLongFunction) {
            DoubleToLongFunction f = (DoubleToLongFunction) function;
            return (T) new DoubleToLongFunction() {
                public long applyAsLong(double value) {return f.applyAsLong(value);}
                public String toString() {return msg;}
            };
        } else if (function instanceof DoubleUnaryOperator) {
            DoubleUnaryOperator f = (DoubleUnaryOperator) function;
            return (T) new DoubleUnaryOperator() {
                public double applyAsDouble(double value) {return f.applyAsDouble(value);}
                public String toString() {return msg;}
            };
        } else
            
            if (function instanceof ToIntFunction) {
                ToIntFunction f = (ToIntFunction) function;
                return (T) new ToIntFunction() {
                    public int applyAsInt(Object value) {return f.applyAsInt(value);}
                    public String toString() {return msg;}
                };
            } else if (function instanceof IntFunction) {
                IntFunction f = (IntFunction) function;
                return (T) new IntFunction() {
                    public Object apply(int value) {return f.apply(value);}
                    public String toString() {return msg;}
                };
            } else if (function instanceof IntToDoubleFunction) {
                IntToDoubleFunction f = (IntToDoubleFunction) function;
                return (T) new IntToDoubleFunction() {
                    public double applyAsDouble(int value) {return f.applyAsDouble(value);}
                    public String toString() {return msg;}
                };
            } else if (function instanceof IntToLongFunction) {
                IntToLongFunction f = (IntToLongFunction) function;
                return (T) new IntToLongFunction() {
                    public long applyAsLong(int value) {return f.applyAsLong(value);}
                    public String toString() {return msg;}
                };
            } else if (function instanceof IntUnaryOperator) {
                IntUnaryOperator f = (IntUnaryOperator) function;
                return (T) new IntUnaryOperator() {
                    public int applyAsInt(int value) {return f.applyAsInt(value);}
                    public String toString() {return msg;}
                };
            } 
        // @formatter:on
        throw new AssertionError("Unknown type " + function);
    }
}
