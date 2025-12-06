package com.bps.publikasistatistik.util;

import java.util.regex.Pattern;

public class PhoneNumberValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+62|62|0)[0-9]{9,12}$");
    private static final Pattern VALID_OPERATOR_PATTERN = Pattern.compile("[12357]");

    /**
     * Validate Indonesian phone number format
     * Accepts: 08xxx, 628xxx, +628xxx
     * Length: 10-13 digits after cleaning
     */
    public static boolean isValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        // Remove all non-digit characters except +
        String cleanPhone = phoneNumber.replaceAll("[^0-9+]", "");

        // Check basic pattern
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            return false;
        }

        // Validate operator prefix (digit after 08 or 628)
        String operatorDigit = extractOperatorDigit(cleanPhone);
        if (operatorDigit == null) {
            return false;
        }

        // Valid operators in Indonesia: 1, 2, 3, 5, 7, 8, 9
        // Most common: 1 (Telkomsel), 2 (Telkomsel), 3 (XL/Indosat), 5 (Indosat), 7 (XL), 8 (Smartfren), 9 (Tri)
        return operatorDigit.matches("[1235789]");
    }

    /**
     * Normalize phone number to standard format: 628XXXXXXXXX
     * Converts all variations to international format without +
     */
    public static String normalize(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }

        // Remove all non-digit characters
        String cleanPhone = phoneNumber.replaceAll("[^0-9]", "");

        // Convert to 628XXXXXXXXX format
        if (cleanPhone.startsWith("08")) {
            // 08123456789 -> 628123456789
            return "62" + cleanPhone.substring(1);
        } else if (cleanPhone.startsWith("628")) {
            // 628123456789 -> 628123456789 (already normalized)
            return cleanPhone;
        } else if (cleanPhone.startsWith("8") && cleanPhone.length() >= 10) {
            // 8123456789 -> 628123456789
            return "62" + cleanPhone;
        }

        // If format is unknown, return as-is
        return cleanPhone;
    }

    /**
     * Validate and normalize phone number
     * Returns normalized phone number if valid, throws exception if invalid
     */
    public static String validateAndNormalize(String phoneNumber) {
        if (!isValid(phoneNumber)) {
            throw new IllegalArgumentException(
                "Invalid phone number format. Use Indonesian phone number (e.g., 08123456789, +628123456789)"
            );
        }
        return normalize(phoneNumber);
    }

    /**
     * Extract operator digit from phone number
     * Returns the digit after 08 or 628
     */
    private static String extractOperatorDigit(String cleanPhone) {
        if (cleanPhone.startsWith("08") && cleanPhone.length() >= 3) {
            return cleanPhone.substring(2, 3);
        } else if (cleanPhone.startsWith("628") && cleanPhone.length() >= 4) {
            return cleanPhone.substring(3, 4);
        } else if (cleanPhone.startsWith("+628") && cleanPhone.length() >= 5) {
            return cleanPhone.substring(4, 5);
        }
        return null;
    }

    /**
     * Format phone number for display (with dashes)
     * Example: 628123456789 -> 0812-3456-789
     */
    public static String formatForDisplay(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return phoneNumber;
        }

        // Convert to 08xxx format first
        String displayFormat = phoneNumber;
        if (phoneNumber.startsWith("62")) {
            displayFormat = "0" + phoneNumber.substring(2);
        }

        // Add dashes: 08123456789 -> 0812-3456-789
        if (displayFormat.length() >= 11) {
            return displayFormat.substring(0, 4) + "-" + 
                   displayFormat.substring(4, 8) + "-" + 
                   displayFormat.substring(8);
        }

        return displayFormat;
    }
}
