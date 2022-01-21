package com.ccvendor.creditcardservice.util;

import java.util.stream.IntStream;

public class LuhnCardNumberValidator {

    public static boolean luhn10Check(final String ccNumber) {
        long luhnSum = luhn10Sum(ccNumber);
        return luhnSum > 0 && luhnSum % 10 == 0;
    }

    private static long luhn10Sum(final String ccNumber) {
        return IntStream.range(1, ccNumber.length() + 1).mapToLong(t -> {
            if (t % 2 == 0) {
                int twoTimeOf = Character.getNumericValue(ccNumber.charAt(ccNumber.length() - t)) * 2;
                return twoTimeOf > 9 ? twoTimeOf - 9 : twoTimeOf;
            } else {
                return Character.getNumericValue(ccNumber.charAt(ccNumber.length() - t));
            }
        }).sum();
    }
}
