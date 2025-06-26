package com.cars.child_abuse_reporting_system.dtos;


import com.cars.child_abuse_reporting_system.enums.Gender;
import com.cars.child_abuse_reporting_system.enums.Role;
import com.cars.child_abuse_reporting_system.enums.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserDto {

    private String Id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;


    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters long and include uppercase, lowercase, and numbers")
    private String password;


    @NotBlank(message = "Invitation code is required")
    private String registrationCode; // Used to determine role

    private Status status;

}
