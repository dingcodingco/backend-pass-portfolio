package ding.co.backendportfolio.chapter6._2_remote_redis_practice;

import ding.co.backendportfolio.chapter6._1_practice.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BookDataProvider {
    /**
     * 주어진 횟수만큼 랜덤한 Book 데이터를 생성하는 유틸 메서드.
     * 이름은 1~12자 랜덤 문자열, 품절 여부는 랜덤 boolean.
     */
    public static List<Book> createRandomBooks(int count) {
        List<Book> books = new ArrayList<>(count);
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            String randomName = generateRandomString(random.nextInt(12) + 1);
            boolean randomIsSoldOut = random.nextBoolean();
            books.add(Book.builder()
                    .name(randomName)
                    .isSoldOut(randomIsSoldOut)
                    .build());
        }
        return books;
    }

    /**
     * 주어진 길이만큼의 랜덤 문자열을 생성하는 유틸 메서드.
     * 문자열에는 영문 대/소문자 및 숫자가 포함됨.
     */
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
