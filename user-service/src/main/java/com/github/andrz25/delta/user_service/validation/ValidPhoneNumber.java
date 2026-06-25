package com.github.andrz25.delta.user_service.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
public @interface ValidPhoneNumber {

    String message()

    default "Invalid phone number format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String defaultRegion() default "CA";
}
