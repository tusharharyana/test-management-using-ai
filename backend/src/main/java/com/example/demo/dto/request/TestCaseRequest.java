package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class TestCaseRequest {

    private String input;

    @NotBlank(message = "Expected output is required")
    private String expectedOutput;

    @NotNull(message = "Test case order is required")
    @Min(value = 1, message = "Test case order must be at least 1")
    private Integer testCaseOrder;


    public TestCaseRequest() {
    }


    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }


    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }


    public Integer getTestCaseOrder() {
        return testCaseOrder;
    }

    public void setTestCaseOrder(Integer testCaseOrder) {
        this.testCaseOrder = testCaseOrder;
    }
}