package com.example.demo.dto.response;

import java.util.List;

public class RunCodeResponse {

    private String status;

    private Integer totalTestCases;

    private Integer passedTestCases;

    private Integer failedTestCases;

    private String compileOutput;

    private String stderr;

    private String message;

    private List<TestCaseResultResponse> results;


    public RunCodeResponse() {
    }


    public RunCodeResponse(
            String status,
            Integer totalTestCases,
            Integer passedTestCases,
            Integer failedTestCases,
            String compileOutput,
            String stderr,
            String message,
            List<TestCaseResultResponse> results
    ) {
        this.status = status;
        this.totalTestCases = totalTestCases;
        this.passedTestCases = passedTestCases;
        this.failedTestCases = failedTestCases;
        this.compileOutput = compileOutput;
        this.stderr = stderr;
        this.message = message;
        this.results = results;
    }


    public String getStatus() {
        return status;
    }

    public Integer getTotalTestCases() {
        return totalTestCases;
    }

    public Integer getPassedTestCases() {
        return passedTestCases;
    }

    public Integer getFailedTestCases() {
        return failedTestCases;
    }

    public String getCompileOutput() {
        return compileOutput;
    }

    public String getStderr() {
        return stderr;
    }

    public String getMessage() {
        return message;
    }

    public List<TestCaseResultResponse> getResults() {
        return results;
    }
}