package com.example.demo.controller;

import com.example.demo.dto.request.CreateTestRequest;
import com.example.demo.dto.response.TestResponse;
import com.example.demo.service.TestService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;


    public TestController(TestService testService) {
        this.testService = testService;
    }


    // =========================
    // Create Test
    // POST /api/tests
    // =========================

    @PostMapping
    public ResponseEntity<TestResponse> createTest(
            @RequestBody CreateTestRequest request
    ) {

        TestResponse response =
                testService.createTest(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================
    // Get All Tests
    // GET /api/tests
    // =========================

    @GetMapping
    public ResponseEntity<List<TestResponse>> getAllTests() {

        return ResponseEntity.ok(
                testService.getAllTests()
        );
    }


    // =========================
    // Get Test By ID
    // GET /api/tests/1
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<TestResponse> getTestById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                testService.getTestById(id)
        );
    }


    // =========================
    // Get Test By Access Code
    // GET /api/tests/access/ABC123
    // =========================

    @GetMapping("/access/{accessCode}")
    public ResponseEntity<TestResponse> getTestByAccessCode(
            @PathVariable String accessCode
    ) {

        return ResponseEntity.ok(
                testService.getTestByAccessCode(accessCode)
        );
    }

    @PatchMapping("/{id}/activate")
public ResponseEntity<TestResponse> activateTest(
        @PathVariable Long id
) {

    return ResponseEntity.ok(
            testService.activateTest(id)
    );
}

@PatchMapping("/{id}/complete")
public ResponseEntity<TestResponse> completeTest(
        @PathVariable Long id
) {

    return ResponseEntity.ok(
            testService.completeTest(id)
    );
}
}