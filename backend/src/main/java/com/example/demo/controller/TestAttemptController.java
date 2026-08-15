package com.example.demo.controller;

import com.example.demo.dto.request.StartTestRequest;
import com.example.demo.dto.response.TestAttemptResponse;
import com.example.demo.service.TestAttemptService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attempts")
public class TestAttemptController {

    private final TestAttemptService testAttemptService;


    public TestAttemptController(
            TestAttemptService testAttemptService
    ) {
        this.testAttemptService = testAttemptService;
    }


    @PostMapping("/start")
    public ResponseEntity<TestAttemptResponse> startTest(
                @Valid @RequestBody StartTestRequest request
        ){

        TestAttemptResponse response =
                testAttemptService.startTest(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{attemptId}/submit")
        public ResponseEntity<TestAttemptResponse> submitTest(
                @PathVariable Long attemptId
        ) {

        return ResponseEntity.ok(
                testAttemptService.submitTest(attemptId)
        );
        }
        @DeleteMapping("/{attemptId}")
        public ResponseEntity<Void> deleteAttempt(
                @PathVariable Long attemptId
        ) {

        testAttemptService.deleteAttempt(attemptId);

        return ResponseEntity.noContent().build();
        }
}