package com.bps.publikasistatistik.service;

import com.bps.publikasistatistik.dto.ChangePasswordRequest;
import com.bps.publikasistatistik.dto.UpdateProfileRequest;
import com.bps.publikasistatistik.dto.UserResponse;
import com.bps.publikasistatistik.entity.User;
import com.bps.publikasistatistik.repository.UserRepository;
import com.bps.publikasistatistik.security.CustomUserDetails;
import com.bps.publikasistatistik.util.PhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    public UserResponse getProfile(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UserResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(CustomUserDetails userDetails, UpdateProfileRequest request) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update username if provided and different
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username already taken");
            }
            user.setUsername(request.getUsername());
        }

        // Update email if provided and different
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already registered");
            }
            user.setEmail(request.getEmail());
        }

        // Update fullName if provided
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        // Update gender if provided
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }

        // Update placeOfBirth if provided
        if (request.getPlaceOfBirth() != null) {
            user.setPlaceOfBirth(request.getPlaceOfBirth());
        }

        // Update dateOfBirth if provided
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        // Update phoneNumber if provided (with validation and normalization)
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            // Validate and normalize phone number
            String normalizedPhone = PhoneNumberValidator.validateAndNormalize(request.getPhoneNumber());
            user.setPhoneNumber(normalizedPhone);
        }

        // Update address if provided
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        User updatedUser = userRepository.save(user);
        log.info("User profile updated: {}", updatedUser.getEmail());
        
        // Send security alert notification
        notificationService.notifyProfileUpdated(updatedUser);

        return new UserResponse(updatedUser);
    }

    @Transactional
    public void changePassword(CustomUserDetails userDetails, ChangePasswordRequest request) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("User password changed: {}", user.getEmail());
        
        // Send security alert notification
        notificationService.notifyPasswordChanged(user);
    }

    @Transactional
    public void deleteAccount(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Delete profile picture if exists
        if (user.getProfilePicture() != null) {
            fileStorageService.deleteProfilePicture(user.getProfilePicture());
        }

        userRepository.delete(user);
        log.info("User account deleted: {}", user.getEmail());
    }

    @Transactional
    public UserResponse uploadProfilePicture(CustomUserDetails userDetails, MultipartFile file) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        // Validate file type (images only)
        String contentType = file.getContentType();
        if (!isValidImageType(contentType)) {
            throw new RuntimeException("Invalid file type. Only JPG, JPEG, PNG are allowed");
        }

        // Delete old profile picture if exists
        if (user.getProfilePicture() != null) {
            fileStorageService.deleteProfilePicture(user.getProfilePicture());
        }

        // Store new file
        String fileName = fileStorageService.storeProfilePicture(file);
        user.setProfilePicture(fileName);

        User updatedUser = userRepository.save(user);
        log.info("Profile picture uploaded for user: {}", user.getEmail());

        return new UserResponse(updatedUser);
    }

    public Resource getProfilePicture(CustomUserDetails userDetails) throws IOException {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getProfilePicture() == null) {
            throw new RuntimeException("Profile picture not found");
        }

        // Load file as Resource
        Path filePath = fileStorageService.loadProfilePicture(user.getProfilePicture());
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Profile picture file not found: " + user.getProfilePicture());
        }
    }

    @Transactional
    public UserResponse deleteProfilePicture(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getProfilePicture() == null) {
            throw new RuntimeException("No profile picture to delete");
        }

        // Delete file from storage
        fileStorageService.deleteProfilePicture(user.getProfilePicture());

        // Remove reference from database
        user.setProfilePicture(null);
        User updatedUser = userRepository.save(user);

        log.info("Profile picture deleted for user: {}", user.getEmail());

        return new UserResponse(updatedUser);
    }

    private boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png")
        );
    }
}