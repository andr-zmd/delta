package com.github.andrz25.delta.user_service.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private String defaultRegion;
    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        this.defaultRegion = constraintAnnotation.defaultRegion();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        try {
            PhoneNumber number = phoneUtil.parse(value, defaultRegion);

            return phoneUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            return false;
        }
    }
}
