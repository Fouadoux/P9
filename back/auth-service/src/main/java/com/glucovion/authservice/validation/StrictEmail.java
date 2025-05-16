package com.glucovion.authservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom annotation for validating strict email formats.
 * <p>
 * This constraint uses {@link StrictEmailValidator} to enforce custom email rules
 * beyond standard {@link jakarta.validation.constraints.Email} validation.
 * </p>
 *
 * <p>Can be applied to fields only.</p>
 *
 * Example usage:
 * <pre>
 * &#64;StrictEmail
 * private String email;
 * </pre>
 */
@Documented
@Constraint(validatedBy = StrictEmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrictEmail {

    /**
     * Error message to return when validation fails.
     */
    String message() default "Email format is invalid";

    /**
     * Allows grouping of validation constraints.
     */
    Class<?>[] groups() default {};

    /**
     * Used to carry metadata information.
     */
    Class<? extends Payload>[] payload() default {};
}
