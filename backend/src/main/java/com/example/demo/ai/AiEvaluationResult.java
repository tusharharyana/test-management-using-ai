package com.example.demo.ai;

public class AiEvaluationResult {

    private Integer score;

    private Integer correctnessScore;

    private Integer edgeCaseScore;

    private Integer efficiencyScore;

    private Integer codeQualityScore;

    private Integer syntaxScore;

    private Integer confidence;

    private String feedback;


    public AiEvaluationResult() {
    }


    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
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
}