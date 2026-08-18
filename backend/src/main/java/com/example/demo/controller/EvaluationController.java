package com.example.demo.controller;

import com.example.demo.dto.response.EvaluationResponse;
import com.example.demo.service.EvaluationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.response.EvaluationStatusResponse;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;


    public EvaluationController(
            EvaluationService evaluationService
    ) {
        this.evaluationService = evaluationService;
    }


    @PostMapping("/submission/{submissionId}")
    public ResponseEntity<EvaluationResponse>
    evaluateSubmission(
            @PathVariable Long submissionId
    ) {

        return ResponseEntity.ok(
                evaluationService
                        .evaluateSubmission(submissionId)
        );
    }
        @GetMapping("/attempt/{attemptId}/status")
        public ResponseEntity<EvaluationStatusResponse>
        getAttemptEvaluationStatus(
                @PathVariable Long attemptId
        ) {

        return ResponseEntity.ok(
                evaluationService
                        .getAttemptEvaluationStatus(attemptId)
        );
        }
}