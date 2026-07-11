package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "submission_id",
            nullable = false,
            unique = true
    )
    private Submission submission;


    @Column(name = "total_score", nullable = false)
    private Integer totalScore;


    @Column(name = "correctness_score", nullable = false)
    private Integer correctnessScore;


    @Column(name = "edge_case_score", nullable = false)
    private Integer edgeCaseScore;


    @Column(name = "efficiency_score", nullable = false)
    private Integer efficiencyScore;


    @Column(name = "code_quality_score", nullable = false)
    private Integer codeQualityScore;


    @Column(name = "syntax_score", nullable = false)
    private Integer syntaxScore;


    @Column(nullable = false)
    private Integer confidence;


    @Column(columnDefinition = "TEXT")
    private String feedback;


    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;

    @Column(name = "ai_provider", nullable = false)
    private String aiProvider;

    @Column(name = "ai_model", nullable = false)
    private String aiModel;

    @Column(name = "teacher_score")
    private Integer teacherScore;

    @Column(name = "teacher_comment", columnDefinition = "TEXT")
    private String teacherComment;


    public Evaluation() {
    }


    @PrePersist
    protected void onCreate() {

        if (this.evaluatedAt == null) {
            this.evaluatedAt = LocalDateTime.now();
        }
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Submission getSubmission() {
        return submission;
    }


    public void setSubmission(Submission submission) {
        this.submission = submission;
    }


    public Integer getTotalScore() {
        return totalScore;
    }


    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }


    public Integer getCorrectnessScore() {
        return correctnessScore;
    }


    public void setCorrectnessScore(Integer correctnessScore) {
        this.correctnessScore = correctnessScore;
    }


    public Integer getEdgeCaseScore() {
        return edgeCaseScore;
    }


    public void setEdgeCaseScore(Integer edgeCaseScore) {
        this.edgeCaseScore = edgeCaseScore;
    }


    public Integer getEfficiencyScore() {
        return efficiencyScore;
    }


    public void setEfficiencyScore(Integer efficiencyScore) {
        this.efficiencyScore = efficiencyScore;
    }


    public Integer getCodeQualityScore() {
        return codeQualityScore;
    }


    public void setCodeQualityScore(Integer codeQualityScore) {
        this.codeQualityScore = codeQualityScore;
    }


    public Integer getSyntaxScore() {
        return syntaxScore;
    }


    public void setSyntaxScore(Integer syntaxScore) {
        this.syntaxScore = syntaxScore;
    }


    public Integer getConfidence() {
        return confidence;
    }


    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }


    public String getFeedback() {
        return feedback;
    }


    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }


    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }


    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public String getAiProvider() {
    return aiProvider;
    }

    public void setAiProvider(String aiProvider) {
        this.aiProvider = aiProvider;
    }

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }

    public Integer getTeacherScore() {
    return teacherScore;
    }

    public void setTeacherScore(Integer teacherScore) {
        this.teacherScore = teacherScore;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }
}