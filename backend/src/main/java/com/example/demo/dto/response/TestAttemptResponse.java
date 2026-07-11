package com.example.demo.dto.response;

import com.example.demo.enums.AttemptStatus;

import java.time.LocalDateTime;
import java.util.List;

public class TestAttemptResponse {

    private Long attemptId;

    private String studentName;

    private String studentUid;

    private Long testId;

    private String testTitle;

    private Integer durationMinutes;

    private AttemptStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime expiresAt;

    private List<QuestionResponse> questions;


    public TestAttemptResponse() {
    }


    public TestAttemptResponse(
            Long attemptId,
            String studentName,
            String studentUid,
            Long testId,
            String testTitle,
            Integer durationMinutes,
            AttemptStatus status,
            LocalDateTime startedAt,
            LocalDateTime expiresAt,
            List<QuestionResponse> questions
    ) {
        this.attemptId = attemptId;
        this.studentName = studentName;
        this.studentUid = studentUid;
        this.testId = testId;
        this.testTitle = testTitle;
        this.durationMinutes = durationMinutes;
        this.status = status;
        this.startedAt = startedAt;
        this.expiresAt = expiresAt;
        this.questions = questions;
    }


    public Long getAttemptId() {
        return attemptId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentUid() {
        return studentUid;
    }

    public Long getTestId() {
        return testId;
    }

    public String getTestTitle() {
        return testTitle;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public List<QuestionResponse> getQuestions() {
        return questions;
    }
}