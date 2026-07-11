package com.example.demo.controller;

import com.example.demo.dto.request.CreateSubmissionRequest;
import com.example.demo.dto.response.SubmissionResponse;
import com.example.demo.service.SubmissionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.response.SubmissionResultResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;


    public SubmissionController(
            SubmissionService submissionService
    ) {
        this.submissionService = submissionService;
    }


        @PostMapping
        public ResponseEntity<SubmissionResponse> createSubmission(
                @Valid @RequestBody CreateSubmissionRequest request
        ) {

        SubmissionResponse response =
                submissionService.createSubmission(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
        }

    @GetMapping("/{submissionId}/result")
public ResponseEntity<SubmissionResultResponse>
getSubmissionResult(
        @PathVariable Long submissionId
) {

    return ResponseEntity.ok(
            submissionService.getSubmissionResult(
                    submissionId
            )
    );
}
}