package com.octo.niobenchmark.httpcore.util;

import java.util.concurrent.atomic.AtomicLong;

public class ConsumeCPU {

    private static volatile long consumedCPU = System.nanoTime();

    // Computed in init phase, use for time/nbtoken calibration
    private static long nbTokensFor1second = calibrateCPUTimeOnMultithread();

    public static void consumeCpuInMillisecond(long nbTimeInMilliseconds) {
        long startTime = System.nanoTime();
        consumeCPU(nbTimeInMilliseconds * nbTokensFor1second / 1000);
        long stopTime = System.nanoTime();

        long duration = (stopTime - startTime) / 1000000;
        // TODO - Use a logger
        //System.out.println("Duration in milliseconds: " + duration);
        //System.out.println(Math.abs(duration - nbTimeInMilliseconds) < 0.02f * nbTimeInMilliseconds);
    }

    /**
     * Consume some amount of time tokens.
     * @param tokens CPU tokens to consume
     */
    private static void consumeCPU(long tokens) {

        long t = 0;

        for (long i = tokens; i > 0; i--) {
            t += (t * 0x5DEECE66DL + 0xBL + i) & (0xFFFFFFFFFFFFL);

        }

        if (t == 42) {
            consumedCPU += t;
        }
    }

    /**
     * Returns the time in milliseconds elapsed between nbtokens iteration of consumeCPU
     * @param nbtokens
     * @return
     */
    private static Float calibrateFor(long nbtokens) {
        assert (nbtokens > 1000);
        Long start = System.nanoTime();
        consumeCPU(nbtokens);
        Long stop = System.nanoTime();
        return (stop - start) / 1000000f;
    }

    public static long calibrateCPUTimeOnMultithread() {
        final AtomicLong l = new AtomicLong();
        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    l.getAndAdd(ConsumeCPU.calibrateCPUTime(1000));
                }
            }).run();
        }

        int nbTokensFor1Second = Math.round(l.get() / (3));

        //System.out.println("The number of token for 1 second is : " + nbTokensFor1Second);
        return nbTokensFor1Second;
    }

    /**
     * Return the number of token to use for the duration nbMilliseconds
     *
     * @param nbMilliseconds
     * @return
     */
    private static long calibrateCPUTime(long nbMilliseconds) {

        long nbtokens = 1000;
        float elapsedTime = 0f;
        for (int i = 1; i < 500; i++) {
            elapsedTime = calibrateFor(nbtokens);

            if (Math.abs(elapsedTime - nbMilliseconds) < (0.01 * nbMilliseconds)) {
//                System.out.println("Calibrating after " + i + " tries");
                return nbtokens;
            } else {
                nbtokens = Math.round(nbMilliseconds / (elapsedTime / nbtokens));
            }
        }

        return -1;

    }


    public static void main(String[] args) {
        consumeCpuInMillisecond(2000);
        consumeCpuInMillisecond(1000);
        consumeCpuInMillisecond(5000);
        consumeCpuInMillisecond(100);
    }
}