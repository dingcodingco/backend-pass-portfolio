package ding.co.backendportfolio.chapter5._3_data_processing;

import java.util.concurrent.ThreadLocalRandom;

public enum PostType {
    TEXT,
    IMAGE,
    VIDEO;

    private static final PostType[] VALUES = values();

    public static PostType getByRandom() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int randomInt = random.nextInt(VALUES.length);
        return VALUES[randomInt];
    }
}
