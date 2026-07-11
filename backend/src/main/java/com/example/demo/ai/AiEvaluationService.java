package com.example.demo.ai;

import com.example.demo.entity.Submission;

public interface AiEvaluationService {

    AiEvaluationResult evaluate(Submission submission);
}