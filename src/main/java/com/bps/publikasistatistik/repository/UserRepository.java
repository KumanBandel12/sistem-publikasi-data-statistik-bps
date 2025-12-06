package com.bps.publikasistatistik.repository;

import com.bps.publikasistatistik.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (untuk login)
    Optional<User> findByEmail(String email);

    // Find user by username
    Optional<User> findByUsername(String username);

    // Check if email exists (untuk validasi registrasi)
    Boolean existsByEmail(String email);

    // Check if username exists (untuk validasi registrasi)
    Boolean existsByUsername(String username);
    
    // Find users by role (for notification targeting)
    java.util.List<User> findByRole(User.Role role);
}