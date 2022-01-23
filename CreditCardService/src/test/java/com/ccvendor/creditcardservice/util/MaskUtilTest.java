package com.ccvendor.creditcardservice.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MaskUtilTest {

    @Test
    public void testMaskFunctionality() {
        assertEquals("x3x2x1x0x3xxxx", MaskUtil.maskCreditCardNumber("23123120435943"));
        assertEquals("x1x", MaskUtil.maskCreditCardNumber("010"));
        assertEquals("x4x3x3x2x5x3x9x5x0xxxx", MaskUtil.maskCreditCardNumber("3423439285030945809348"));
        assertEquals("x0x0x1x3x8x1x0x0xxxx", MaskUtil.maskCreditCardNumber("00000123983120000000"));
    }
}
