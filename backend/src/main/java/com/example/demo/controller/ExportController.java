package com.example.demo.controller;

import com.example.demo.service.ExcelExportService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tests")
@CrossOrigin(origins = "*")
public class ExportController {

    private final ExcelExportService excelExportService;

    public ExportController(
            ExcelExportService excelExportService
    ) {
        this.excelExportService = excelExportService;
    }

    @GetMapping("/{testId}/export")
    public ResponseEntity<byte[]> exportTestResults(
            @PathVariable Long testId
    ) {

        byte[] excel =
                excelExportService.exportTestResults(testId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Test_" + testId + "_Results.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(excel);
    }
}