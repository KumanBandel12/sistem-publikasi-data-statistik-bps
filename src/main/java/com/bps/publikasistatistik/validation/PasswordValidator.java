package com.bps.publikasistatistik.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    // Pattern untuk validasi password:
    // - Minimal 8 karakter
    // - Minimal 1 huruf besar
    // - Minimal 1 huruf kecil
    // - Minimal 1 angka
    // - Hanya boleh mengandung huruf, angka, dan simbol umum
    // - Simbol opsional
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&.,;:#_\\-+=(){}\\[\\]<>|/\\\\~`'\"\\^]{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        // Validasi panjang minimal
        if (password.length() < 8) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must be at least 8 characters")
                   .addConstraintViolation();
            return false;
        }

        // Validasi harus ada huruf besar
        if (!password.matches(".*[A-Z].*")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must contain at least one uppercase letter")
                   .addConstraintViolation();
            return false;
        }

        // Validasi harus ada huruf kecil
        if (!password.matches(".*[a-z].*")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must contain at least one lowercase letter")
                   .addConstraintViolation();
            return false;
        }

        // Validasi harus ada angka
        if (!password.matches(".*\\d.*")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must contain at least one digit")
                   .addConstraintViolation();
            return false;
        }

        // Validasi hanya boleh mengandung karakter yang diizinkan (huruf, angka, simbol umum)
        if (!pattern.matcher(password).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password can only contain letters, numbers, and common symbols (@$!%*?&.,;:#_-+=(){}[]<>|/\\~`'\"^)")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}
