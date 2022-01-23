package com.ccvendor.creditcardservice.util;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MaskUtil {

    public static String maskCreditCardNumber(final String cardNumber) {
        final String maskedString = IntStream.range(0, cardNumber.length()).mapToObj(t -> t % 2 == 0 ? "x" : Character.toString(cardNumber.charAt(t))).collect(Collectors.joining(""));
        return maskedString.length() > 4 ? maskedString.substring(0, maskedString.length() - 4) + "xxxx" : maskedString;

    }

}

