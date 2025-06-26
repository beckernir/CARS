package com.cars.child_abuse_reporting_system.viewControllers;


import com.cars.child_abuse_reporting_system.dtos.*;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.entities.User;
import com.cars.child_abuse_reporting_system.enums.CaseStatus;
import com.cars.child_abuse_reporting_system.services.CaseReportService;
import com.cars.child_abuse_reporting_system.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeViewController {

    @Autowired
    private UserService userService; // Inject your user service

    @Autowired
    private CaseReportService caseReportService;

    private void addUserDataToModel(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            // Fetch user from database
            User user = userService.findByUsername(username);

            if (user != null) {
                String fullName = user.getFirstName() + " " + user.getLastName();
                String role = formatRole(user.getRole().name()); // Assuming you have a Role entity

                model.addAttribute("currentUserFullName", fullName);
                model.addAttribute("currentUserRole", role);
            }
        }
    }

    private String formatRole(String role) {
        if (role.startsWith("ROLE_")) {
            role = role.substring(5); // Remove "ROLE_" prefix
        }
        return role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase().replace("_", " ");
    }


    @GetMapping
    public String home() {
        return "/home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login-page";
    }

    @GetMapping("/signup")
    public String signUpPage() {
        return "signup-page";
    }
//
//    @GetMapping("/admin-dashboard")
//    public String adminDashboard(Model model, Authentication authentication) {
//        addUserDataToModel(model, authentication);
//        return "/admin/admin-dashboard";
//    }
    /**
     * Main dashboard page
     */
    @GetMapping("/admin-dashboard")
    public String getDashboard(Model model) {
        try {
            // Get all analytics data
            DashboardAnalyticsDTO analytics = caseReportService.getDashboardAnalytics();
            CaseStatisticsDTO statistics = caseReportService.getCaseStatistics();
            List<AgeGroupAnalysisDTO> ageAnalysis = caseReportService.getAgeGroupAnalysis();
            List<RegionAnalysisDTO> regionAnalysis = caseReportService.getRegionAnalysis();
            List<AbuseTypeAnalysisDTO> abuseTypeAnalysis = caseReportService.getAbuseTypeAnalysis();
            List<MonthlyTrendDTO> monthlyTrends = caseReportService.getMonthlyTrends();
            List<CaseReport> recentCases = caseReportService.getActiveCases();

            // Limit recent cases to last 10 for display
            if (recentCases.size() > 10) {
                recentCases = recentCases.subList(0, 10);
            }

            // Add all data to model
            model.addAttribute("analytics", analytics);
            model.addAttribute("statistics", statistics);
            model.addAttribute("ageAnalysis", ageAnalysis);
            model.addAttribute("regionAnalysis", regionAnalysis);
            model.addAttribute("abuseTypeAnalysis", abuseTypeAnalysis);
            model.addAttribute("monthlyTrends", monthlyTrends);
            model.addAttribute("recentCases", recentCases);
            model.addAttribute("caseStatuses", CaseStatus.values());


            return "/admin/admin-dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load dashboard data: " + e.getMessage());
            return "error";
        }
    }


    @GetMapping("/public-dashboard")
    public String publicWorkerDashboard() {
        return "/public/my-cases";
    }

    @GetMapping("/authority-dashboard")
    public String authorityDashboard() {
        return "/authority/my-cases";
    }

    // Updated default/generic dashboard with all roles
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        // Get user roles from Authentication object
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            // Redirect based on role
            if (role.equals("ROLE_ADMIN")) {
                return "redirect:/admin-dashboard";

            } else if (role.equals("ROLE_POLICE_OFFICER")) {
                return "redirect:/api/v1/authority/allCases";

            } else if (role.equals("ROLE_HEALTHCARE")) {
                return "redirect:/api/v1/authority/allCases";

            } else if (role.equals("ROLE_SOCIAL_WORKER")) {
                return "redirect:/api/v1/authority/allCases";

            } else if (role.equals("ROLE_PUBLIC")) {
                return "redirect:/api/v1/public/allCases";

            }
        }
        // Default fallback if no matching role found
        return "/home";
    }
}