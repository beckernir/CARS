package com.cars.child_abuse_reporting_system.controllers;

import com.cars.child_abuse_reporting_system.dtos.TranslationRequest;
import com.cars.child_abuse_reporting_system.dtos.BatchTranslationRequest;
import com.cars.child_abuse_reporting_system.dtos.TranslationResponse;
import com.cars.child_abuse_reporting_system.services.TranslationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Translation Controller
@RestController
@RequestMapping("/api/translate")
@CrossOrigin(origins = "*")
public class TranslationController {

    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<String, String>> translateBatch(@RequestBody BatchTranslationRequest request) {
        try {
            Map<String, String> translations = translationService.translateBatch(
                    request.getTexts(),
                    request.getSource(),
                    request.getTarget()
            );
            return ResponseEntity.ok(translations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/text")
    public ResponseEntity<TranslationResponse> translateText(@RequestBody TranslationRequest request) {
        try {
            String translatedText = translationService.translateText(
                    request.getQ(),
                    request.getSource(),
                    request.getTarget()
            );
            return ResponseEntity.ok(new TranslationResponse(translatedText));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

