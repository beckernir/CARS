package com.cars.child_abuse_reporting_system.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service to handle geolocation functionality for the Child Abuse Reporting System
 */
@Service
public class GeoLocationService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Retrieves address information from coordinates using OpenStreetMap Nominatim API
     *
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @return A string representation of the address
     */
    public String getAddressFromCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return "Unknown location";
        }

        try {
            String url = String.format(
                    "https://nominatim.openstreetmap.org/reverse?format=json&lat=%s&lon=%s",
                    latitude, longitude
            );

            Map<String, Object> response = restTemplate.getForObject(url, HashMap.class);

            if (response != null && response.containsKey("display_name")) {
                return (String) response.get("display_name");
            } else {
                return "Location found, but address details unavailable";
            }
        } catch (Exception e) {
            // Log the error
            System.err.println("Error fetching address from coordinates: " + e.getMessage());
            return "Error retrieving location information";
        }
    }

    /**
     * Shares location information with authorities in case of emergency
     *
     * @param caseId The unique case identifier
     * @param locationDescription Textual description of the location
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @return true if the location was successfully shared, false otherwise
     */
    public boolean shareLocationWithAuthorities(String caseId, String locationDescription,
                                                Double latitude, Double longitude) {

        try {
            // Example implementation - in production replace with actual emergency service API call
            CompletableFuture.runAsync(() -> {
                System.out.println("EMERGENCY ALERT: Case " + caseId);
                System.out.println("Location: " + locationDescription);
                System.out.println("Coordinates: " + latitude + ", " + longitude);

            });

            return true;
        } catch (Exception e) {
            // Log the error
            System.err.println("Failed to share location with authorities: " + e.getMessage());
            return false;
        }
    }
}