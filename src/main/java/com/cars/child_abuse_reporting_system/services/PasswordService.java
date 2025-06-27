package com.cars.child_abuse_reporting_system.services;

import com.cars.child_abuse_reporting_system.dtos.PasswordChangeRequest;
import com.cars.child_abuse_reporting_system.entities.User;
import com.cars.child_abuse_reporting_system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PasswordService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private EmailService emailService;


    @Autowired
    public PasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Change password for logged-in user
    public void changePassword(String userEmail, PasswordChangeRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Validate new password
        validatePassword(request.getNewPassword());

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }
        // Add more validation rules as needed
    }
}