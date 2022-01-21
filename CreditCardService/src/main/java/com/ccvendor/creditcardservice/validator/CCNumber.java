package com.ccvendor.creditcardservice.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CCNumberValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CCNumber {

    String message() default "{Invalid card number passed, Card number validation failed}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload()default{};

}
