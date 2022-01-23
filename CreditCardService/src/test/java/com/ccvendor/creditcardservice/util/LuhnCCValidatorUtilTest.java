package com.ccvendor.creditcardservice.util;


import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LuhnCCValidatorUtilTest {
    // Testing with precalculated valid and invalid Luhn Card numbers.
    @Test
    public void testLuhnCardNumberCheckCardNumberValid() {
        assertTrue(LuhnCCValidatorUtil.luhn10Check("63819201928371239"));
        assertTrue(LuhnCCValidatorUtil.luhn10Check("4617019281263712"));
        assertTrue(LuhnCCValidatorUtil.luhn10Check("6948038296645101035"));
        assertTrue(LuhnCCValidatorUtil.luhn10Check("0525316293847857"));
    }

    @Test
    public void testLuhnCardNumberCheckCardNumberInValid() {
        assertFalse(LuhnCCValidatorUtil.luhn10Check("381920192837123"));
        assertFalse(LuhnCCValidatorUtil.luhn10Check(""));
        assertFalse(LuhnCCValidatorUtil.luhn10Check("0"));
        assertFalse(LuhnCCValidatorUtil.luhn10Check("00000000000000000"));
        assertFalse(LuhnCCValidatorUtil.luhn10Check("0219839532942342667"));
        assertFalse(LuhnCCValidatorUtil.luhn10Check("461021939123821034"));
    }

}
