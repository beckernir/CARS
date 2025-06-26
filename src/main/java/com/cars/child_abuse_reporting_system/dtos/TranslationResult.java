package com.cars.child_abuse_reporting_system.dtos;

import lombok.Data;

@Data
public class TranslationResult {
    private String translatedText;
    private String targetLanguage;
}