package com.bps.publikasistatistik.service;

import com.bps.publikasistatistik.dto.AuthResponse;
import com.bps.publikasistatistik.dto.ForgotPasswordRequest;
import com.bps.publikasistatistik.dto.LoginRequest;
import com.bps.publikasistatistik.dto.RegisterRequest;
import com.bps.publikasistatistik.dto.UserResponse;
import com.bps.publikasistatistik.entity.User;
import com.bps.publikasistatistik.repository.UserRepository;
import com.bps.publikasistatistik.security.CustomUserDetails;
import com.bps.publikasistatistik.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER); // Default role is USER

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());
        
        // Notify all admins about new user registration
        notificationService.notifyAdminsNewUser(savedUser);

        return new UserResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);

        // Get user from database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User logged in: {}", user.getEmail());

        return new AuthResponse(jwt, new UserResponse(user));
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // Validate password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Password and confirmation password do not match");
        }

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

        // Validate security questions
        // Check if user has completed profile (dateOfBirth and placeOfBirth must be filled)
        if (user.getDateOfBirth() == null || user.getPlaceOfBirth() == null || 
            user.getPlaceOfBirth().trim().isEmpty()) {
            throw new RuntimeException("Profile data incomplete. Please contact admin to reset password.");
        }

        // Validate date of birth
        if (!user.getDateOfBirth().equals(request.getDateOfBirth())) {
            throw new RuntimeException("Date of birth is incorrect");
        }

        // Validate place of birth (case-insensitive, trim whitespace)
        if (!user.getPlaceOfBirth().trim().equalsIgnoreCase(request.getPlaceOfBirth().trim())) {
            throw new RuntimeException("Place of birth is incorrect");
        }

        // All validations passed - update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password reset successfully for user: {}", user.getEmail());

        // Send security alert notification
        notificationService.notifyPasswordChanged(user);
    }
}