package com.ccvendor.creditcardservice.validator;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CCLimitValidator implements ConstraintValidator<CardLimit, Double> {

    @Value("#{new Double('${creditcard.minimum.limit}')}")
    private Double minimumCardLimit;

    @Value("#{new Double('${creditcard.maximum.limit}')}")
    private Double maximumCardLimit;

    @Override
    public void initialize(final CardLimit ccNumber) {
    }

    @Override
    public boolean isValid(final Double cardLimit, final ConstraintValidatorContext ctx) {
        if (cardLimit == null)
            return false;
        return cardLimit > this.minimumCardLimit && cardLimit < this.maximumCardLimit;
    }

}