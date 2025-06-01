package com.cars.child_abuse_reporting_system.controllers;

import com.cars.child_abuse_reporting_system.dtos.UserDto;
import com.cars.child_abuse_reporting_system.entities.User;
import com.cars.child_abuse_reporting_system.services.UserService;
import com.cars.child_abuse_reporting_system.exceptions.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Public user registration (no registration code needed)
     */
    @PostMapping("/create")
    public ResponseEntity<User> registerPublicUser(@RequestBody UserDto request) {
        // Public users register without registration code
        request.setRegistrationCode(null);
        User createdUser = userService.createUser(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Privileged user registration using a registration code
     * Used internally (e.g., by staff/admins)
     */
    @PostMapping("/register/privileged")
    public ResponseEntity<User> registerPrivilegedUser(@RequestBody UserDto request) {
        if (request.getRegistrationCode() == null || request.getRegistrationCode().isEmpty()) {
            throw new InvalidFormatException("Registration code is required for privileged user registration");
        }

        User createdUser = userService.createUser(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}
