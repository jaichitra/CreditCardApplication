package com.ccvendor.creditcardservice.validator;

import com.ccvendor.creditcardservice.util.LuhnCCValidatorUtil;
import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CCNumberValidator implements ConstraintValidator<CCNumber, String> {

    @Override
    public void initialize(final CCNumber ccNumber) {
    }

    @Override
    public boolean isValid(final String ccNo, final ConstraintValidatorContext ctx) {
        if (StringUtils.isEmpty(ccNo)) return false;
        if (ccNo.length() < 16 || ccNo.length() > 19) return false;
        if (!LuhnCCValidatorUtil.luhn10Check(ccNo)) return false;
        return true;
    }

}
