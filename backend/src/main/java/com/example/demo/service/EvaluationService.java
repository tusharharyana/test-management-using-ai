package com.example.demo.service;

import com.example.demo.ai.AiEvaluationResult;
import com.example.demo.ai.AiEvaluationService;
import com.example.demo.dto.response.EvaluationResponse;
import com.example.demo.entity.Evaluation;
import com.example.demo.entity.Submission;
import com.example.demo.enums.SubmissionStatus;
import com.example.demo.repository.EvaluationRepository;
import com.example.demo.repository.SubmissionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvaluationService {

    private final SubmissionRepository submissionRepository;

    private final EvaluationRepository evaluationRepository;

    private final AiEvaluationService aiEvaluationService;


    public EvaluationService(
            SubmissionRepository submissionRepository,
            EvaluationRepository evaluationRepository,
            AiEvaluationService aiEvaluationService
    ) {
        this.submissionRepository = submissionRepository;
        this.evaluationRepository = evaluationRepository;
        this.aiEvaluationService = aiEvaluationService;
    }


    @Transactional
    public EvaluationResponse evaluateSubmission(
            Long submissionId
    ) {

        Submission submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Submission not found with id: "
                                        + submissionId
                        )
                );


        if (evaluationRepository
                .existsBySubmissionId(submissionId)) {

            throw new RuntimeException(
                    "This submission has already been evaluated"
            );
        }


        submission.setStatus(
                SubmissionStatus.EVALUATING
        );

        submissionRepository.save(submission);


        try {

            AiEvaluationResult result =
                    aiEvaluationService.evaluate(submission);


            validateAiResult(result);


            Evaluation evaluation = new Evaluation();

            evaluation.setSubmission(submission);

            evaluation.setTotalScore(result.getScore());

            evaluation.setCorrectnessScore(
                    result.getCorrectnessScore()
            );

            evaluation.setEdgeCaseScore(
                    result.getEdgeCaseScore()
            );

            evaluation.setEfficiencyScore(
                    result.getEfficiencyScore()
            );

            evaluation.setCodeQualityScore(
                    result.getCodeQualityScore()
            );

            evaluation.setSyntaxScore(
                    result.getSyntaxScore()
            );

            evaluation.setConfidence(
                    result.getConfidence()
            );

            evaluation.setFeedback(
                    result.getFeedback()
            );

            evaluation.setAiProvider("GEMINI");
            evaluation.setAiModel("gemini-3.1-flash-lite");


            Evaluation savedEvaluation =
                    evaluationRepository.save(evaluation);


            submission.setStatus(
                    SubmissionStatus.EVALUATED
            );

            submissionRepository.save(submission);


            return mapToResponse(savedEvaluation);

        } catch (Exception exception) {

            submission.setStatus(
                    SubmissionStatus.FAILED
            );

            submissionRepository.save(submission);

            throw exception;
        }
    }


    private void validateAiResult(
            AiEvaluationResult result
    ) {

        if (result == null) {
            throw new RuntimeException(
                    "AI returned an empty evaluation"
            );
        }


        if (result.getCorrectnessScore() == null
                || result.getCorrectnessScore() < 0
                || result.getCorrectnessScore() > 15) {

            throw new RuntimeException(
                    "Invalid correctness score returned by AI"
            );
        }


        if (result.getEdgeCaseScore() == null
                || result.getEdgeCaseScore() < 0
                || result.getEdgeCaseScore() > 6) {

            throw new RuntimeException(
                    "Invalid edge-case score returned by AI"
            );
        }


        if (result.getEfficiencyScore() == null
                || result.getEfficiencyScore() < 0
                || result.getEfficiencyScore() > 4) {

            throw new RuntimeException(
                    "Invalid efficiency score returned by AI"
            );
        }


        if (result.getCodeQualityScore() == null
                || result.getCodeQualityScore() < 0
                || result.getCodeQualityScore() > 3) {

            throw new RuntimeException(
                    "Invalid code-quality score returned by AI"
            );
        }


        if (result.getSyntaxScore() == null
                || result.getSyntaxScore() < 0
                || result.getSyntaxScore() > 2) {

            throw new RuntimeException(
                    "Invalid syntax score returned by AI"
            );
        }


        int calculatedTotal =
                result.getCorrectnessScore()
                        + result.getEdgeCaseScore()
                        + result.getEfficiencyScore()
                        + result.getCodeQualityScore()
                        + result.getSyntaxScore();


        // Do not blindly trust AI's total.
        // Our backend calculates the final total itself.

        result.setScore(calculatedTotal);


        if (result.getConfidence() == null
                || result.getConfidence() < 0
                || result.getConfidence() > 100) {

            throw new RuntimeException(
                    "Invalid confidence returned by AI"
            );
        }
    }


    private EvaluationResponse mapToResponse(
            Evaluation evaluation
    ) {

        return new EvaluationResponse(

                evaluation.getId(),

                evaluation.getSubmission().getId(),

                evaluation.getTotalScore(),

                evaluation.getCorrectnessScore(),

                evaluation.getEdgeCaseScore(),

                evaluation.getEfficiencyScore(),

                evaluation.getCodeQualityScore(),

                evaluation.getSyntaxScore(),

                evaluation.getConfidence(),

                evaluation.getFeedback(),

                evaluation.getEvaluatedAt()
        );
    }
}