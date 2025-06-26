package com.cars.child_abuse_reporting_system.utils;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CaseIdGeneratorService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_STRING_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    public String generateCaseId() {
        // Format: PREFIX-YYYYMMDD-RANDOM
        String dateComponent = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomComponent = generateRandomString(RANDOM_STRING_LENGTH);

        return String.format("CR-%s-%s", dateComponent, randomComponent);
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
}