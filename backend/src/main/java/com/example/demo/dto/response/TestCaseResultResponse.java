package com.example.demo.dto.response;

public class TestCaseResultResponse {

    private Integer testCaseOrder;

    private String status;

    private String input;

    private String expectedOutput;

    private String actualOutput;


    public TestCaseResultResponse() {
    }


    public TestCaseResultResponse(
            Integer testCaseOrder,
            String status,
            String input,
            String expectedOutput,
            String actualOutput
    ) {
        this.testCaseOrder = testCaseOrder;
        this.status = status;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.actualOutput = actualOutput;
    }


    public Integer getTestCaseOrder() {
        return testCaseOrder;
    }


    public String getStatus() {
        return status;
    }


    public String getInput() {
        return input;
    }


    public String getExpectedOutput() {
        return expectedOutput;
    }


    public String getActualOutput() {
        return actualOutput;
    }
}