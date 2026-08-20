package com.example.demo.dto.request;

import com.example.demo.enums.ProgrammingLanguage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateSubmissionRequest {

    @NotNull(message = "Attempt ID is required")
    private Long attemptId;


    @NotNull(message = "Question ID is required")
    private Long questionId;


    @NotNull(message = "Programming language is required")
    private ProgrammingLanguage language;


    @NotBlank(message = "Source code cannot be empty")
    private String sourceCode;

    private boolean autoSubmit;


    public CreateSubmissionRequest() {
    }


    public Long getAttemptId() {
        return attemptId;
    }


    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }


    public Long getQuestionId() {
        return questionId;
    }


    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }


    public ProgrammingLanguage getLanguage() {
        return language;
    }


    public void setLanguage(
            ProgrammingLanguage language
    ) {
        this.language = language;
    }


    public String getSourceCode() {
        return sourceCode;
    }


    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

     public boolean isAutoSubmit() {
        return autoSubmit;
    }

    public void setAutoSubmit(boolean autoSubmit) {
        this.autoSubmit = autoSubmit;
    }
}