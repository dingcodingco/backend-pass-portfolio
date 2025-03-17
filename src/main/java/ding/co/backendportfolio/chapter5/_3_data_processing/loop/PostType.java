package ding.co.backendportfolio.chapter5._3_data_processing.loop;

public enum PostType {
    TEXT,
    IMAGE,
    VIDEO;

    private static final PostType[] VALUES = values();

    public static PostType getByInt(int value) {
        return values()[value % 3];
    }
}
