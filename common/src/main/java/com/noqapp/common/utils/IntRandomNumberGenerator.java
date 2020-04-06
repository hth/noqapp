package com.noqapp.common.utils;

import java.util.PrimitiveIterator;
import java.util.Random;

/**
 * hitender
 * 2019-03-26 23:25
 */
public class IntRandomNumberGenerator {
    private PrimitiveIterator.OfInt randomIterator;

    /**
     * Initialize a new random number generator that generates
     * random numbers in the range [min, max]
     * @param min - the min value (inclusive)
     * @param max - the max value (inclusive)
     */
    private IntRandomNumberGenerator(int min, int max) {
        randomIterator = new Random().ints(min, max).iterator();
    }

    public static IntRandomNumberGenerator newInstanceExclusiveOfMaxRange(int min, int max) {
        return new IntRandomNumberGenerator(min, max);
    }

    public static IntRandomNumberGenerator newInstanceInclusiveOfMaxRange(int min, int max) {
        return new IntRandomNumberGenerator(min, max + 1);
    }

    /**
     * Returns a random number in the range (min, max)
     * @return a random number in the range (min, max)
     */
    public int nextInt() {
        return randomIterator.nextInt();
    }
}
