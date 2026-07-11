package com.example.demo.dto.response;

import java.time.LocalDateTime;

public class EvaluationResponse {

    private Long evaluationId;

    private Long submissionId;

    private Integer totalScore;

    private Integer correctnessScore;

    private Integer edgeCaseScore;

    private Integer efficiencyScore;

    private Integer codeQualityScore;

    private Integer syntaxScore;

    private Integer confidence;

    private String feedback;

    private LocalDateTime evaluatedAt;


    public EvaluationResponse() {
    }


    public EvaluationResponse(
            Long evaluationId,
            Long submissionId,
            Integer totalScore,
            Integer correctnessScore,
            Integer edgeCaseScore,
            Integer efficiencyScore,
            Integer codeQualityScore,
            Integer syntaxScore,
            Integer confidence,
            String feedback,
            LocalDateTime evaluatedAt
    ) {
        this.evaluationId = evaluationId;
        this.submissionId = submissionId;
        this.totalScore = totalScore;
        this.correctnessScore = correctnessScore;
        this.edgeCaseScore = edgeCaseScore;
        this.efficiencyScore = efficiencyScore;
        this.codeQualityScore = codeQualityScore;
        this.syntaxScore = syntaxScore;
        this.confidence = confidence;
        this.feedback = feedback;
        this.evaluatedAt = evaluatedAt;
    }


    public Long getEvaluationId() {
        return evaluationId;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public Integer getCorrectnessScore() {
        return correctnessScore;
    }

    public Integer getEdgeCaseScore() {
        return edgeCaseScore;
    }

    public Integer getEfficiencyScore() {
        return efficiencyScore;
    }

    public Integer getCodeQualityScore() {
        return codeQualityScore;
    }

    public Integer getSyntaxScore() {
        return syntaxScore;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public String getFeedback() {
        return feedback;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }
}