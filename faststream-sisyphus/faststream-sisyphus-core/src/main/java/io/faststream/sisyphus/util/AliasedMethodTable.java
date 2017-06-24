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

import static java.util.Objects.requireNonNull;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 * An aliased method table implementation. Used for assigning different weights to test methods.
 * 
 * Based on http://www.keithschwarz.com/darts-dice-coins/
 * 
 * @param <E>
 *            the type of elements
 * @author Kasper Nielsen
 */
public final class AliasedMethodTable<E> implements Function<Random, E> {

    /** The alias table. */
    private final int[] alias;

    /** The elements to return. */
    private final E[] elements;

    /** The probability table. */
    private final double[] probabilities;

    /** An array of weights. */
    private final double[] weights;

    /**
     * Constructs a new AliasedMethodTable to sample from a discrete distribution and hand back outcomes based on the
     * probability distribution.
     * <p>
     * Given as input a list of probabilities corresponding to outcomes 0, 1, ..., n - 1, this constructor creates the
     * probability and alias tables needed to efficiently sample from this distribution.
     * 
     * @param elementsToWeight
     *            a map of elements and probabilities
     */
    @SuppressWarnings("unchecked")
    public AliasedMethodTable(Map<E, Double> elementsToWeight) {
        if (requireNonNull(elementsToWeight, "elementsToWeight is null").size() == 0) {
            throw new IllegalArgumentException("elementsToWeight is empty");
        }
        elements = (E[]) elementsToWeight.keySet().toArray();// nulls allowed
        probabilities = new double[elements.length];
        alias = new int[elements.length];
        weights = new double[elements.length];
        double averageProbability = 1.0d / elements.length;

        // Calculates the weighted sum.
        Double[] p = elementsToWeight.values().toArray(new Double[elements.length]);
        double weightedSum = 0d;
        for (int i = 0; i < p.length; i++) {
            weightedSum += this.weights[i] = p[i];// also checks for null
        }

        // Some temporary work lists.
        ArrayDeque<Integer> smallest = new ArrayDeque<>();
        ArrayDeque<Integer> largest = new ArrayDeque<>();

        // Normalize weights and if probability is below the average probability add to small, otherwise add to big
        for (int i = 0; i < p.length; i++) {
            this.weights[i] = p[i];
            ((p[i] /= weightedSum) >= averageProbability ? largest : smallest).add(i);

        }
        // Do to floating point inaccuracies we might exhaust largest list first so check both
        while (!smallest.isEmpty() && !largest.isEmpty()) {
            int iSmall = smallest.removeLast();
            int iLarge = alias[iSmall] = largest.removeLast();
            probabilities[iSmall] = p[iSmall] * p.length;
            // Decrease the probability of the larger one by the appropriate amount.
            // If the new probability is less than the average, add it into the small list; otherwise add it to the
            // large list.
            ((p[iLarge] += p[iSmall] - averageProbability) >= 1.0d / p.length ? largest : smallest).add(iLarge);
        }
        smallest.addAll(largest); // Remaining probabilities are all 1/n.
        while (!smallest.isEmpty()) {
            probabilities[smallest.removeLast()] = 1.0d;
        }
    }

    /**
     * Returns the next element using the specified random source.
     * 
     * @param random
     *            the source.
     * @return the next element
     */
    public E apply(Random random) {
        return elements[nextIndex(random)];
    }

    /**
     * Samples a value from the underlying distribution using the specified ("perfect") random source.
     * 
     * @param param
     *            the random source
     * @return A random value sampled from the underlying distribution.
     */
    private int nextIndex(Random random) {
        int column = random.nextInt(probabilities.length);
        return random.nextDouble() < probabilities[column] ? column : alias[column];
    }

    public static AliasedMethodTable<Integer> create(double... weights) {
        LinkedHashMap<Integer, Double> lhm = new LinkedHashMap<>();
        for (int i = 0; i < weights.length; i++) {
            lhm.put(i, weights[i]);
        }
        return new AliasedMethodTable<>(lhm);
    }

    public void print(Function<E, String> f, PrintWriter pw) {
        for (int i = 0; i < elements.length; i++) {
            System.out.println(f.apply(elements[i]) + ": " + alias[i]);
        }
        throw new Error();
    }

    public static void maidn(String[] args) {
        int samples = 10000000;
        Random r = new Random();
        AliasedMethodTable<Integer> am = create(1, 3, 1, 8, 1, 1, 1);
        int[] hits = new int[am.alias.length];
        for (int i = 0; i < samples; i++) {
            hits[am.apply(r)]++;
        }
        DecimalFormat df = new DecimalFormat("##.00");
        for (int i = 0; i < hits.length; i++) {
            System.out.println(i + " " + df.format(hits[i] / (double) samples * 100) + " %");
        }
        for (int i = 0; i < am.probabilities.length; i++) {
            System.out.println(i + " " + am.probabilities[i]);
        }
    }
}
