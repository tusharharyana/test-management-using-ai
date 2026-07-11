package com.example.demo.dto.response;

import com.example.demo.enums.TestStatus;

import java.time.LocalDateTime;
import java.util.List;

public class TestResponse {

    private Long id;

    private String title;

    private String description;

    private String accessCode;

    private Integer durationMinutes;

    private TestStatus status;

    private LocalDateTime createdAt;

    private List<QuestionResponse> questions;


    public TestResponse() {
    }


    public TestResponse(
            Long id,
            String title,
            String description,
            String accessCode,
            Integer durationMinutes,
            TestStatus status,
            LocalDateTime createdAt,
            List<QuestionResponse> questions
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.accessCode = accessCode;
        this.durationMinutes = durationMinutes;
        this.status = status;
        this.createdAt = createdAt;
        this.questions = questions;
    }


    public Long getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }


    public String getAccessCode() {
        return accessCode;
    }


    public Integer getDurationMinutes() {
        return durationMinutes;
    }


    public TestStatus getStatus() {
        return status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public List<QuestionResponse> getQuestions() {
        return questions;
    }
}