package com.cars.child_abuse_reporting_system.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchTranslationRequest {
    private Map<String, String> texts;
    private String source;
    private String target;
}
