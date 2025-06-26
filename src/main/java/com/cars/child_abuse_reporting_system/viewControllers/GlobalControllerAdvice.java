package com.cars.child_abuse_reporting_system.viewControllers;

import com.cars.child_abuse_reporting_system.entities.User;
import com.cars.child_abuse_reporting_system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addUserAttributes(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {

            String username = authentication.getName();
            User user = userRepository.findByEmail(username).orElse(null);
            String fullName = user.getFirstName() + " " + user.getLastName();


            if (user != null) {
                model.addAttribute("currentUserFullName", fullName);
            } else {
                model.addAttribute("currentUserFullName", username);
            }

            // Get role
            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .map(auth -> auth.startsWith("ROLE_") ? auth.substring(5) : auth)
                    .orElse("User");

            model.addAttribute("currentUserRole", role);
        }
    }
}