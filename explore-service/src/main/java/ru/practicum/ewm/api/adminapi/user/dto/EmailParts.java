package ru.practicum.ewm.api.adminapi.user.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = EmailPartsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
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
