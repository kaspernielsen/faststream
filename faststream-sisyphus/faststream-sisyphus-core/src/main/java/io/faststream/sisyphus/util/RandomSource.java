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
package io.faststream.sisyphus.util;

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * A random implementation that allows for getting and setting the seed. Unlike Random this implementation is not thread
 * safe.
 * 
 * @author Kasper Nielsen
 */
@SuppressWarnings("serial")
public final class RandomSource extends Random {

    private static final long MULTIPLIER = 0x5deece66dL;
    private static final long SEED_MASK = (1L << 48);

    private static final ThreadLocal<RandomSource> TL = new ThreadLocal<>();

    /** The seed. */
    private long seed;

    private RandomSource(long seed) {
        this.seed = seed;
    }

    public long getSeed() {
        return seed;
    }

    protected int next(int bits) {
        seed = seed * MULTIPLIER + 0xbL & SEED_MASK - 1;
        return (int) (seed >>> 48 - bits);
    }

    public float nextGaussian(int mean, float std) {
        return Math.abs((float) nextGaussian() * std + mean);
    }

    public RandomSource split() {
        return new RandomSource(seed);
    }

    /**
     * Returns a pseudorandom, uniformly distributed value between the given least value (inclusive) and bound
     * (exclusive).
     * 
     * @param least
     *            the least value returned
     * @param bound
     *            the upper bound (exclusive)
     * @throws IllegalArgumentException
     *             if least is greater than or equal to bound
     * @return the next value
     */
    public int nextInt(int least, int bound) {
        if (least >= bound) {
            throw new IllegalArgumentException("least = " + least + ", bound = " + bound);
        }
        return nextInt(bound - least) + least;
    }

    public long nextLong(long n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive, was " + n);
        }
        if (n <= Integer.MAX_VALUE) {
            return nextInt((int) n);
        } else if (n <= (long) Integer.MAX_VALUE << 1L + 1) { // No more than 32 bits are needed
            for (;;) {
                long val = next(32) & 0xffffffffL;
                if (val < n) {
                    return val;
                }
            }
        }
        int bits = 32 - Integer.numberOfLeadingZeros((int) (n - 1 >>> 32));
        for (;;) {
            long msb = (long) next(bits) << 32L;
            if (msb < n) {
                long val = msb | next(32) & 0xffffffffL;
                if (val < n) {
                    return val;
                }
            }

        }
    }

    public long nextLong(long least, long bound) {
        if (least >= bound) {
            throw new IllegalArgumentException("least = " + least + ", bound = " + bound);
        }
        return nextLong(bound - least) + least;
    }

    public String nextString(int size) {
        return randomEnglishAlphabet(size).toUpperCase();
    }

    /**
     * Returns a random element from the specified collection
     * 
     * @param list
     *            the list to
     * @return
     * @throws AssertionError
     *             if the specified collection is empty
     */
    @SuppressWarnings("unchecked")
    public <T> T nextElement(Collection<T> list) {
        if (list instanceof List) {
            int size = list.size();
            if (size == 0) {
                throw new AssertionError("The specified collection is empty. Cannot choose a random element from an empty collection");
            }
            return ((List<T>) list).get(nextInt(size));
        } else {
            Object[] o = list.toArray();
            if (o.length == 0) {
                throw new AssertionError("The specified collection is empty. Cannot choose a random element from an empty collection");
            }
            return (T) o[nextInt(o.length)];
        }
    }

    private String randomEnglishAlphabet(int size) {
        return randomString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", size);
    }

    /**
     * Returns a random string containing only lower and upper case English better. The length is between 3 and 8.
     * 
     * @return a random string.
     */
    public String randomString() {
        return randomString(3, 8);
    }

    public String randomString(int minSize, int maxSize) {
        return randomEnglishAlphabet(minSize + nextInt(maxSize - minSize));
    }

    String randomString(String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(nextInt(characters.length()));
        }
        return new String(text);
    }

    public void setSeed(long seed) {
        this.seed = (seed ^ MULTIPLIER) & SEED_MASK - 1;
    }

    public static RandomSource current() {
        RandomSource r = TL.get();
        if (r == null) {
            r = new RandomSource(1234);
            TL.set(r);
        }
        return r;
    }

}
