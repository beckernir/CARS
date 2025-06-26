package com.cars.child_abuse_reporting_system.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
public class TranslationRequest {
    private String q;
    private String source;
    private String target;

    // Constructors
    public TranslationRequest() {
    }

    public TranslationRequest(String q, String source, String target) {
        this.q = q;
        this.source = source;
        this.target = target;
    }

}
  