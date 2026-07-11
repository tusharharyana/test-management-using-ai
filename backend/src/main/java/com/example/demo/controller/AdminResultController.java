package com.example.demo.controller;

import com.example.demo.dto.request.OverrideMarksRequest;
import com.example.demo.dto.response.EvaluationSummaryResponse;
import com.example.demo.dto.response.StudentTestResultResponse;
import com.example.demo.service.AdminResultService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminResultController {

    private final AdminResultService adminResultService;


    public AdminResultController(
            AdminResultService adminResultService
    ) {
        this.adminResultService = adminResultService;
    }


    @GetMapping("/tests/{testId}/results")
    public ResponseEntity<List<StudentTestResultResponse>>
    getTestResults(
            @PathVariable Long testId
    ) {

        return ResponseEntity.ok(
                adminResultService.getTestResults(testId)
        );
    }


    @GetMapping(
            "/tests/{testId}/results/{attemptId}"
    )
    public ResponseEntity<StudentTestResultResponse>
    getStudentResult(
            @PathVariable Long testId,
            @PathVariable Long attemptId
    ) {

        return ResponseEntity.ok(
                adminResultService.getStudentResult(
                        testId,
                        attemptId
                )
        );
    }


    @PatchMapping(
            "/evaluations/{evaluationId}/marks"
    )
    public ResponseEntity<EvaluationSummaryResponse>
    overrideMarks(
            @PathVariable Long evaluationId,
            @RequestBody OverrideMarksRequest request
    ) {

        return ResponseEntity.ok(
                adminResultService.overrideMarks(
                        evaluationId,
                        request
                )
        );
    }
}