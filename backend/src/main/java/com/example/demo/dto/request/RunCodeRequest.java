package com.example.demo.dto.request;

import com.example.demo.enums.ProgrammingLanguage;

public class RunCodeRequest {

    private Long questionId;

    private ProgrammingLanguage language;

    private String sourceCode;


    public RunCodeRequest() {
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

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }


    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}