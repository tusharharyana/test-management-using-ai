package com.example.demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateTestRequest {

    @NotBlank(message = "Test title is required")
    private String title;


    private String description;


    @NotNull(message = "Test duration is required")
    @Min(
            value = 1,
            message = "Test duration must be at least 1 minute"
    )
    private Integer durationMinutes;


    @NotEmpty(message = "At least one question is required")
    @Valid
    private List<CreateQuestionRequest> questions;


    public CreateTestRequest() {
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public Integer getDurationMinutes() {
        return durationMinutes;
    }


    public void setDurationMinutes(
            Integer durationMinutes
    ) {
        this.durationMinutes = durationMinutes;
    }


    public List<CreateQuestionRequest> getQuestions() {
        return questions;
    }


    public void setQuestions(
            List<CreateQuestionRequest> questions
    ) {
        this.questions = questions;
    }
}