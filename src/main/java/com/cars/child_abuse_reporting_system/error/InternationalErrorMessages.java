package com.cars.child_abuse_reporting_system.error;

import java.util.Map;

public class InternationalErrorMessages {

    // These keys can be used with Spring's MessageSource for i18n
    public static final String ACCESS_DENIED_KEY = "error.access.denied";
    public static final String LOGIN_REQUIRED_KEY = "error.login.required";
    public static final String INSUFFICIENT_PRIVILEGES_KEY = "error.insufficient.privileges";
    public static final String SESSION_EXPIRED_KEY = "error.session.expired";
    public static final String CONTACT_ADMIN_KEY = "error.contact.admin";
    public static final String RETURN_DASHBOARD_KEY = "error.return.dashboard";

    // Default English messages (fallback)
    public static final Map<String, String> DEFAULT_MESSAGES = Map.of(
            ACCESS_DENIED_KEY, "You do not have permission to access this resource.",
            LOGIN_REQUIRED_KEY, "Please sign in to continue.",
            INSUFFICIENT_PRIVILEGES_KEY, "Your account does not have the required privileges.",
            SESSION_EXPIRED_KEY, "Your session has expired. Please sign in again.",
            CONTACT_ADMIN_KEY, "Contact your administrator if you need access to this resource.",
            RETURN_DASHBOARD_KEY, "Return to your dashboard or the main page."
    );
}
