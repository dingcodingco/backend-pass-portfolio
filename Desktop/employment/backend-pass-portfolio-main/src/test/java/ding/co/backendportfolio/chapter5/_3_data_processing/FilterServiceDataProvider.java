package ding.co.backendportfolio.chapter5._3_data_processing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FilterServiceDataProvider {

    public static final int LIST_SIZE = 100;
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    public static final int ALPHABET_LENGTH = ALPHABET.length();

    public static List<Post> createTestData() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= LIST_SIZE; i++) {
            list.add(i);
        }

        return list.stream()
                .map(intValue ->
                        Post.builder()
                                .id(Long.valueOf(intValue))
                                .content(generateRandomString())
                                .build()
                )
                .toList();
    }

    /**
     * - 알파벳 소문자로 구성된 길이가 10인 랜덤한 문자열 생성
     */
    private static String generateRandomString() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        StringBuilder stringBuilder = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(ALPHABET_LENGTH);
            stringBuilder.append(ALPHABET.charAt(index));
        }

        return stringBuilder.toString();
    }
}
