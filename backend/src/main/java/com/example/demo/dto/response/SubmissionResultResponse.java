package com.example.demo.dto.response;

import com.example.demo.enums.ProgrammingLanguage;
import com.example.demo.enums.SubmissionStatus;

import java.time.LocalDateTime;

public class SubmissionResultResponse {

    private Long submissionId;

    private Long attemptId;

    private Long questionId;

    private String questionTitle;

    private ProgrammingLanguage language;

    private SubmissionStatus status;

    private LocalDateTime submittedAt;

    private EvaluationSummaryResponse evaluation;


    public SubmissionResultResponse() {
    }


    public SubmissionResultResponse(
            Long submissionId,
            Long attemptId,
            Long questionId,
            String questionTitle,
            ProgrammingLanguage language,
            SubmissionStatus status,
            LocalDateTime submittedAt,
            EvaluationSummaryResponse evaluation
    ) {
        this.submissionId = submissionId;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.language = language;
        this.status = status;
        this.submittedAt = submittedAt;
        this.evaluation = evaluation;
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

    public String getQuestionTitle() {
        return questionTitle;
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

    public EvaluationSummaryResponse getEvaluation() {
        return evaluation;
    }
}