package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "system_settings")
public class SystemSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ai_evaluation_enabled", nullable = false)
    private Boolean aiEvaluationEnabled = false;

    public SystemSetting() {
    }

    public Long getId() {
        return id;
    }

    public Boolean getAiEvaluationEnabled() {
        return aiEvaluationEnabled;
    }

    public void setAiEvaluationEnabled(Boolean aiEvaluationEnabled) {
        this.aiEvaluationEnabled = aiEvaluationEnabled;
    }
}