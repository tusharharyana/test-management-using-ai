package com.example.demo.entity;

import com.example.demo.enums.ProgrammingLanguage;
import com.example.demo.enums.SubmissionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "submissions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attempt_question",
                        columnNames = {"attempt_id", "question_id"}
                )
        }
)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private TestAttempt testAttempt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProgrammingLanguage language;


    @Lob
    @Column(name = "source_code", nullable = false, columnDefinition = "LONGTEXT")
    private String sourceCode;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubmissionStatus status;


    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;


    public Submission() {
    }


    @PrePersist
    protected void onCreate() {

        if (this.submittedAt == null) {
            this.submittedAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = SubmissionStatus.PENDING;
        }
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public TestAttempt getTestAttempt() {
        return testAttempt;
    }


    public void setTestAttempt(TestAttempt testAttempt) {
        this.testAttempt = testAttempt;
    }


    public Question getQuestion() {
        return question;
    }


    public void setQuestion(Question question) {
        this.question = question;
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


    public SubmissionStatus getStatus() {
        return status;
    }


    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }


    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }


    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}