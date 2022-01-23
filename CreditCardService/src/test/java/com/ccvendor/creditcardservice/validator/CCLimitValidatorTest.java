package com.ccvendor.creditcardservice.validator;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CCLimitValidatorTest {

    private CCLimitValidator validator;

    @Test
    public void testLimitOfCardInBounds() {
        this.validator = new CCLimitValidator(10.00, 20.00);

        assertFalse(this.validator.isValid(9.00, null));
        assertFalse(this.validator.isValid(0.00, null));
        assertFalse(this.validator.isValid(Double.MAX_VALUE, null));
        assertFalse(this.validator.isValid(Double.MIN_VALUE, null));
        assertFalse(this.validator.isValid(9.99, null));

        assertFalse(this.validator.isValid(20.01, null));
        assertFalse(this.validator.isValid(100.00, null));

        assertTrue(this.validator.isValid(10.01, null));
        assertTrue(this.validator.isValid(19.99, null));
        assertTrue(this.validator.isValid(16.23, null));
        assertTrue(this.validator.isValid(11.58, null));
    }
}
