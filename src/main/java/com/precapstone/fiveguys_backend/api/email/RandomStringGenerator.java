package com.precapstone.fiveguys_backend.api.email;

import java.security.SecureRandom;

public class RandomStringGenerator {

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*";
    private static final String ALPHANUMERIC_SPECIAL = LETTERS + NUMBERS + SPECIAL_CHARACTERS;

    public static String generateRandomPassword() {
        int length = 12;
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        // 첫 번째 자리는 영문자에서 선택
        sb.append(LETTERS.charAt(random.nextInt(LETTERS.length())));

        // 숫자와 특수문자를 각각 한 자리씩 추가
        sb.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        sb.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // 나머지 자리수를 무작위로 채움
        for (int i = 3; i < length; i++) {
            sb.append(ALPHANUMERIC_SPECIAL.charAt(random.nextInt(ALPHANUMERIC_SPECIAL.length())));
        }

        // StringBuilder 내용을 무작위로 섞음
        return shuffleString(sb.toString(), random);
    }

    // 문자열을 무작위로 섞기 위한 함수
    private static String shuffleString(String input, SecureRandom random) {
        char[] array = input.toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
        return new String(array);
    }
}
