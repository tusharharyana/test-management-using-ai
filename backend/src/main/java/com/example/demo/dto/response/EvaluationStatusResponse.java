package com.example.demo.dto.response;

public class EvaluationStatusResponse {

    private String status;

    private int totalSubmissions;

    private int evaluatedSubmissions;

    private int failedSubmissions;


    public EvaluationStatusResponse(
            String status,
            int totalSubmissions,
            int evaluatedSubmissions,
            int failedSubmissions
    ) {

        this.status = status;
        this.totalSubmissions = totalSubmissions;
        this.evaluatedSubmissions = evaluatedSubmissions;
        this.failedSubmissions = failedSubmissions;
    }


    public String getStatus() {
        return status;
    }


    public int getTotalSubmissions() {
        return totalSubmissions;
    }


    public int getEvaluatedSubmissions() {
        return evaluatedSubmissions;
    }


    public int getFailedSubmissions() {
        return failedSubmissions;
    }
}