package ding.co.backendportfolio.chapter5.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomStringUtil extends AbstractUtilClass {

    private static final char[] CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public static final int MIN_STRING_SIZE = 0;
    public static final int MAX_STRING_SIZE = 10_000;

    public static final int MIN_LIST_SIZE = 0;
    public static final int MAX_LIST_SIZE = 10_000;

    /**
     * 고정된 길이의 랜덤 문자열 생성
     *
     * @param stringSize 생성할 문자열의 길이 (양수여야 함)
     * @return 랜덤 문자열
     * @throws IllegalArgumentException size가 1 미만, 10_000 초과인 경우 예외 발생
     */
    public static String generateRandomString(int stringSize) {
        validateStringSize(stringSize);

        StringBuilder stringBuilder = new StringBuilder(stringSize);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < stringSize; i++) {
            int index = random.nextInt(CHARACTERS.length);
            char character = CHARACTERS[index];

            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }

    private static void validateStringSize(int size) {
        if (size <= MIN_STRING_SIZE) {
            throw new IllegalArgumentException("String size must be greater than 0");
        }
        if (size > MAX_STRING_SIZE) {
            throw new IllegalArgumentException("String size is too large. Must be less than or equal to 10,000.");
        }
    }

    /**
     * 고정된 길이의 랜덤 문자열을 가진 List 생성
     *
     * @param stringSize 각 문자열의 길이 (양수여야 함)
     * @param listSize   리스트의 크기 (양수여야 함)
     * @return 랜덤 문자열 리스트
     * @throws IllegalArgumentException stringSize나 listSize가 1 미만, 10_000 초과인 경우 예외 발생
     */
    public static List<String> generateRandomStringList(int stringSize, int listSize) {
        validateListSize(listSize);

        List<String> randomStringList = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) {
            String randomString = generateRandomString(stringSize);
            randomStringList.add(randomString);
        }

        return randomStringList;
    }

    private static void validateListSize(int listSize) {
        if (listSize <= MIN_LIST_SIZE) {
            throw new IllegalArgumentException("List Size must be greater than 0");
        }
        if (listSize > MAX_LIST_SIZE) {
            throw new IllegalArgumentException("List Size too large. Must be less than or equal to 10,000.");
        }
    }
}