package com.bps.publikasistatistik.dto;

import com.bps.publikasistatistik.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String fullName;
    private String gender;
    private String placeOfBirth;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String address;
    private String profilePicture;
    private LocalDateTime createdAt;

    // Constructor untuk convert dari Entity ke DTO
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.fullName = user.getFullName();
        this.gender = user.getGender();
        this.placeOfBirth = user.getPlaceOfBirth();
        this.dateOfBirth = user.getDateOfBirth();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.profilePicture = user.getProfilePicture();
        this.createdAt = user.getCreatedAt();
    }
}