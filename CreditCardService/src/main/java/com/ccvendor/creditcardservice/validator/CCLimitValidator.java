package com.ccvendor.creditcardservice.validator;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CCLimitValidator implements ConstraintValidator<CardLimit, Double> {

    private final Double minimumCardLimit;

    private final Double maximumCardLimit;

    public CCLimitValidator(@Value("#{new Double('${creditcard.minimum.limit}')}") final Double minimumCardLimit,
                            @Value("#{new Double('${creditcard.maximum.limit}')}") final Double maximumCardLimit) {
        super();
        this.maximumCardLimit = maximumCardLimit;
        this.minimumCardLimit = minimumCardLimit;
    }

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