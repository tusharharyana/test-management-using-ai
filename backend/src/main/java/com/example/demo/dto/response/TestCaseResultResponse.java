package com.example.demo.dto.response;

public class TestCaseResultResponse {

    private Integer testCaseOrder;

    private String status;

    private String actualOutput;


    public TestCaseResultResponse() {
    }


    public TestCaseResultResponse(
            Integer testCaseOrder,
            String status,
            String actualOutput
    ) {
        this.testCaseOrder = testCaseOrder;
        this.status = status;
        this.actualOutput = actualOutput;
    }


    public Integer getTestCaseOrder() {
        return testCaseOrder;
    }

    public String getStatus() {
        return status;
    }

    public String getActualOutput() {
        return actualOutput;
    }
}