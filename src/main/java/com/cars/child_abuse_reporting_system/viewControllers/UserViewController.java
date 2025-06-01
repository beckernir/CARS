package com.cars.child_abuse_reporting_system.viewControllers;

import com.cars.child_abuse_reporting_system.entities.User;
import com.cars.child_abuse_reporting_system.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserViewController {

    private final UserService userService;

    /**
     * Display the list of cows.
     */
    @GetMapping("/allUsers")
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsersNoPage();
        model.addAttribute("users", users);
        return "/admin/users-list"; // Thymeleaf template "list.html"
    }
    /**
     * Delete a cow by ID.
     */
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "redirect:/api/v1/users/allUsers";
    }

}
