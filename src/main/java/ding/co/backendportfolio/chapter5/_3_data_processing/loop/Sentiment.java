package ding.co.backendportfolio.chapter5._3_data_processing.loop;

import java.util.concurrent.ThreadLocalRandom;

public enum Sentiment {
    POSITIVE,
    NEGATIVE,
    NEUTRAL,
    ANGRY,
    HAPPY,
    SAD;

    // 캐싱
    private static final Sentiment[] ALL_SENTIMENTS = values();

    /**
     * - 랜덤한 Sentiment 반환
     */
    public static Sentiment getByRandom() {
        int randomInt = ThreadLocalRandom.current().nextInt();
        int normalizedIndex = Math.abs(randomInt % ALL_SENTIMENTS.length);
        return ALL_SENTIMENTS[normalizedIndex];
    }
}
