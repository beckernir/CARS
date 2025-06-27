package com.cars.child_abuse_reporting_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Keep CSRF disabled as in your original config
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/login",
                                "/signup",
                                "/",
                                "/api/v1/users/**",
                                "/api/v1/cases/**",
                                "/api/v1/case-reports/**",
                                "/api/v1/reports/export/**"
                        ).permitAll()

                        // Role-based dashboard access
                        .requestMatchers("/api/v1/admin/view/**").hasAnyRole("ADMIN","POLICE_OFFICER", "SOCIAL_WORKER","HEALTHCARE")
                        .requestMatchers("/admin-dashboard","/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/public-dashboard,/api/v1/public/**").hasRole("PUBLIC")
                        .requestMatchers("/authority-dashboard","/api/v1/authority/**").hasAnyRole("POLICE_OFFICER", "SOCIAL_WORKER","HEALTHCARE")

                        // Static resources
                        .requestMatchers("/assets/**", "/fonts/**", "/img/**", "/static/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/process-login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecretKey")
                        .tokenValiditySeconds(86400) // 1 day
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}