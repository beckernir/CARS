package com.cars.child_abuse_reporting_system.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TranslationResponse {
    private String translatedText;

    public TranslationResponse(String translatedText) {
        this.translatedText = translatedText;
    }
}

