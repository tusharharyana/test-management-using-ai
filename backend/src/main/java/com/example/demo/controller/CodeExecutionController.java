package com.example.demo.controller;

import com.example.demo.dto.request.RunCodeRequest;
import com.example.demo.dto.response.RunCodeResponse;
import com.example.demo.service.CodeExecutionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/code")
public class CodeExecutionController {

    private final CodeExecutionService codeExecutionService;

    public CodeExecutionController(
            CodeExecutionService codeExecutionService
    ) {
        this.codeExecutionService = codeExecutionService;
    }

    @PostMapping("/run")
    public ResponseEntity<RunCodeResponse> runCode(
            @RequestBody RunCodeRequest request
    ) throws Exception {

        RunCodeResponse response =
                codeExecutionService.runCode(request);

        return ResponseEntity.ok(response);
    }
}