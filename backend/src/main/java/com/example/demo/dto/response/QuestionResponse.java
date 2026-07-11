package com.example.demo.dto.response;

public class QuestionResponse {

    private Long id;

    private String title;

    private String problemStatement;

    private String examples;

    private Integer maxMarks;

    private Integer questionOrder;


    public QuestionResponse() {
    }


    public QuestionResponse(
            Long id,
            String title,
            String problemStatement,
            String examples,
            Integer maxMarks,
            Integer questionOrder
    ) {
        this.id = id;
        this.title = title;
        this.problemStatement = problemStatement;
        this.examples = examples;
        this.maxMarks = maxMarks;
        this.questionOrder = questionOrder;
    }


    public Long getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }


    public String getProblemStatement() {
        return problemStatement;
    }


    public String getExamples() {
        return examples;
    }


    public Integer getMaxMarks() {
        return maxMarks;
    }


    public Integer getQuestionOrder() {
        return questionOrder;
    }
}