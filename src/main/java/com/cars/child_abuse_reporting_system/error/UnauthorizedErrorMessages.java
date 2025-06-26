package com.cars.child_abuse_reporting_system.error;

import java.util.Map;

public class UnauthorizedErrorMessages {

    // Messages for different user roles trying to access restricted areas
    public static final Map<String, String> ROLE_BASED_MESSAGES = Map.of(
            "PUBLIC_TO_ADMIN", "Administrative functions are restricted to system administrators only.",
            "PUBLIC_TO_AUTHORITY", "This section is reserved for authorized personnel including social workers, healthcare professionals, and law enforcement.",
            "AUTHORITY_TO_ADMIN", "System administration functions require administrator privileges.",
            "EXPIRED_SESSION", "Your session has expired for security reasons. Please log in again.",
            "INVALID_ROLE", "Your account role does not permit access to this resource."
    );

    // User-friendly messages that work globally
    public static final Map<String, String> FRIENDLY_MESSAGES = Map.of(
            "GENERIC_DENIED", "We're sorry, but you don't have permission to view this page.",
            "LOGIN_REQUIRED", "Please sign in to continue.",
            "INSUFFICIENT_PRIVILEGES", "This area requires special authorization that your account doesn't currently have.",
            "CONTACT_ADMIN", "If you believe this is an error, please contact your system administrator.",
            "RETURN_HOME", "You can return to your dashboard or the main page."
    );

    // Messages for specific system areas
    public static final Map<String, String> AREA_SPECIFIC_MESSAGES = Map.of(
            "REPORTS_RESTRICTED", "Report management is limited to authorized personnel only.",
            "CASES_RESTRICTED", "Case files are accessible only to assigned social workers and supervisors.",
            "ADMIN_PANEL", "System administration panel requires administrator credentials.",
            "INVESTIGATIONS", "Investigation records are restricted to law enforcement and authorized case workers.",
            "MEDICAL_RECORDS", "Medical information access is limited to healthcare professionals and authorized personnel.",
            "USER_MANAGEMENT", "User account management is reserved for system administrators.",
            "AUDIT_LOGS", "System audit logs are accessible only to administrators for security purposes."
    );
}
