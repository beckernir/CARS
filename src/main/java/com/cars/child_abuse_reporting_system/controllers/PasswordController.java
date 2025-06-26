package com.cars.child_abuse_reporting_system.controllers;

import com.cars.child_abuse_reporting_system.dtos.PasswordChangeRequest;
import com.cars.child_abuse_reporting_system.services.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Password Controller
@RestController
@RequestMapping("/api/password")
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    // Change password for logged-in user
    @PostMapping("/change")
    public ResponseEntity<?> changePassword(
            @RequestBody PasswordChangeRequest request,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            passwordService.changePassword(userEmail, request);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

//    // Forgot password - send reset email
//    @PostMapping("/forgot")
//    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
//        try {
//            passwordService.forgotPassword(request);
//            return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", e.getMessage()));
//        }
//    }
//
//    // Reset password using token
//    @PostMapping("/reset")
//    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
//        try {
//            passwordService.resetPassword(request);
//            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", e.getMessage()));
//        }
//    }
}