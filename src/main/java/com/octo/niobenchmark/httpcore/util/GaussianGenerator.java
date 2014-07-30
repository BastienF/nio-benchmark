package com.octo.niobenchmark.httpcore.util;

import com.octo.niobenchmark.Parameters;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Bastien on 30/07/2014.
 */
public class GaussianGenerator {
    public static double[] GAUSSIANS_VALUES = new double[Parameters.GAUSSIANS_SIZE];

    public static void init() {
        Random rand = new Random();
        for (int i = 0; i < Parameters.GAUSSIANS_SIZE; i++) {
            GAUSSIANS_VALUES[i] = Math.abs((rand.nextGaussian() * Parameters.STD_DEVIATION + Parameters.MEAN));
        }
    }

    public static double getValue() {
        return GAUSSIANS_VALUES[ThreadLocalRandom.current().nextInt(0, Parameters.GAUSSIANS_SIZE)];
    }
}
