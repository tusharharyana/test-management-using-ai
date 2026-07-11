package com.example.demo.dto.response;

public class EvaluationSummaryResponse {

    private Long evaluationId;

    private Integer aiScore;

    private Integer teacherScore;

    private Integer finalScore;

    private Integer correctnessScore;

    private Integer edgeCaseScore;

    private Integer efficiencyScore;

    private Integer codeQualityScore;

    private Integer syntaxScore;

    private Integer confidence;

    private String feedback;

    private String teacherComment;


    public EvaluationSummaryResponse() {
    }


    public EvaluationSummaryResponse(
            Long evaluationId,
            Integer aiScore,
            Integer teacherScore,
            Integer finalScore,
            Integer correctnessScore,
            Integer edgeCaseScore,
            Integer efficiencyScore,
            Integer codeQualityScore,
            Integer syntaxScore,
            Integer confidence,
            String feedback,
            String teacherComment
    ) {
        this.evaluationId = evaluationId;
        this.aiScore = aiScore;
        this.teacherScore = teacherScore;
        this.finalScore = finalScore;
        this.correctnessScore = correctnessScore;
        this.edgeCaseScore = edgeCaseScore;
        this.efficiencyScore = efficiencyScore;
        this.codeQualityScore = codeQualityScore;
        this.syntaxScore = syntaxScore;
        this.confidence = confidence;
        this.feedback = feedback;
        this.teacherComment = teacherComment;
    }


    public Long getEvaluationId() {
        return evaluationId;
    }

    public Integer getAiScore() {
        return aiScore;
    }

    public Integer getTeacherScore() {
        return teacherScore;
    }

    public Integer getFinalScore() {
        return finalScore;
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

    public String getTeacherComment() {
        return teacherComment;
    }
}