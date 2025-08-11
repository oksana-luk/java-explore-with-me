package ru.practicum.ewm.api.adminapi.user.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailPartsValidator implements ConstraintValidator<EmailParts, String> {
    private int minLocal;
    private int maxLocal;
    private int minDomain;
    private int maxDomain;
    private int minEmail;
    private int maxEmail;

    @Override
    public void initialize(EmailParts constraintAnnotation) {
        this.minLocal = constraintAnnotation.minLocal();
        this.maxLocal = constraintAnnotation.maxLocal();
        this.minDomain = constraintAnnotation.minDomain();
        this.maxDomain = constraintAnnotation.maxDomain();
        this.minEmail = constraintAnnotation.minEmail();
        this.maxEmail = constraintAnnotation.maxEmail();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || !value.contains("@") || value.split("@").length != 2) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Email have to contain '@', domain and local parts")
                    .addConstraintViolation();
            return false;
        }

        if (value.length() < minEmail) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format("Email must be at least %d characters long", minEmail))
                    .addConstraintViolation();
            return false;
        }

        if (value.length() > maxEmail) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format("Email must be at most %d characters long", maxEmail))
                    .addConstraintViolation();
            return false;
        }

        String[] values = value.split("@");
        String local = values[0];
        String domain = values[1];

        if (local.length() < minLocal || local.length() > maxLocal) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("Local part of email have to contain from %d to %d characters",
                            minLocal, maxLocal)
            ).addConstraintViolation();
            return false;
        }

        String[] domainParts = domain.split("\\.");
        if (domainParts.length == 1) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Domain part of email have to contain dot ('.') character"
            ).addConstraintViolation();
            return false;
        }

        for (String part : domainParts) {
            if (part.length() < minDomain || part.length() > maxDomain) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        String.format("Domain part of email %s have to contain from %d to %d characters",
                                part, minDomain, maxDomain)
                ).addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
