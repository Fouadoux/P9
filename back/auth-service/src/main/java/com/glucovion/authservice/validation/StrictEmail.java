package com.glucovion.authservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrictEmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrictEmail {
    String message() default "Email format is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
