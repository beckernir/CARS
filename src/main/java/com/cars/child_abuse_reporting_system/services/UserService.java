package com.cars.child_abuse_reporting_system.services;

import com.cars.child_abuse_reporting_system.dtos.UserDto;
import com.cars.child_abuse_reporting_system.entities.User;
import com.cars.child_abuse_reporting_system.enums.Role;
import com.cars.child_abuse_reporting_system.enums.Status;
import com.cars.child_abuse_reporting_system.exceptions.InvalidFormatException;
import com.cars.child_abuse_reporting_system.exceptions.ResourceNotFoundException;
import com.cars.child_abuse_reporting_system.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsersNoPage() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Creates a new user of any role after validating inputs.
     *
     * @param request Data Transfer Object containing user details.
     * @return The created User entity.
     */
    @Transactional
    public User createUser(UserDto request) {
        String rawPassword = request.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        isInputValidate(request);

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidFormatException("Email address already in use");
        }


        Role assignedRole = Role.PUBLIC; // default for public users

        if (request.getRegistrationCode() != null && !request.getRegistrationCode().isEmpty()) {
            assignedRole = determineRole(request.getRegistrationCode())
                    .orElseThrow(() -> new InvalidFormatException("Invalid registration code"));
        }

        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(encodedPassword)
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .role(assignedRole)
                .accountStatus(Status.ACTIVE) // User will need admin approval
                .isVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(newUser);

        return savedUser;
    }

    private void isInputValidate(UserDto userDto) {
        if (!isValidFirstName(userDto.getFirstName())) {
            throw new InvalidFormatException("Invalid first name format.");
        }

        if (!isValidLastName(userDto.getLastName())) {
            throw new InvalidFormatException("Invalid last name format.");
        }

        if (!isValidPhoneNumber(userDto.getPhoneNumber())) {
            throw new InvalidFormatException("Invalid phone number format.");
        }

        if (!isValidEmail(userDto.getEmail())) {
            throw new InvalidFormatException("Invalid email format.");
        }
    }

    /**
     * Updates an existing user by its ID.
     *
     * @param id      ID of the user to update.
     * @param userDto Data Transfer Object containing updated user details.
     * @return The updated User entity.
     */
    @Transactional
    public User updateUser(String id, UserDto userDto) {
        User existingUser = getUserById(id);

        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPhoneNumber(userDto.getPhoneNumber());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(existingUser);
    }

    /**
     * Retrieves a user by its ID.
     *
     * @param id ID of the user to retrieve.
     * @return The User entity.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Deletes a user by its ID.
     *
     * @param id ID of the user to delete.
     * @throws ResourceNotFoundException if the user does not exist.
     */
    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    public long getTotalUsers() {
        return userRepository.count();
    }

    public List<User> getRecentUsers() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return userRepository.findUsersRegisteredAfter(sevenDaysAgo);
    }
    public Map<String, Long> getUserCountsByRole() {
        List<Object[]> results = userRepository.countUsersByRole();
        Map<String, Long> roleCounts = new HashMap<>();
        for (Object[] row : results) {
            roleCounts.put(row[0].toString(), (Long) row[1]);
        }
        return roleCounts;
    }

    /**
     * Counts all users of a specific role in the database.
     *
     * @param role Role to count users.
     * @return The total number of users with the specified role.
     */
    public long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }

    private boolean isValidFirstName(String firstName) {
        return firstName != null && firstName.matches("^[A-Za-z]+$");
    }

    private boolean isValidLastName(String lastName) {
        return lastName != null && lastName.matches("^[A-Za-z]+$");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        System.out.println("Validating phone number: '" + phoneNumber + "' (length: " +
                (phoneNumber != null ? phoneNumber.length() : "null") + ")");
        String regex = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";
        boolean isValid = phoneNumber != null && phoneNumber.matches(regex);
        System.out.println("Phone number validation result: " + isValid);
        return isValid;
    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(regex);
    }

    private static final Map<String, Role> REGISTRATION_CODES = Map.of(
            hash("HEALTHCARE123"), Role.HEALTHCARE,
            hash("ADMIN123"), Role.ADMIN,
            hash("SOCIALWORKER123"), Role.SOCIAL_WORKER
    );

    public Optional<Role> determineRole(String registrationCode) {
        return Optional.ofNullable(REGISTRATION_CODES.get(hash(registrationCode)));
    }

    private static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing input", e);
        }
    }
}
