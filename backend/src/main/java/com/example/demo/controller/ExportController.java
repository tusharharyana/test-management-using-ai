package com.example.demo.controller;

import com.example.demo.repository.TestAttemptRepository;
import com.example.demo.service.ExcelExportService;
import com.example.demo.service.PdfExportService;
import com.example.demo.entity.TestAttempt;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tests")
@CrossOrigin(origins = "*")
public class ExportController {

    private final ExcelExportService excelExportService;
    private final PdfExportService pdfExportService;
    private final TestAttemptRepository testAttemptRepository;


    public ExportController(
            ExcelExportService excelExportService,
            PdfExportService pdfExportService,
            TestAttemptRepository testAttemptRepository
    ) {
        this.excelExportService = excelExportService;
        this.pdfExportService = pdfExportService;
        this.testAttemptRepository = testAttemptRepository;
    }


    // =========================================================
    // TEACHER - EXPORT TEST RESULTS TO EXCEL
    // =========================================================

    @GetMapping("/{testId}/export")
    public ResponseEntity<byte[]> exportTestResults(
            @PathVariable Long testId
    ) {

        byte[] excel =
                excelExportService.exportTestResults(testId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Test_"
                                + testId
                                + "_Results.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(excel);
    }


    // =========================================================
    // STUDENT - DOWNLOAD THEIR TEST AS PDF
    // =========================================================

    @GetMapping("/attempt/{attemptId}")
    public ResponseEntity<byte[]> downloadTest(
            @PathVariable Long attemptId
    ) {

        TestAttempt attempt =
                testAttemptRepository
                        .findById(attemptId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Test attempt not found"
                                )
                        );


        byte[] pdf =
                pdfExportService.generateTestPdf(
                        attemptId
                );


        String fileName =
                "DSA_ASSESSMENT_"
                        + attempt.getTest().getTitle()
                        .replaceAll(
                                "[^a-zA-Z0-9-_]",
                                "_"
                        )
                        + "_"
                        + attempt.getStudentUid()
                        + ".pdf";


        HttpHeaders headers =
                new HttpHeaders();


        headers.setContentType(
                MediaType.APPLICATION_PDF
        );


        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename(fileName)
                        .build()
        );


        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdf);
    }
}