package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(
            name = "problem_statement",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String problemStatement;

    @Column(columnDefinition = "TEXT")
    private String examples;

    @Column(name = "max_marks", nullable = false)
    private Integer maxMarks;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;


    // =========================
    // Constructors
    // =========================

    public Question() {
    }

    public Question(
            String title,
            String problemStatement,
            String examples,
            Integer maxMarks,
            Integer questionOrder
    ) {
        this.title = title;
        this.problemStatement = problemStatement;
        this.examples = examples;
        this.maxMarks = maxMarks;
        this.questionOrder = questionOrder;
    }


    // =========================
    // Getters and Setters
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setProblemStatement(String problemStatement) {
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

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }


    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }
}