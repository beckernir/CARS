package com.cars.child_abuse_reporting_system.services;//package com.cars.child_abuse_reporting_system.services;

import com.cars.child_abuse_reporting_system.dtos.Language;
import com.cars.child_abuse_reporting_system.dtos.LibreTranslateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;

@Service
public class TranslationService {

    private final RestTemplate restTemplate;

    @Value("${libretranslate.url:http://localhost:5000}")  // Changed to standard port
    private String libreTranslateUrl;

    @Value("${libretranslate.api-key:}")
    private String apiKey;

    public TranslationService() {
        this.restTemplate = new RestTemplate();

        // Configure RestTemplate timeout properly
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10000);          // Connection timeout
        factory.setConnectionRequestTimeout(10000); // Request timeout
//        factory.setReadTimeout(30000);             // Read timeout - Added this
        this.restTemplate.setRequestFactory(factory);
    }

    public Map<String, String> translateBatch(Map<String, String> texts, String source, String target) {
        Map<String, String> translations = new HashMap<>();

        for (Map.Entry<String, String> entry : texts.entrySet()) {
            try {
                String translatedText = translateText(entry.getValue(), source, target);
                translations.put(entry.getKey(), translatedText);
            } catch (Exception e) {
                // If translation fails, keep original text
                translations.put(entry.getKey(), entry.getValue());
                System.err.println("Translation failed for key: " + entry.getKey() + ", error: " + e.getMessage());
            }
        }

        return translations;
    }

    public String translateText(String text, String source, String target) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        if (source.equals(target)) {
            return text;
        }

        try {
            // Set proper headers - Added Accept header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); // This is crucial!

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("q", text);
            requestBody.put("source", source);
            requestBody.put("target", target);
            requestBody.put("format", "text");

            if (!apiKey.isEmpty()) {
                requestBody.put("api_key", apiKey);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Log the request for debugging
            System.out.println("Calling LibreTranslate at: " + libreTranslateUrl + "/translate");
            System.out.println("Request body: " + requestBody);

            ResponseEntity<LibreTranslateResponse> response = restTemplate.postForEntity(
                    libreTranslateUrl + "/translate",
                    entity,
                    LibreTranslateResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getTranslatedText();
            } else {
                throw new RuntimeException("Translation service returned error status: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            // Log the actual error response
            System.err.println("HTTP Error: " + e.getStatusCode());
            System.err.println("Response Body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Translation failed: HTTP " + e.getStatusCode() + " - " + e.getResponseBodyAsString());

        } catch (ResourceAccessException e) {
            System.err.println("Connection Error - Cannot reach LibreTranslate at: " + libreTranslateUrl);
            System.err.println("Error details: " + e.getMessage());
            throw new RuntimeException("Cannot connect to LibreTranslate service at " + libreTranslateUrl);

        } catch (Exception e) {
            System.err.println("Translation failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Translation failed", e);
        }
    }

    public List<Language> getSupportedLanguages() {
        try {
            // Set proper headers for languages endpoint too
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String url = libreTranslateUrl + "/languages";
            if (!apiKey.isEmpty()) {
                url += "?api_key=" + apiKey;
            }

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Language[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Language[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Failed to get supported languages: " + e.getMessage());
        }

        // Return default languages if API call fails
        return getDefaultLanguages();
    }

    private List<Language> getDefaultLanguages() {
        return Arrays.asList(
                new Language("en", "English"),
                new Language("fr", "French"),
                new Language("es", "Spanish"),
                new Language("de", "German"),
                new Language("sw", "Swahili"),
                new Language("ar", "Arabic"),
                new Language("rw", "Kinyarwanda")
        );
    }

    // Add a health check method
    public boolean isServiceAvailable() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    libreTranslateUrl + "/languages",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            System.err.println("LibreTranslate service check failed: " + e.getMessage());
            return false;
        }
    }
}