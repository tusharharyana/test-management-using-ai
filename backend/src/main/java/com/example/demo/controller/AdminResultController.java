package com.example.demo.controller;

import com.example.demo.dto.request.OverrideMarksRequest;
import com.example.demo.dto.response.EvaluationSummaryResponse;
import com.example.demo.dto.response.StudentTestResultResponse;
import com.example.demo.service.AdminResultService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.example.demo.service.EvaluationService;

@RestController
@RequestMapping("/api/admin")
public class AdminResultController {

    private final AdminResultService adminResultService;
    private final EvaluationService evaluationService;


    public AdminResultController(
            AdminResultService adminResultService,
            EvaluationService evaluationService
    ) {
        this.adminResultService = adminResultService;
        this.evaluationService = evaluationService;
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

    @PostMapping("/tests/{testId}/reevaluate")
        public ResponseEntity<String> reEvaluateTest(
                @PathVariable Long testId
        ) {

        evaluationService.reEvaluateTest(testId);

        return ResponseEntity.ok(
                "AI re-evaluation started for all submissions"
        );
        }

        @PostMapping(
        "/tests/{testId}/results/{attemptId}/reevaluate"
        )
        public ResponseEntity<String> reEvaluateStudent(
                @PathVariable Long testId,
                @PathVariable Long attemptId
        ) {

        evaluationService.reEvaluateAttempt(attemptId);

        return ResponseEntity.ok(
                "AI re-evaluation started for this student"
        );
        }
}