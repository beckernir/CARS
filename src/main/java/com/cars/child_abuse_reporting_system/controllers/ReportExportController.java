package com.cars.child_abuse_reporting_system.controllers;

import com.cars.child_abuse_reporting_system.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
//import java.io.IOException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/reports/export")
public class ReportExportController {

    @Autowired
    private ReportService reportService;

    // Export all cases to PDF
    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> exportAllCasesToPdf(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer orderColumn,
            @RequestParam(required = false) String orderDirection) throws IOException {

        byte[] pdfData = reportService.generatePdfReport(search, orderColumn, orderDirection);
        ByteArrayInputStream bis = new ByteArrayInputStream(pdfData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=cases_report_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    // Export all cases to CSV
    @GetMapping("/csv")
    public ResponseEntity<InputStreamResource> exportAllCasesToCsv(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer orderColumn,
            @RequestParam(required = false) String orderDirection) throws IOException {

        byte[] csvData = reportService.generateCsvReport(search, orderColumn, orderDirection);
        ByteArrayInputStream bis = new ByteArrayInputStream(csvData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=cases_report_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(bis));
    }

    // Export all cases to Excel
    @GetMapping("/excel")
    public ResponseEntity<InputStreamResource> exportAllCasesToExcel(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer orderColumn,
            @RequestParam(required = false) String orderDirection) throws IOException {

        byte[] excelData = reportService.generateExcelReport(search, orderColumn, orderDirection);
        ByteArrayInputStream bis = new ByteArrayInputStream(excelData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=cases_report_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(bis));
    }

    // Export single case to PDF
    @GetMapping("/pdf/{caseId}")
    public ResponseEntity<InputStreamResource> exportSingleCaseToPdf(@PathVariable String caseId) throws IOException {
        byte[] pdfData = reportService.generateSingleCasePdfReport(caseId);
        ByteArrayInputStream bis = new ByteArrayInputStream(pdfData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=case_" + caseId + "_report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    // Export single case to CSV
    @GetMapping("/csv/{caseId}")
    public ResponseEntity<InputStreamResource> exportSingleCaseToCsv(@PathVariable String caseId) throws IOException {
        byte[] csvData = reportService.generateSingleCaseCsvReport(caseId);
        ByteArrayInputStream bis = new ByteArrayInputStream(csvData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=case_" + caseId + "_report.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(bis));
    }

    // Export single case to Excel
    @GetMapping("/excel/{caseId}")
    public ResponseEntity<InputStreamResource> exportSingleCaseToExcel(@PathVariable String caseId) throws IOException {
        byte[] excelData = reportService.generateSingleCaseExcelReport(caseId);
        ByteArrayInputStream bis = new ByteArrayInputStream(excelData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=case_" + caseId + "_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(bis));
    }
}
