package ding.co.backendportfolio.chapter5._3_data_processing;

import java.util.concurrent.ThreadLocalRandom;

public class FilterUtil {

    public static void simulateCost(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {

        }
    }

    public static boolean simulateSelectivity(double value) {
        return ThreadLocalRandom.current().nextDouble() < value;
    }
}
