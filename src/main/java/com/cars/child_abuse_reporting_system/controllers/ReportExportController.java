package com.cars.child_abuse_reporting_system.controllers;

import com.cars.child_abuse_reporting_system.services.EnhancedReportService;
import com.cars.child_abuse_reporting_system.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/reports/export")
public class ReportExportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private EnhancedReportService enhancedReportService;

    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");


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
    public ResponseEntity<InputStreamResource> exportSingleCaseToPdf(@PathVariable Long caseId) throws IOException {
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
    public ResponseEntity<InputStreamResource> exportSingleCaseToCsv(@PathVariable Long caseId) throws IOException {
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
    public ResponseEntity<InputStreamResource> exportSingleCaseToExcel(@PathVariable Long caseId) throws IOException {
        byte[] excelData = reportService.generateSingleCaseExcelReport(caseId);
        ByteArrayInputStream bis = new ByteArrayInputStream(excelData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=case_" + caseId + "_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(bis));
    }


    // ========== COMPREHENSIVE SINGLE CASE REPORTS ==========

    /**
     * Generate comprehensive PDF report for a single case
     */
    @GetMapping("/comprehensive/pdf/{caseId}")
    public ResponseEntity<byte[]> generateComprehensiveCasePdfReport(@PathVariable Long caseId) {
        try {
            byte[] pdfData = enhancedReportService.generateComprehensiveCasePdfReport(caseId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    generateFileName("comprehensive_case_report_" + caseId, "pdf"));
            headers.setContentLength(pdfData.length);

            return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating comprehensive PDF report: " + e.getMessage()).getBytes());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage().getBytes());
        }
    }

    /**
     * Generate comprehensive CSV report for a single case
     */
    @GetMapping("/comprehensive/csv/{caseId}")
    public ResponseEntity<byte[]> generateComprehensiveCaseCsvReport(@PathVariable Long caseId) {
        try {
            byte[] csvData = enhancedReportService.generateComprehensiveCaseCsvReport(caseId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment",
                    generateFileName("comprehensive_case_report_" + caseId, "csv"));
            headers.setContentLength(csvData.length);

            return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating comprehensive CSV report: " + e.getMessage()).getBytes());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage().getBytes());
        }
    }

    /**
     * Generate comprehensive Excel report for a single case
     */
    @GetMapping("/comprehensive/excel/{caseId}")
    public ResponseEntity<byte[]> generateComprehensiveCaseExcelReport(@PathVariable Long caseId) {
        try {
            byte[] excelData = enhancedReportService.generateComprehensiveCaseExcelReport(caseId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment",
                    generateFileName("comprehensive_case_report_" + caseId, "xlsx"));
            headers.setContentLength(excelData.length);

            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating comprehensive Excel report: " + e.getMessage()).getBytes());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage().getBytes());
        }
    }

    // ========== BASIC SINGLE CASE REPORTS ==========

    /**
     * Generate basic PDF report for a single case
     */
    @GetMapping("/basic/pdf/{caseId}")
    public ResponseEntity<byte[]> generateSingleCasePdfReport(@PathVariable Long caseId) {
        try {
            byte[] pdfData = enhancedReportService.generateSingleCasePdfReport(caseId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    generateFileName("case_report_" + caseId, "pdf"));
            headers.setContentLength(pdfData.length);

            return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating PDF report: " + e.getMessage()).getBytes());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage().getBytes());
        }
    }

    /**
     * Generate basic CSV report for a single case
     */
    @GetMapping("/case/{caseId}/basic/csv")
    public ResponseEntity<byte[]> generateSingleCaseCsvReport(@PathVariable Long caseId) {
        try {
            byte[] csvData = enhancedReportService.generateSingleCaseCsvReport(caseId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment",
                    generateFileName("case_report_" + caseId, "csv"));
            headers.setContentLength(csvData.length);

            return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating CSV report: " + e.getMessage()).getBytes());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage().getBytes());
        }
    }

    /**
     * Generate basic Excel report for a single case
     */
    @GetMapping("/case/{caseId}/basic/excel")
    public ResponseEntity<byte[]> generateSingleCaseExcelReport(@PathVariable Long caseId) {
        try {
            byte[] excelData = enhancedReportService.generateSingleCaseExcelReport(caseId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment",
                    generateFileName("case_report_" + caseId, "xlsx"));
            headers.setContentLength(excelData.length);

            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating Excel report: " + e.getMessage()).getBytes());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage().getBytes());
        }
    }
    /**
     * Generate filename with timestamp
     */
    private String generateFileName(String baseName, String extension) {
        String timestamp = LocalDateTime.now().format(FILE_DATE_FORMATTER);
        return baseName + "_" + timestamp + "." + extension;
    }

}
