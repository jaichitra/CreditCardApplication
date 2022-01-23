package com.ccvendor.creditcardservice.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CCLimitValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CardLimit {

    String message() default "Card Limit validation failed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

