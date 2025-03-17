package ding.co.backendportfolio.chapter5.util;

import java.util.concurrent.ThreadLocalRandom;

public class FilterUtil {

    /**
     * (1) 비용: 10, 선택도: 0.5
     * => Filter-Overhead: 5
     */
    public static boolean filterOverhead5() {
        simulateCost(10);
        return simulateSelectivity(0.5);
    }

    /**
     * (2) 비용: 10, 선택도: 0.7
     * => Filter-Overhead: 7
     */
    public static boolean filterOverhead7() {
        simulateCost(10);
        return simulateSelectivity(0.7);
    }

    /**
     * (3) 비용: 100, 선택도: 0.5
     * => Filter-Overhead: 50
     */
    public static boolean filterOverhead50() {
        simulateCost(100);
        return simulateSelectivity(0.5);
    }

    /**
     * (4) 비용: 100, 선택도: 0.7
     * => Filter-Overhead: 70
     */
    public static boolean filterOverhead70() {
        simulateCost(100);
        return simulateSelectivity(0.7);
    }


    /**
     * busy-wait 방식으로 'nanos'만큼 CPU 시간을 소모
     */
    private static void simulateCost(long nanos) {
        long start = System.nanoTime();
        while (System.nanoTime() - start < nanos) {
            // busy-wait
        }
    }

    private static boolean simulateSelectivity(double value) {
        return ThreadLocalRandom.current().nextDouble() < value;
    }
}
