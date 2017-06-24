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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.Spliterator;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/**
 *
 * @author Kasper Nielsen
 */
public class SpliteratorTestUtil {

    @SuppressWarnings("unchecked")
    public static <E> void testSpliterator(Random random, Spliterator<E> spliterator, List<E> list) {
        // int characteristics = spliterator.characteristics();
        if (random.nextDouble() < 0.5) {
            // perform split
            Spliterator<E> newSplit = SpliteratorTestUtil.splitAndVerify(spliterator);
            if (newSplit != null) {
                testSpliterator(random, newSplit, list);
                testSpliterator(random, spliterator, list);
                return;
            }
        }

        // Split so check it
        if (random.nextBoolean()) {
            if (!(spliterator instanceof Spliterator.OfPrimitive) || random.nextBoolean()) {
                spliterator.forEachRemaining(e -> list.add(e));
            } else if (spliterator instanceof Spliterator.OfLong) {
                ((Spliterator.OfLong) spliterator).forEachRemaining((LongConsumer) e -> ((List<Long>) list).add(e));
            } else if (spliterator instanceof Spliterator.OfDouble) {
                ((Spliterator.OfDouble) spliterator).forEachRemaining((DoubleConsumer) e -> ((List<Double>) list).add(e));
            } else {
                ((Spliterator.OfInt) spliterator).forEachRemaining((IntConsumer) e -> ((List<Integer>) list).add(e));
            }
        } else {
            boolean remaining = true;
            while (remaining) {
                if (!(spliterator instanceof Spliterator.OfPrimitive) || random.nextBoolean()) {
                    remaining = spliterator.tryAdvance(e -> list.add(e));
                } else if (spliterator instanceof Spliterator.OfLong) {
                    remaining = ((Spliterator.OfLong) spliterator).tryAdvance((LongConsumer) e -> ((List<Long>) list).add(e));
                } else if (spliterator instanceof Spliterator.OfDouble) {
                    remaining = ((Spliterator.OfDouble) spliterator).tryAdvance((DoubleConsumer) e -> ((List<Double>) list).add(e));
                } else {
                    remaining = ((Spliterator.OfInt) spliterator).tryAdvance((IntConsumer) e -> ((List<Integer>) list).add(e));
                }
            }
        }
        SpliteratorTestUtil.testEmptySpliterator(spliterator);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Spliterator<?>> T splitAndVerify(T spliterator) {
        long exactSize = spliterator.getExactSizeIfKnown();
        T newSplit = (T) spliterator.trySplit();
        if (newSplit != null) {
            if (exactSize >= 0 && exactSize != Long.MAX_VALUE) {
                // System.out.println("A " + exactSize);
                // System.out.println("B " + spliterator.getExactSizeIfKnown());
                // System.out.println("C " + newSplit.getExactSizeIfKnown());
                assertEquals(exactSize, spliterator.getExactSizeIfKnown() + newSplit.getExactSizeIfKnown());
            }
        } else {
            assertEquals(exactSize, spliterator.getExactSizeIfKnown());
        }
        return newSplit;
    }

    public static void testCharacteristics(Spliterator<?> s) {
        if (s.hasCharacteristics(Spliterator.SUBSIZED)) {
            assertTrue(s.hasCharacteristics(Spliterator.SIZED));
        }
        if (s.hasCharacteristics(Spliterator.SIZED)) {
            assertTrue(s.estimateSize() >= 0 && s.estimateSize() != Long.MAX_VALUE);
            assertTrue(s.getExactSizeIfKnown() >= 0);
        }
        try {
            s.getComparator();
            assertTrue(s.hasCharacteristics(Spliterator.SORTED));
        } catch (IllegalStateException e) {
            assertFalse(s.hasCharacteristics(Spliterator.SORTED));
        }
    }

    public static void testEmptySpliterator(Spliterator<?> s) {
        assertFalse(s.tryAdvance(e -> {
            throw new AssertionError();
        }));
        s.forEachRemaining(e -> {
            throw new AssertionError();
        });
        if (s instanceof Spliterator.OfLong) {
            Spliterator.OfLong pSpliterator = (Spliterator.OfLong) s;
            assertFalse(pSpliterator.tryAdvance((LongConsumer) e -> {
                throw new AssertionError();
            }));
            pSpliterator.forEachRemaining((LongConsumer) e -> {
                throw new AssertionError();
            });
        } else if (s instanceof Spliterator.OfDouble) {
            Spliterator.OfDouble pSpliterator = (Spliterator.OfDouble) s;
            assertFalse(pSpliterator.tryAdvance((DoubleConsumer) e -> {
                throw new AssertionError();
            }));
            pSpliterator.forEachRemaining((DoubleConsumer) e -> {
                throw new AssertionError();
            });
        } else if (s instanceof Spliterator.OfInt) {
            Spliterator.OfInt pSpliterator = (Spliterator.OfInt) s;
            assertFalse(pSpliterator.tryAdvance((IntConsumer) e -> {
                throw new AssertionError();
            }));
            pSpliterator.forEachRemaining((IntConsumer) e -> {
                throw new AssertionError();
            });
        }

        long size = s.getExactSizeIfKnown();
        assertTrue("Expected size 0 or -1, but was " + size, size == 0 || size == -1);
        s = s.trySplit();
        if (s != null) {
            testEmptySpliterator(s);
        }
    }
    //
    // long size = spliterator.getExactSizeIfKnown();
    // if (size == -1) {
    // assertFalse(spliterator.hasCharacteristics(Spliterator.SIZED));
    // } else {
    // assertEquals(expected().size(), size);
    // assertEquals(expected().size(), spliterator.estimateSize());
    // assertTrue(spliterator.hasCharacteristics(Spliterator.SIZED));
    // }
    //
    // // test charistics
    // spliterator.forEachRemaining(null);
    // spliterator.tryAdvance(null);
    // spliterator.trySplit();

}
