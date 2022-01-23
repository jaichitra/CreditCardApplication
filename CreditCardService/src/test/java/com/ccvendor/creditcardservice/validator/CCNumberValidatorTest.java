package com.ccvendor.creditcardservice.validator;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class CCNumberValidatorTest {

    private final CCNumberValidator validator = new CCNumberValidator();

    @Test
    public void testCardNumberBlankOrEmpty() {
        assertFalse(this.validator.isValid("", null));
        assertFalse(this.validator.isValid(null, null));
    }


    @Test
    public void testCardNumberLimitTest() {
        assertFalse(this.validator.isValid("13282191", null));
        assertFalse(this.validator.isValid("319128218218281", null));
        assertFalse(this.validator.isValid("12929192921114312312", null));
    }


    @Test
    public void testValidCardNumber() {
        assertTrue(this.validator.isValid("63819201928371239", null));
        assertTrue(this.validator.isValid("4617019281263712", null));
        assertTrue(this.validator.isValid("0525316293847857", null));
    }
}
