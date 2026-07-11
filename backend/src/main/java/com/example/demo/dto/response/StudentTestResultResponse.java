package com.example.demo.dto.response;

import com.example.demo.enums.AttemptStatus;

import java.time.LocalDateTime;
import java.util.List;

public class StudentTestResultResponse {

    private Long attemptId;

    private String studentName;

    private String studentUid;

    private AttemptStatus attemptStatus;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    private Integer aiTotalScore;

    private Integer finalTotalScore;

    private Integer maximumPossibleScore;

    private List<SubmissionResultResponse> submissions;


    public StudentTestResultResponse() {
    }


    public StudentTestResultResponse(
            Long attemptId,
            String studentName,
            String studentUid,
            AttemptStatus attemptStatus,
            LocalDateTime startedAt,
            LocalDateTime submittedAt,
            Integer aiTotalScore,
            Integer finalTotalScore,
            Integer maximumPossibleScore,
            List<SubmissionResultResponse> submissions
    ) {
        this.attemptId = attemptId;
        this.studentName = studentName;
        this.studentUid = studentUid;
        this.attemptStatus = attemptStatus;
        this.startedAt = startedAt;
        this.submittedAt = submittedAt;
        this.aiTotalScore = aiTotalScore;
        this.finalTotalScore = finalTotalScore;
        this.maximumPossibleScore = maximumPossibleScore;
        this.submissions = submissions;
    }


    public Long getAttemptId() {
        return attemptId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentUid() {
        return studentUid;
    }

    public AttemptStatus getAttemptStatus() {
        return attemptStatus;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public Integer getAiTotalScore() {
        return aiTotalScore;
    }

    public Integer getFinalTotalScore() {
        return finalTotalScore;
    }

    public Integer getMaximumPossibleScore() {
        return maximumPossibleScore;
    }

    public List<SubmissionResultResponse> getSubmissions() {
        return submissions;
    }
}