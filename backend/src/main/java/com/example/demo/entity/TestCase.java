package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "test_cases")
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Lob
    @Column(
            name = "input",
            columnDefinition = "LONGTEXT",
            nullable = false
    )
    private String input;

    @Lob
    @Column(
            name = "expected_output",
            columnDefinition = "LONGTEXT",
            nullable = false
    )
    private String expectedOutput;

    @Column(
            name = "test_case_order",
            nullable = false
    )
    private Integer testCaseOrder;


    // =========================
    // Constructors
    // =========================

    public TestCase() {
    }

    public TestCase(
            String input,
            String expectedOutput,
            Integer testCaseOrder
    ) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.testCaseOrder = testCaseOrder;
    }


    // =========================
    // Helper
    // =========================

    public void setQuestion(Question question) {
        this.question = question;
    }


    // =========================
    // Getters
    // =========================

    public Long getId() {
        return id;
    }

    public String getInput() {
        return input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public Integer getTestCaseOrder() {
        return testCaseOrder;
    }

    public Question getQuestion() {
        return question;
    }


    // =========================
    // Setters
    // =========================

    public void setId(Long id) {
        this.id = id;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public void setTestCaseOrder(Integer testCaseOrder) {
        this.testCaseOrder = testCaseOrder;
    }
}