package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CreateQuestionRequest {

    @NotBlank(message = "Question title is required")
    private String title;


    @NotBlank(message = "Problem statement is required")
    private String problemStatement;


    private String examples;


    @NotNull(message = "Maximum marks are required")
    @Min(
            value = 1,
            message = "Maximum marks must be at least 1"
    )
    private Integer maxMarks;


    @NotNull(message = "Question order is required")
    @Min(
            value = 1,
            message = "Question order must be at least 1"
    )
    private Integer questionOrder;

    @Valid
    @NotEmpty(message = "At least one test case is required")
    @Size(
            max = 10,
            message = "A question can have at most 10 test cases"
    )
    private List<TestCaseRequest> testCases;
        public CreateQuestionRequest() {
        }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getProblemStatement() {
        return problemStatement;
    }


    public void setProblemStatement(
            String problemStatement
    ) {
        this.problemStatement = problemStatement;
    }


    public String getExamples() {
        return examples;
    }


    public void setExamples(String examples) {
        this.examples = examples;
    }


    public Integer getMaxMarks() {
        return maxMarks;
    }


    public void setMaxMarks(Integer maxMarks) {
        this.maxMarks = maxMarks;
    }


    public Integer getQuestionOrder() {
        return questionOrder;
    }


    public void setQuestionOrder(
            Integer questionOrder
    ) {
        this.questionOrder = questionOrder;
    }
     public List<TestCaseRequest> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCaseRequest> testCases) {
        this.testCases = testCases;
    }
}