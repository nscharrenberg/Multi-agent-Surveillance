package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomUtil {
    public final static String DEFAULT_SEED = "surveillance";
    private final static String algorithm = "SHA1PRNG";

    public static SecureRandom seeded() throws NoSuchAlgorithmException {
        return SecureRandom.getInstance(algorithm);
    }

    /**
     * Generates a SecureRandom instance with a seed
     * @param seedValue - the seed
     * @return SecureRandom with seed
     * @throws NoSuchAlgorithmException
     */
    public static SecureRandom seeded(String seedValue) throws NoSuchAlgorithmException {
        byte[] seed = seedValue.getBytes();

        return seeded(seed);
    }

    /**
     * Generates a SecureRandom instance with a seed
     * @param seed - the seed
     * @return SecureRandom instance with seed
     * @throws NoSuchAlgorithmException
     */
    public static SecureRandom seeded(byte[] seed) throws NoSuchAlgorithmException {
        SecureRandom instance = SecureRandom.getInstance(algorithm);

        instance.setSeed(seed);

        return instance;
    }
}
