package com.cars.child_abuse_reporting_system.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EvidenceUploadRequest {

    private Long id;
    private MultipartFile evidenceFile;
}
