package com.cars.child_abuse_reporting_system.viewControllers;

import com.cars.child_abuse_reporting_system.dtos.PasswordChangeRequest;
import com.cars.child_abuse_reporting_system.dtos.UserDto;
import com.cars.child_abuse_reporting_system.entities.User;
import com.cars.child_abuse_reporting_system.services.PasswordService;
import com.cars.child_abuse_reporting_system.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserViewController {

    private final UserService userService;
    private final PasswordService passwordService;

    /**
     * Display user profile page
     * GET /profile
     */
    @GetMapping
    public String viewProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("profileUpdateRequest", new UserDto());
        model.addAttribute("passwordChangeRequest", new PasswordChangeRequest()); // ✅ Required!
        model.addAttribute("pageTitle", "My Profile");

        return "/public/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute PasswordChangeRequest request,
                                 Authentication authentication,
                                 Model model) {
        try {
            String userEmail = authentication.getName();
            passwordService.changePassword(userEmail, request);
            model.addAttribute("successMessage", "Password changed successfully");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "public/profile";
    }

    /**
     * Handle profile update form submission
     * POST /profile/update
     */
    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute UserDto profileRequest,
                                BindingResult bindingResult,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (bindingResult.hasErrors()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            model.addAttribute("user", user);
            model.addAttribute("profileUpdateRequest", profileRequest);
            return "/public/profile";
        }

        try {
            String username = authentication.getName();
            User updatedUser = userService.updateUser(username, profileRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/api/v1/users";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            model.addAttribute("user", user);
            model.addAttribute("profileUpdateRequest", profileRequest);
            return "/public/profile";
        }
    }

    /**
     * Display profile edit form
     * GET /profile/edit
     */
    @GetMapping("/edit")
    public String editProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user == null) {
            return "redirect:/login";
        }

        User profileRequest = new User();
        profileRequest.setFirstName(user.getFirstName());
        profileRequest.setLastName(user.getLastName());
        profileRequest.setEmail(user.getEmail());
        profileRequest.setPhoneNumber(user.getPhoneNumber());
        model.addAttribute("user", user);
        model.addAttribute("profileUpdateRequest", profileRequest);
        model.addAttribute("pageTitle", "Edit Profile");
        model.addAttribute("editMode", true);

        return "/public/profile";
    }

}
