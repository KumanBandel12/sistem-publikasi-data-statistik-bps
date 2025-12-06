package com.bps.publikasistatistik.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Email must be valid")
    private String email;

    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @Size(max = 20, message = "Gender must not exceed 20 characters")
    private String gender;

    @Size(max = 100, message = "Place of birth must not exceed 100 characters")
    private String placeOfBirth;

    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(\\+62|62|0)[0-9]{9,12}$", 
             message = "Invalid phone number format. Use Indonesian phone number (e.g., 08123456789)")
    private String phoneNumber;

    private String address;
}