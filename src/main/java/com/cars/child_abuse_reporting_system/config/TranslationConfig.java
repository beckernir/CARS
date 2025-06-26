package com.cars.child_abuse_reporting_system.config;//package com.cars.child_abuse_reporting_system.config;
//
//import lombok.Data;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@ConfigurationProperties(prefix = "libretranslate")
//@Component
//@Data
//public class LibreTranslateConfig {
//    private String baseUrl = "https://libretranslate.de"; // or your own instance
//    private String apiKey; // optional for some instances
//}

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

// Configuration Class
@Configuration
public class TranslationConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Configure timeout settings
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 seconds
        factory.setConnectionRequestTimeout(30000);    // 30 seconds

        restTemplate.setRequestFactory(factory);

        // Add error handler
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatusCode statusCode = response.getStatusCode();
                return statusCode.is4xxClientError() || statusCode.is5xxServerError();
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // Custom error handling if needed
                System.err.println("HTTP Error: " + response.getStatusCode() + " - " + response.getStatusText());
            }
        });

        return restTemplate;
    }
}