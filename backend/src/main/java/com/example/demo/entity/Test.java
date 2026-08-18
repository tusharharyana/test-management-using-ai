package com.example.demo.entity;

import com.example.demo.enums.TestStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(
            name = "access_code",
            nullable = false,
            unique = true,
            length = 20
    )
    private String accessCode;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "show_ai_score_in_pdf", nullable = false)
    private boolean showAiScoreInPdf = false;

    @Column(name = "show_ai_feedback_in_pdf", nullable = false)
    private boolean showAiFeedbackInPdf = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "test",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Question> questions = new ArrayList<>();


    // =========================
    // Lifecycle Methods
    // =========================

    @PrePersist
    protected void onCreate() {

        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = TestStatus.DRAFT;
        }
    }


    // =========================
    // Constructors
    // =========================

    public Test() {
    }

    public Test(
            String title,
            String description,
            String accessCode,
            Integer durationMinutes
    ) {
        this.title = title;
        this.description = description;
        this.accessCode = accessCode;
        this.durationMinutes = durationMinutes;
        this.status = TestStatus.DRAFT;
    }


    // =========================
    // Helper Methods
    // =========================

    public void addQuestion(Question question) {

        questions.add(question);
        question.setTest(this);
    }

    public void removeQuestion(Question question) {

        questions.remove(question);
        question.setTest(null);
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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }


    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }


    public TestStatus getStatus() {
        return status;
    }

    public void setStatus(TestStatus status) {
        this.status = status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

        public boolean isShowAiScoreInPdf() {
        return showAiScoreInPdf;
    }

    public void setShowAiScoreInPdf(boolean showAiScoreInPdf) {
        this.showAiScoreInPdf = showAiScoreInPdf;
    }

    public boolean isShowAiFeedbackInPdf() {
        return showAiFeedbackInPdf;
    }

    public void setShowAiFeedbackInPdf(boolean showAiFeedbackInPdf) {
        this.showAiFeedbackInPdf = showAiFeedbackInPdf;
    }
}