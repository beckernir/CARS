package com.cars.child_abuse_reporting_system.error;

public class ErrorPageTemplates {

    public static final String UNAUTHORIZED_HTML = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Access Denied</title>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; text-align: center; margin: 50px; }
                .error-container { max-width: 600px; margin: 0 auto; }
                .error-code { font-size: 72px; color: #dc3545; margin: 20px 0; }
                .error-message { font-size: 24px; color: #333; margin: 20px 0; }
                .error-details { color: #666; margin: 20px 0; }
                .btn { background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
                .btn:hover { background-color: #0056b3; }
            </style>
        </head>
        <body>
            <div class="error-container">
                <div class="error-code">403</div>
                <div class="error-message">Access Denied</div>
                <div class="error-details">
                    You do not have permission to access this resource.<br>
                    Please contact your administrator if you believe this is an error.
                </div>
                <a href="/dashboard" class="btn">Return to Dashboard</a>
                <a href="/login" class="btn" style="margin-left: 10px;">Sign In</a>
            </div>
        </body>
        </html>
        """;

    public static final String LOGIN_REQUIRED_HTML = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Login Required</title>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; text-align: center; margin: 50px; }
                .error-container { max-width: 600px; margin: 0 auto; }
                .error-code { font-size: 72px; color: #ffc107; margin: 20px 0; }
                .error-message { font-size: 24px; color: #333; margin: 20px 0; }
                .error-details { color: #666; margin: 20px 0; }
                .btn { background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
                .btn:hover { background-color: #0056b3; }
            </style>
        </head>
        <body>
            <div class="error-container">
                <div class="error-code">401</div>
                <div class="error-message">Authentication Required</div>
                <div class="error-details">
                    Please sign in to access this resource.<br>
                    Your session may have expired.
                </div>
                <a href="/login" class="btn">Sign In</a>
                <a href="/" class="btn" style="margin-left: 10px;">Go Home</a>
            </div>
        </body>
        </html>
        """;
}
