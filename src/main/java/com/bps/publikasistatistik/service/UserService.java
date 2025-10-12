package com.bps.publikasistatistik.service;

import com.bps.publikasistatistik.dto.ChangePasswordRequest;
import com.bps.publikasistatistik.dto.UpdateProfileRequest;
import com.bps.publikasistatistik.dto.UserResponse;
import com.bps.publikasistatistik.entity.User;
import com.bps.publikasistatistik.repository.UserRepository;
import com.bps.publikasistatistik.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

        User updatedUser = userRepository.save(user);
        log.info("User profile updated: {}", updatedUser.getEmail());

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
    }

    @Transactional
    public void deleteAccount(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userRepository.delete(user);
        log.info("User account deleted: {}", user.getEmail());
    }
}