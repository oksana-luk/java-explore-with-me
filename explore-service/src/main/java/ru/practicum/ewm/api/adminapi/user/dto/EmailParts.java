package ru.practicum.ewm.api.adminapi.user.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmailPartsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailParts {

    int minLocal() default 1;
    int maxLocal() default 64;

    int minDomain() default 1;
    int maxDomain() default 63;

    int minEmail() default 6;
    int maxEmail() default 254;

    String message() default "Invalid email format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
