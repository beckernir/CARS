package com.cars.child_abuse_reporting_system.services;

import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.repositories.CaseReportRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private CaseReportRepository caseReportRepository;

    private static final String[] HEADERS = {
            "No.", "Case ID", "Child Name", "Abuse Type", "Status", "Location", "Date Reported"
    };

    // Generate PDF report for all cases
    public byte[] generatePdfReport(String search, Integer orderColumn, String orderDirection) throws IOException {
        List<CaseReport> cases = getFilteredAndSortedCases(search, orderColumn, orderDirection);
        return createPdfReport(cases, "All Cases Report");
    }

    // Generate CSV report for all cases
    public byte[] generateCsvReport(String search, Integer orderColumn, String orderDirection) throws IOException {
        List<CaseReport> cases = getFilteredAndSortedCases(search, orderColumn, orderDirection);
        return createCsvReport(cases);
    }

    // Generate Excel report for all cases
    public byte[] generateExcelReport(String search, Integer orderColumn, String orderDirection) throws IOException {
        List<CaseReport> cases = getFilteredAndSortedCases(search, orderColumn, orderDirection);
        return createExcelReport(cases, "All Cases Report");
    }

    // Generate PDF report for single case
    public byte[] generateSingleCasePdfReport(Long caseId) throws IOException {
        Optional<CaseReport> caseReport = caseReportRepository.findById(caseId);
        if (caseReport.isPresent()) {
            List<CaseReport> cases = List.of(caseReport.get());
            return createPdfReport(cases, "Case Report - " + caseId);
        }
        throw new RuntimeException("Case not found with ID: " + caseId);
    }

    // Generate CSV report for single case
    public byte[] generateSingleCaseCsvReport(Long caseId) throws IOException {
        Optional<CaseReport> caseReport = caseReportRepository.findById(caseId);
        if (caseReport.isPresent()) {
            List<CaseReport> cases = List.of(caseReport.get());
            return createCsvReport(cases);
        }
        throw new RuntimeException("Case not found with ID: " + caseId);
    }

    // Generate Excel report for single case
    public byte[] generateSingleCaseExcelReport(Long caseId) throws IOException {
        Optional<CaseReport> caseReport = caseReportRepository.findById(caseId);
        if (caseReport.isPresent()) {
            List<CaseReport> cases = List.of(caseReport.get());
            return createExcelReport(cases, "Case Report - " + caseId);
        }
        throw new RuntimeException("Case not found with ID: " + caseId);
    }

    // Helper method to get filtered and sorted cases
    private List<CaseReport> getFilteredAndSortedCases(String search, Integer orderColumn, String orderDirection) {
        Specification<CaseReport> spec = (root, query, criteriaBuilder) -> {
            if (search == null || search.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchPattern = "%" + search.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("caseId")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("childFullName")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("abuseType")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("locationOfIncident")), searchPattern));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.unsorted();
        if (orderColumn != null && orderDirection != null) {
            String sortField = getSortField(orderColumn);
            sort = "desc".equalsIgnoreCase(orderDirection) ?
                    Sort.by(sortField).descending() :
                    Sort.by(sortField).ascending();
        }

        return caseReportRepository.findAll(spec,sort);
    }

    // Map column index to field name
    private String getSortField(Integer columnIndex) {
        switch (columnIndex) {
            case 1: return "caseId";
            case 2: return "childFullName";
            case 3: return "abuseType";
            case 4: return "status";
            case 5: return "locationOfIncident";
            case 6: return "reportDate";
            default: return "reportDate";
        }
    }

    // Create PDF report
    private byte[] createPdfReport(List<CaseReport> cases, String title) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph titleParagraph = new Paragraph(title, titleFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            titleParagraph.setSpacingAfter(20);
            document.add(titleParagraph);

            // Add generation date
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph dateParagraph = new Paragraph("Generated on: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), dateFont);
            dateParagraph.setAlignment(Element.ALIGN_RIGHT);
            dateParagraph.setSpacingAfter(20);
            document.add(dateParagraph);

            // Create table
            PdfPTable table = new PdfPTable(HEADERS.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // Add headers
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            for (String header : HEADERS) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Add data rows
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            int rowNumber = 1;
            for (CaseReport caseReport : cases) {
                table.addCell(new PdfPCell(new Phrase(String.valueOf(rowNumber++), dataFont)));
                table.addCell(new PdfPCell(new Phrase(caseReport.getCaseId(), dataFont)));
                table.addCell(new PdfPCell(new Phrase(caseReport.getChildFullName(), dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(caseReport.getAbuseType()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(caseReport.getStatus()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(caseReport.getLocationOfIncident(), dataFont)));
                table.addCell(new PdfPCell(new Phrase(
                        caseReport.getReportDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), dataFont)));
            }

            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Error creating PDF report", e);
        }
    }

    // Create CSV report
    private byte[] createCsvReport(List<CaseReport> cases) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             StringWriter stringWriter = new StringWriter();
             CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT.withHeader(HEADERS))) {

            int rowNumber = 1;
            for (CaseReport caseReport : cases) {
                csvPrinter.printRecord(
                        rowNumber++,
                        caseReport.getCaseId(),
                        caseReport.getChildFullName(),
                        caseReport.getAbuseType(),
                        caseReport.getStatus(),
                        caseReport.getLocationOfIncident(),
                        caseReport.getReportDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                );
            }

            csvPrinter.flush();
            return stringWriter.toString().getBytes();
        }
    }

    // Create Excel report
    private byte[] createExcelReport(List<CaseReport> cases, String title) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Cases Report");

            // Create title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);

            // Create title style
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = (Font) workbook.createFont();
//            titleFont.setBold(true);
//            titleFont.setFontHeightInPoints((short) 16);
//            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);

            // Create date row
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Generated on: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // Create header row
            Row headerRow = sheet.createRow(3);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = (Font) workbook.createFont();
//            headerFont.se(true);
//            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // Add data rows
            int rowNum = 4;
            int caseNumber = 1;
            for (CaseReport caseReport : cases) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(caseNumber++);
                row.createCell(1).setCellValue(caseReport.getCaseId());
                row.createCell(2).setCellValue(caseReport.getChildFullName());
//                row.createCell(3).setCellValue(caseReport.getAbuseType());
//                row.createCell(4).setCellValue(caseReport.getStatus());
                row.createCell(5).setCellValue(caseReport.getLocationOfIncident());
                row.createCell(6).setCellValue(caseReport.getReportDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }

            // Auto-size columns
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        }
    }
}