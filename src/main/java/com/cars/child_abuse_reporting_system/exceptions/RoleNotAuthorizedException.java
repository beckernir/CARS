package com.cars.child_abuse_reporting_system.exceptions;

public class RoleNotAuthorizedException extends RuntimeException {
    private final String requiredRole;
    private final String userRole;

    public RoleNotAuthorizedException(String requiredRole, String userRole) {
        super(String.format("Access denied. Required role: %s, User role: %s", requiredRole, userRole));
        this.requiredRole = requiredRole;
        this.userRole = userRole;
    }

    public String getRequiredRole() { return requiredRole; }
    public String getUserRole() { return userRole; }
}