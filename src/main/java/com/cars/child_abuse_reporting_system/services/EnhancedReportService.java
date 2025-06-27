package com.cars.child_abuse_reporting_system.services;

import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.entities.Interview;
import com.cars.child_abuse_reporting_system.entities.Summary;
import com.cars.child_abuse_reporting_system.repositories.CaseReportRepository;
import com.cars.child_abuse_reporting_system.repositories.InterviewRepository;
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
public class EnhancedReportService {

    @Autowired
    private CaseReportRepository caseReportRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    private static final String[] BASIC_HEADERS = {
            "No.", "Case ID", "Child Name", "Abuse Type", "Status", "Location", "Date Reported"
    };

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Generate comprehensive PDF report for single case
    public byte[] generateComprehensiveCasePdfReport(Long caseId) throws IOException {
        Optional<CaseReport> caseReportOpt = caseReportRepository.findById(caseId);
        if (caseReportOpt.isPresent()) {
            CaseReport caseReport = caseReportOpt.get();
            List<Interview> interviews = interviewRepository.findByCaseReportIdOrderByInterviewDateDesc(caseReport.getId());
            return createComprehensivePdfReport(caseReport, interviews);
        }
        throw new RuntimeException("Case not found with ID: " + caseId);
    }

    // Generate comprehensive CSV report for single case
    public byte[] generateComprehensiveCaseCsvReport(Long caseId) throws IOException {
        Optional<CaseReport> caseReportOpt = caseReportRepository.findById(caseId);
        if (caseReportOpt.isPresent()) {
            CaseReport caseReport = caseReportOpt.get();
            List<Interview> interviews = interviewRepository.findByCaseReportIdOrderByInterviewDateDesc(caseReport.getId());
            return createComprehensiveCsvReport(caseReport, interviews);
        }
        throw new RuntimeException("Case not found with ID: " + caseId);
    }

    // Generate comprehensive Excel report for single case
    public byte[] generateComprehensiveCaseExcelReport(Long caseId) throws IOException {
        Optional<CaseReport> caseReportOpt = caseReportRepository.findById(caseId);
        if (caseReportOpt.isPresent()) {
            CaseReport caseReport = caseReportOpt.get();
            List<Interview> interviews = interviewRepository.findByCaseReportIdOrderByInterviewDateDesc(caseReport.getId());
            return createComprehensiveExcelReport(caseReport, interviews);
        }
        throw new RuntimeException("Case not found with ID: " + caseId);
    }

    // Create comprehensive PDF report
    private byte[] createComprehensivePdfReport(CaseReport caseReport, List<Interview> interviews) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Comprehensive Case Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            // Case ID
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph caseIdPara = new Paragraph("Case ID: " + caseReport.getCaseId(), subtitleFont);
            caseIdPara.setAlignment(Element.ALIGN_CENTER);
            caseIdPara.setSpacingAfter(20);
            document.add(caseIdPara);

            // Generation date
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph datePara = new Paragraph("Generated on: " + LocalDateTime.now().format(DATE_TIME_FORMATTER), dateFont);
            datePara.setAlignment(Element.ALIGN_RIGHT);
            datePara.setSpacingAfter(20);
            document.add(datePara);

            // Case Information Section
            addSectionHeader(document, "CASE INFORMATION");
            addCaseInformationToPdf(document, caseReport);

            // Reporter Information Section
            addSectionHeader(document, "REPORTER INFORMATION");
            addReporterInformationToPdf(document, caseReport);

            // Child Information Section
            addSectionHeader(document, "CHILD INFORMATION");
            addChildInformationToPdf(document, caseReport);

            // Incident Information Section
            addSectionHeader(document, "INCIDENT INFORMATION");
            addIncidentInformationToPdf(document, caseReport);

            // Location Information Section
            addSectionHeader(document, "LOCATION INFORMATION");
            addLocationInformationToPdf(document, caseReport);

            // File Attachments Section
            addSectionHeader(document, "FILE ATTACHMENTS");
            addFileAttachmentsToPdf(document, caseReport);

            // Translation Information Section
            if (caseReport.getOriginalLanguage() != null || caseReport.getTranslatedLanguage() != null) {
                addSectionHeader(document, "TRANSLATION INFORMATION");
                addTranslationInformationToPdf(document, caseReport);
            }

            // Summaries Section
            if (caseReport.getSummaries() != null && !caseReport.getSummaries().isEmpty()) {
                addSectionHeader(document, "CASE SUMMARIES");
                addSummariesToPdf(document, caseReport.getSummaries());
            }

            // Interviews Section
            if (!interviews.isEmpty()) {
                addSectionHeader(document, "INTERVIEWS");
                addInterviewsToPdf(document, interviews);
            }

            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Error creating comprehensive PDF report", e);
        }
    }

    private void addSectionHeader(Document document, String headerText) throws DocumentException {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
        Paragraph header = new Paragraph(headerText, headerFont);
        header.setSpacingBefore(15);
        header.setSpacingAfter(10);
        document.add(header);
    }

    private void addCaseInformationToPdf(Document document, CaseReport caseReport) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);

        addTableRow(table, "Case ID:", caseReport.getCaseId());
        addTableRow(table, "Status:", String.valueOf(caseReport.getStatus()));
        addTableRow(table, "Report Date:", caseReport.getReportDate() != null ?
                caseReport.getReportDate().format(DATE_TIME_FORMATTER) : "N/A");
        addTableRow(table, "Created By User ID:", caseReport.getCreatedByUserId());

        document.add(table);
    }

    private void addReporterInformationToPdf(Document document, CaseReport caseReport) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);

        addTableRow(table, "Reporter Full Name:", caseReport.getReporterFullName());
        addTableRow(table, "Abuser Gender:", caseReport.getAbuserGender());
        addTableRow(table, "Abuser Approximate Age:", String.valueOf(caseReport.getAbuserApproximateAge()));
        addTableRow(table, "Relationship to Child:", caseReport.getRelationship());

        document.add(table);
    }

    private void addChildInformationToPdf(Document document, CaseReport caseReport) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);

        addTableRow(table, "Child Full Name:", caseReport.getChildFullName());
        addTableRow(table, "Child Date of Birth:", caseReport.getChildDateOfBirth() != null ?
                caseReport.getChildDateOfBirth().format(DATE_FORMATTER) : "N/A");
        addTableRow(table, "Child Gender:", caseReport.getChildGender());

        document.add(table);
    }

    private void addIncidentInformationToPdf(Document document, CaseReport caseReport) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);

        addTableRow(table, "Abuse Type:", String.valueOf(caseReport.getAbuseType()));
        addTableRow(table, "Date of Incident:", caseReport.getDateOfIncident() != null ?
                caseReport.getDateOfIncident().format(DATE_FORMATTER) : "N/A");
        addTableRow(table, "Incident Description:", caseReport.getIncidentDescription());

        document.add(table);
    }

    private void addLocationInformationToPdf(Document document, CaseReport caseReport) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);

        addTableRow(table, "Location of Incident:", caseReport.getLocationOfIncident());
        addTableRow(table, "Used Current Location:", String.valueOf(caseReport.isUsedCurrentLocation()));
        addTableRow(table, "Latitude:", caseReport.getLatitude() != null ?
                caseReport.getLatitude().toString() : "N/A");
        addTableRow(table, "Longitude:", caseReport.getLongitude() != null ?
                caseReport.getLongitude().toString() : "N/A");

        document.add(table);
    }

    private void addFileAttachmentsToPdf(Document document, CaseReport caseReport) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);

        addTableRow(table, "Voice Report Path:", caseReport.getVoiceReportPath());
        addTableRow(table, "Video Report Path:", caseReport.getVideoReportPath());
        addTableRow(table, "Evidence File Path:", caseReport.getEvidenceFilePath());

        document.add(table);
    }

    private void addTranslationInformationToPdf(Document document, CaseReport caseReport) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);

        addTableRow(table, "Original Language:", caseReport.getOriginalLanguage());
        addTableRow(table, "Translated Language:", caseReport.getTranslatedLanguage());

        if (caseReport.getOriginalDescription() != null) {
            addTableRow(table, "Original Description:", caseReport.getOriginalDescription());
        }
        if (caseReport.getTranslatedDescription() != null) {
            addTableRow(table, "Translated Description:", caseReport.getTranslatedDescription());
        }

        document.add(table);
    }

    private void addSummariesToPdf(Document document, List<Summary> summaries) throws DocumentException {
        for (int i = 0; i < summaries.size(); i++) {
            Summary summary = summaries.get(i);

            Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Paragraph summaryHeader = new Paragraph("Summary " + (i + 1) + " - " + summary.getNoteType(), summaryFont);
            summaryHeader.setSpacingBefore(10);
            summaryHeader.setSpacingAfter(5);
            document.add(summaryHeader);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingAfter(10);

            addTableRow(table, "Note Type:", String.valueOf(summary.getNoteType()));
            addTableRow(table, "Description:", summary.getDescription());
            addTableRow(table, "Incident Details:", summary.getIncidentDetails());
            addTableRow(table, "Initial Assessment:", summary.getInitialAssessment());
            addTableRow(table, "Current Updates:", summary.getCurrentUpdates());
            addTableRow(table, "Created At:", summary.getCreatedAt() != null ?
                    summary.getCreatedAt().format(DATE_TIME_FORMATTER) : "N/A");
            addTableRow(table, "Updated At:", summary.getUpdatedAt() != null ?
                    summary.getUpdatedAt().format(DATE_TIME_FORMATTER) : "N/A");

            document.add(table);
        }
    }

    private void addInterviewsToPdf(Document document, List<Interview> interviews) throws DocumentException {
        for (int i = 0; i < interviews.size(); i++) {
            Interview interview = interviews.get(i);

            Font interviewFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Paragraph interviewHeader = new Paragraph("Interview " + (i + 1), interviewFont);
            interviewHeader.setSpacingBefore(10);
            interviewHeader.setSpacingAfter(5);
            document.add(interviewHeader);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingAfter(10);

            addTableRow(table, "Interviewee Name:", interview.getIntervieweeName());
            addTableRow(table, "Interviewer Name:", interview.getInterviewerName());
            addTableRow(table, "Interview Date:", interview.getInterviewDate() != null ?
                    interview.getInterviewDate().format(DATE_TIME_FORMATTER) : "N/A");
            addTableRow(table, "Location:", interview.getLocation());
            addTableRow(table, "Summary:", interview.getSummary());
            addTableRow(table, "Created At:", interview.getCreatedAt() != null ?
                    interview.getCreatedAt().format(DATE_TIME_FORMATTER) : "N/A");
            addTableRow(table, "Updated At:", interview.getUpdatedAt() != null ?
                    interview.getUpdatedAt().format(DATE_TIME_FORMATTER) : "N/A");

            document.add(table);
        }
    }

    private void addTableRow(PdfPTable table, String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(5);
        labelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "N/A", valueFont));
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    // Create comprehensive CSV report
    private byte[] createComprehensiveCsvReport(CaseReport caseReport, List<Interview> interviews) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             StringWriter stringWriter = new StringWriter();
             CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT)) {

            // Header
            csvPrinter.printRecord("Comprehensive Case Report - " + caseReport.getCaseId());
            csvPrinter.printRecord("Generated on: " + LocalDateTime.now().format(DATE_TIME_FORMATTER));
            csvPrinter.printRecord(); // Empty line

            // Case Information
            csvPrinter.printRecord("CASE INFORMATION");
            csvPrinter.printRecord("Field", "Value");
            csvPrinter.printRecord("Case ID", caseReport.getCaseId());
            csvPrinter.printRecord("Status", caseReport.getStatus());
            csvPrinter.printRecord("Report Date", caseReport.getReportDate() != null ?
                    caseReport.getReportDate().format(DATE_TIME_FORMATTER) : "N/A");
            csvPrinter.printRecord("Created By User ID", caseReport.getCreatedByUserId());
            csvPrinter.printRecord();

            // Reporter Information
            csvPrinter.printRecord("REPORTER INFORMATION");
            csvPrinter.printRecord("Field", "Value");
            csvPrinter.printRecord("Reporter Full Name", caseReport.getReporterFullName());
            csvPrinter.printRecord("Abuser Gender", caseReport.getAbuserGender());
            csvPrinter.printRecord("Abuser Approximate Age", caseReport.getAbuserApproximateAge());
            csvPrinter.printRecord("Relationship to Child", caseReport.getRelationship());
            csvPrinter.printRecord();

            // Child Information
            csvPrinter.printRecord("CHILD INFORMATION");
            csvPrinter.printRecord("Field", "Value");
            csvPrinter.printRecord("Child Full Name", caseReport.getChildFullName());
            csvPrinter.printRecord("Child Date of Birth", caseReport.getChildDateOfBirth() != null ?
                    caseReport.getChildDateOfBirth().format(DATE_FORMATTER) : "N/A");
            csvPrinter.printRecord("Child Gender", caseReport.getChildGender());
            csvPrinter.printRecord();

            // Incident Information
            csvPrinter.printRecord("INCIDENT INFORMATION");
            csvPrinter.printRecord("Field", "Value");
            csvPrinter.printRecord("Abuse Type", caseReport.getAbuseType());
            csvPrinter.printRecord("Date of Incident", caseReport.getDateOfIncident() != null ?
                    caseReport.getDateOfIncident().format(DATE_FORMATTER) : "N/A");
            csvPrinter.printRecord("Incident Description", caseReport.getIncidentDescription());
            csvPrinter.printRecord();

            // Location Information
            csvPrinter.printRecord("LOCATION INFORMATION");
            csvPrinter.printRecord("Field", "Value");
            csvPrinter.printRecord("Location of Incident", caseReport.getLocationOfIncident());
            csvPrinter.printRecord("Used Current Location", caseReport.isUsedCurrentLocation());
            csvPrinter.printRecord("Latitude", caseReport.getLatitude());
            csvPrinter.printRecord("Longitude", caseReport.getLongitude());
            csvPrinter.printRecord();

            // File Attachments
            csvPrinter.printRecord("FILE ATTACHMENTS");
            csvPrinter.printRecord("Field", "Value");
            csvPrinter.printRecord("Voice Report Path", caseReport.getVoiceReportPath());
            csvPrinter.printRecord("Video Report Path", caseReport.getVideoReportPath());
            csvPrinter.printRecord("Evidence File Path", caseReport.getEvidenceFilePath());
            csvPrinter.printRecord();

            // Translation Information
            if (caseReport.getOriginalLanguage() != null || caseReport.getTranslatedLanguage() != null) {
                csvPrinter.printRecord("TRANSLATION INFORMATION");
                csvPrinter.printRecord("Field", "Value");
                csvPrinter.printRecord("Original Language", caseReport.getOriginalLanguage());
                csvPrinter.printRecord("Translated Language", caseReport.getTranslatedLanguage());
                csvPrinter.printRecord("Original Description", caseReport.getOriginalDescription());
                csvPrinter.printRecord("Translated Description", caseReport.getTranslatedDescription());
                csvPrinter.printRecord();
            }

            // Summaries
            if (caseReport.getSummaries() != null && !caseReport.getSummaries().isEmpty()) {
                csvPrinter.printRecord("CASE SUMMARIES");
                for (int i = 0; i < caseReport.getSummaries().size(); i++) {
                    Summary summary = caseReport.getSummaries().get(i);
                    csvPrinter.printRecord("Summary " + (i + 1));
                    csvPrinter.printRecord("Field", "Value");
                    csvPrinter.printRecord("Note Type", summary.getNoteType());
                    csvPrinter.printRecord("Description", summary.getDescription());
                    csvPrinter.printRecord("Incident Details", summary.getIncidentDetails());
                    csvPrinter.printRecord("Initial Assessment", summary.getInitialAssessment());
                    csvPrinter.printRecord("Current Updates", summary.getCurrentUpdates());
                    csvPrinter.printRecord("Created At", summary.getCreatedAt() != null ?
                            summary.getCreatedAt().format(DATE_TIME_FORMATTER) : "N/A");
                    csvPrinter.printRecord("Updated At", summary.getUpdatedAt() != null ?
                            summary.getUpdatedAt().format(DATE_TIME_FORMATTER) : "N/A");
                    csvPrinter.printRecord();
                }
            }

            // Interviews
            if (!interviews.isEmpty()) {
                csvPrinter.printRecord("INTERVIEWS");
                for (int i = 0; i < interviews.size(); i++) {
                    Interview interview = interviews.get(i);
                    csvPrinter.printRecord("Interview " + (i + 1));
                    csvPrinter.printRecord("Field", "Value");
                    csvPrinter.printRecord("Interviewee Name", interview.getIntervieweeName());
                    csvPrinter.printRecord("Interviewer Name", interview.getInterviewerName());
                    csvPrinter.printRecord("Interview Date", interview.getInterviewDate() != null ?
                            interview.getInterviewDate().format(DATE_TIME_FORMATTER) : "N/A");
                    csvPrinter.printRecord("Location", interview.getLocation());
                    csvPrinter.printRecord("Summary", interview.getSummary());
                    csvPrinter.printRecord("Created At", interview.getCreatedAt() != null ?
                            interview.getCreatedAt().format(DATE_TIME_FORMATTER) : "N/A");
                    csvPrinter.printRecord("Updated At", interview.getUpdatedAt() != null ?
                            interview.getUpdatedAt().format(DATE_TIME_FORMATTER) : "N/A");
                    csvPrinter.printRecord();
                }
            }

            csvPrinter.flush();
            return stringWriter.toString().getBytes();
        }
    }

    // Create comprehensive Excel report
    private byte[] createComprehensiveExcelReport(CaseReport caseReport, List<Interview> interviews) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Comprehensive Case Report");
            int rowNum = 0;

            // Title
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Comprehensive Case Report - " + caseReport.getCaseId());
            CellStyle titleStyle = createBoldStyle(workbook);
            titleCell.setCellStyle(titleStyle);

            // Generation date
            Row dateRow = sheet.createRow(rowNum++);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Generated on: " + LocalDateTime.now().format(DATE_TIME_FORMATTER));
            rowNum++; // Empty row

            // Case Information
            rowNum = addExcelSection(sheet, workbook, rowNum, "CASE INFORMATION");
            rowNum = addExcelKeyValue(sheet, rowNum, "Case ID", caseReport.getCaseId());
            rowNum = addExcelKeyValue(sheet, rowNum, "Status", String.valueOf(caseReport.getStatus()));
            rowNum = addExcelKeyValue(sheet, rowNum, "Report Date", caseReport.getReportDate() != null ?
                    caseReport.getReportDate().format(DATE_TIME_FORMATTER) : "N/A");
            rowNum = addExcelKeyValue(sheet, rowNum, "Created By User ID", caseReport.getCreatedByUserId());
            rowNum++; // Empty row

            // Reporter Information
            rowNum = addExcelSection(sheet, workbook, rowNum, "REPORTER INFORMATION");
            rowNum = addExcelKeyValue(sheet, rowNum, "Reporter Full Name", caseReport.getReporterFullName());
            rowNum = addExcelKeyValue(sheet, rowNum, "Abuser Gender", caseReport.getAbuserGender());
            rowNum = addExcelKeyValue(sheet, rowNum, "Abuser Approximate Age", String.valueOf(caseReport.getAbuserApproximateAge()));
            rowNum = addExcelKeyValue(sheet, rowNum, "Relationship to Child", caseReport.getRelationship());
            rowNum++; // Empty row

            // Child Information
            rowNum = addExcelSection(sheet, workbook, rowNum, "CHILD INFORMATION");
            rowNum = addExcelKeyValue(sheet, rowNum, "Child Full Name", caseReport.getChildFullName());
            rowNum = addExcelKeyValue(sheet, rowNum, "Child Date of Birth", caseReport.getChildDateOfBirth() != null ?
                    caseReport.getChildDateOfBirth().format(DATE_FORMATTER) : "N/A");
            rowNum = addExcelKeyValue(sheet, rowNum, "Child Gender", caseReport.getChildGender());
            rowNum++; // Empty row

            // Incident Information
            rowNum = addExcelSection(sheet, workbook, rowNum, "INCIDENT INFORMATION");
            rowNum = addExcelKeyValue(sheet, rowNum, "Abuse Type", String.valueOf(caseReport.getAbuseType()));
            rowNum = addExcelKeyValue(sheet, rowNum, "Date of Incident", caseReport.getDateOfIncident() != null ?
                    caseReport.getDateOfIncident().format(DATE_FORMATTER) : "N/A");
            rowNum = addExcelKeyValue(sheet, rowNum, "Incident Description", caseReport.getIncidentDescription());
            rowNum++; // Empty row

            // Location Information
            rowNum = addExcelSection(sheet, workbook, rowNum, "LOCATION INFORMATION");
            rowNum = addExcelKeyValue(sheet, rowNum, "Location of Incident", caseReport.getLocationOfIncident());
            rowNum = addExcelKeyValue(sheet, rowNum, "Used Current Location", String.valueOf(caseReport.isUsedCurrentLocation()));
            rowNum = addExcelKeyValue(sheet, rowNum, "Latitude", caseReport.getLatitude() != null ?
                    caseReport.getLatitude().toString() : "N/A");
            rowNum = addExcelKeyValue(sheet, rowNum, "Longitude", caseReport.getLongitude() != null ?
                    caseReport.getLongitude().toString() : "N/A");
            rowNum++; // Empty row

            // File Attachments
            rowNum = addExcelSection(sheet, workbook, rowNum, "FILE ATTACHMENTS");
            rowNum = addExcelKeyValue(sheet, rowNum, "Voice Report Path", caseReport.getVoiceReportPath());
            rowNum = addExcelKeyValue(sheet, rowNum, "Video Report Path", caseReport.getVideoReportPath());
            rowNum = addExcelKeyValue(sheet, rowNum, "Evidence File Path", caseReport.getEvidenceFilePath());
            rowNum++; // Empty row

            // Translation Information
            if (caseReport.getOriginalLanguage() != null || caseReport.getTranslatedLanguage() != null) {
                rowNum = addExcelSection(sheet, workbook, rowNum, "TRANSLATION INFORMATION");
                rowNum = addExcelKeyValue(sheet, rowNum, "Original Language", caseReport.getOriginalLanguage());
                rowNum = addExcelKeyValue(sheet, rowNum, "Translated Language", caseReport.getTranslatedLanguage());
                rowNum = addExcelKeyValue(sheet, rowNum, "Original Description", caseReport.getOriginalDescription());
                rowNum = addExcelKeyValue(sheet, rowNum, "Translated Description", caseReport.getTranslatedDescription());
                rowNum++; // Empty row
            }

            // Summaries
            if (caseReport.getSummaries() != null && !caseReport.getSummaries().isEmpty()) {
                rowNum = addExcelSection(sheet, workbook, rowNum, "CASE SUMMARIES");
                for (int i = 0; i < caseReport.getSummaries().size(); i++) {
                    Summary summary = caseReport.getSummaries().get(i);
                    rowNum = addExcelSection(sheet, workbook, rowNum, "Summary " + (i + 1));
                    rowNum = addExcelKeyValue(sheet, rowNum, "Note Type", String.valueOf(summary.getNoteType()));
                    rowNum = addExcelKeyValue(sheet, rowNum, "Description", summary.getDescription());
                    rowNum = addExcelKeyValue(sheet, rowNum, "Incident Details", summary.getIncidentDetails());
                    rowNum = addExcelKeyValue(sheet, rowNum, "Initial Assessment", summary.getInitialAssessment());
                    rowNum = addExcelKeyValue(sheet, rowNum, "Current Updates", summary.getCurrentUpdates());
                    rowNum = addExcelKeyValue(sheet, rowNum, "Created At", summary.getCreatedAt() != null ?
                            summary.getCreatedAt().format(DATE_TIME_FORMATTER) : "N/A");
                    rowNum = addExcelKeyValue(sheet, rowNum, "Updated At", summary.getUpdatedAt() != null ?
                            summary.getUpdatedAt().format(DATE_TIME_FORMATTER) : "N/A");
                    rowNum++; // Empty row
                }
            }

            // Interviews
            if (!interviews.isEmpty()) {
                rowNum = addExcelSection(sheet, workbook, rowNum, "INTERVIEWS");
                for (int i = 0; i < interviews.size(); i++) {
                    Interview interview = interviews.get(i);
                    rowNum = addExcelSection(sheet, workbook, rowNum, "Interview " + (i + 1));
                    rowNum = addExcelKeyValue(sheet, rowNum, "Interviewee Name", interview.getIntervieweeName());
                    rowNum = addExcelKeyValue(sheet, rowNum, "Interviewer Name", interview.getInterviewerName());
                    rowNum = addExcelKeyValue(sheet, rowNum, "Interview Date", interview.getInterviewDate() != null ?
                            interview.getInterviewDate().format(DATE_TIME_FORMATTER) : "N/A");
                    rowNum = addExcelKeyValue(sheet, rowNum, "Location", interview.getLocation());
                    rowNum = addExcelKeyValue(sheet, rowNum, "Summary", interview.getSummary());
                    rowNum = addExcelKeyValue(sheet, rowNum, "Created At", interview.getCreatedAt() != null ?
                            interview.getCreatedAt().format(DATE_TIME_FORMATTER) : "N/A");
                    rowNum = addExcelKeyValue(sheet, rowNum, "Updated At", interview.getUpdatedAt() != null ?
                            interview.getUpdatedAt().format(DATE_TIME_FORMATTER) : "N/A");
                    rowNum++; // Empty row
                }
            }

            // Auto-size columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    private int addExcelSection(Sheet sheet, Workbook workbook, int rowNum, String sectionTitle) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(sectionTitle);
        cell.setCellStyle(createBoldStyle(workbook));
        return rowNum + 1;
    }

    private int addExcelKeyValue(Sheet sheet, int rowNum, String key, String value) {
        Row row = sheet.createRow(rowNum);
        Cell keyCell = row.createCell(0);
        keyCell.setCellValue(key);
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value != null ? value : "N/A");
        return rowNum + 1;
    }

    private CellStyle createBoldStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    // Original methods for backward compatibility
    public byte[] generatePdfReport(String search, Integer orderColumn, String orderDirection) throws IOException {
        List<CaseReport> cases = getFilteredAndSortedCases(search, orderColumn, orderDirection);
        return createPdfReport(cases, "All Cases Report");
    }

    public byte[] generateCsvReport(String search, Integer orderColumn, String orderDirection) throws IOException {
        List<CaseReport> cases = getFilteredAndSortedCases(search, orderColumn, orderDirection);
        return createCsvReport(cases);
    }

    public byte[] generateExcelReport(String search, Integer orderColumn, String orderDirection) throws IOException {
        List<CaseReport> cases = getFilteredAndSortedCases(search, orderColumn, orderDirection);
        return createExcelReport(cases, "All Cases Report");
    }

    public byte[] generateSingleCasePdfReport(Long caseId) throws IOException {
        Optional<CaseReport> caseReport = caseReportRepository.findById(caseId);
        if (caseReport.isPresent()) {
            List<CaseReport> cases = List.of(caseReport.get());
            return createPdfReport(cases, "Case Report - " + caseId);
        }
        throw new RuntimeException("Case not found with ID: " + caseId);
    }

    public byte[] generateSingleCaseCsvReport(Long caseId) throws IOException {
        Optional<CaseReport> caseReport = caseReportRepository.findById(caseId);
        if (caseReport.isPresent()) {
            List<CaseReport> cases = List.of(caseReport.get());
            return createCsvReport(cases);
        }
        throw new RuntimeException("Case not found with ID: " + caseId);
    }

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

        return caseReportRepository.findAll(spec, sort);
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

    // Create PDF report (original method)
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
            PdfPTable table = new PdfPTable(BASIC_HEADERS.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // Add headers
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            for (String header : BASIC_HEADERS) {
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

    // Create CSV report (original method)
    private byte[] createCsvReport(List<CaseReport> cases) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             StringWriter stringWriter = new StringWriter();
             CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT.withHeader(BASIC_HEADERS))) {

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

    // Create Excel report (original method)
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
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);

            // Create date row
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Generated on: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // Create header row
            Row headerRow = sheet.createRow(3);
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < BASIC_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(BASIC_HEADERS[i]);
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
                row.createCell(3).setCellValue(String.valueOf(caseReport.getAbuseType()));
                row.createCell(4).setCellValue(String.valueOf(caseReport.getStatus()));
                row.createCell(5).setCellValue(caseReport.getLocationOfIncident());
                row.createCell(6).setCellValue(caseReport.getReportDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }

            // Auto-size columns
            for (int i = 0; i < BASIC_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        }
    }
}