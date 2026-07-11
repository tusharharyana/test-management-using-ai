package com.example.demo.dto.response;

import com.example.demo.enums.ProgrammingLanguage;
import com.example.demo.enums.SubmissionStatus;

import java.time.LocalDateTime;

public class SubmissionResponse {

    private Long submissionId;

    private Long attemptId;

    private Long questionId;

    private ProgrammingLanguage language;

    private SubmissionStatus status;

    private LocalDateTime submittedAt;


    public SubmissionResponse() {
    }


    public SubmissionResponse(
            Long submissionId,
            Long attemptId,
            Long questionId,
            ProgrammingLanguage language,
            SubmissionStatus status,
            LocalDateTime submittedAt
    ) {
        this.submissionId = submissionId;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.language = language;
        this.status = status;
        this.submittedAt = submittedAt;
    }


    public Long getSubmissionId() {
        return submissionId;
    }


    public Long getAttemptId() {
        return attemptId;
    }


    public Long getQuestionId() {
        return questionId;
    }


    public ProgrammingLanguage getLanguage() {
        return language;
    }


    public SubmissionStatus getStatus() {
        return status;
    }


    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
}