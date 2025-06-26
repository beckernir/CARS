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

//    // Forgot password - send reset token
//    public void forgotPassword(ForgotPasswordRequest request) {
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));
//
//        // Generate reset token
//        String resetToken = generateResetToken();
//        user.setResetToken(resetToken);
//        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24)); // 24 hours expiry
//
//        userRepository.save(user);
//
//        // Send email with reset link
//        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
//        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
//    }
//
//    // Reset password using token
//    public void resetPassword(ResetPasswordRequest request) {
//        User user = userRepository.findByResetToken(request.getToken())
//                .orElseThrow(() -> new RuntimeException("Invalid reset token"));
//
//        // Check if token is expired
//        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
//            throw new RuntimeException("Reset token has expired");
//        }
//
//        // Validate new password
//        validatePassword(request.getNewPassword());
//
//        // Update password and clear reset token
//        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//        user.setResetToken(null);
//        user.setResetTokenExpiry(null);
//
//        userRepository.save(user);
//    }

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