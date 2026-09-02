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
import com.example.demo.dto.response.EvaluationStatusResponse;
import java.util.List;
import com.example.demo.messaging.producer.EvaluationProducer;

@Service
public class EvaluationService {

    private final SubmissionRepository submissionRepository;

    private final EvaluationRepository evaluationRepository;

    private final AiEvaluationService aiEvaluationService;
    private final EvaluationProducer evaluationProducer;


    public EvaluationService(
            SubmissionRepository submissionRepository,
            EvaluationRepository evaluationRepository,
            AiEvaluationService aiEvaluationService,
            EvaluationProducer evaluationProducer
    ) {
        this.submissionRepository = submissionRepository;
        this.evaluationRepository = evaluationRepository;
        this.aiEvaluationService = aiEvaluationService;
        this.evaluationProducer = evaluationProducer;
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


        submission.setStatus(
                SubmissionStatus.EVALUATING
        );

        submissionRepository.save(submission);


        try {

            AiEvaluationResult result =
                    aiEvaluationService.evaluate(submission);


            validateAiResult(result);


            Evaluation evaluation =
                        evaluationRepository
                                .findBySubmissionId(submissionId)
                                .orElseGet(() -> {

                                Evaluation newEvaluation =
                                        new Evaluation();

                                newEvaluation.setSubmission(
                                        submission
                                );

                                return newEvaluation;
                                });


                evaluation.setTotalScore(
                        result.getScore()
                );

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

                evaluation.setAiModel(
                        "gemini-3.1-flash-lite"
                );


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
            || result.getCorrectnessScore() < 0) {

        throw new RuntimeException(
                "Invalid correctness score returned by AI"
        );
    }


    if (result.getEdgeCaseScore() == null
            || result.getEdgeCaseScore() < 0) {

        throw new RuntimeException(
                "Invalid edge-case score returned by AI"
        );
    }


    if (result.getEfficiencyScore() == null
            || result.getEfficiencyScore() < 0) {

        throw new RuntimeException(
                "Invalid efficiency score returned by AI"
        );
    }


    if (result.getCodeQualityScore() == null
            || result.getCodeQualityScore() < 0) {

        throw new RuntimeException(
                "Invalid code-quality score returned by AI"
        );
    }


    if (result.getSyntaxScore() == null
            || result.getSyntaxScore() < 0) {

        throw new RuntimeException(
                "Invalid syntax score returned by AI"
        );
    }

    result.setCorrectnessScore(
            Math.min(result.getCorrectnessScore(), 15)
    );

    result.setEdgeCaseScore(
            Math.min(result.getEdgeCaseScore(), 6)
    );

    result.setEfficiencyScore(
            Math.min(result.getEfficiencyScore(), 4)
    );

    result.setCodeQualityScore(
            Math.min(result.getCodeQualityScore(), 3)
    );

    result.setSyntaxScore(
            Math.min(result.getSyntaxScore(), 2)
    );

    int calculatedTotal =
            result.getCorrectnessScore()
                    + result.getEdgeCaseScore()
                    + result.getEfficiencyScore()
                    + result.getCodeQualityScore()
                    + result.getSyntaxScore();


    // Do not blindly trust AI's total.
    // Backend calculates the final total.

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
        @Transactional(readOnly = true)
        public EvaluationStatusResponse getAttemptEvaluationStatus(
                Long attemptId
        ) {

        List<Submission> submissions =
                submissionRepository.findAllByTestAttemptId(attemptId);

        if (submissions.isEmpty()) {
        return new EvaluationStatusResponse(
                "COMPLETED",
                0,
                0,
                0
        );
        }

        int totalSubmissions = submissions.size();
        int evaluatedSubmissions = 0;
        int failedSubmissions = 0;

        for (Submission submission : submissions) {

                if (submission.getStatus() ==
                        SubmissionStatus.EVALUATED) {

                evaluatedSubmissions++;

                } else if (submission.getStatus() ==
                        SubmissionStatus.FAILED) {

                failedSubmissions++;
                }
        }

        String status;

        if (evaluatedSubmissions == totalSubmissions) {

                status = "COMPLETED";

        } else if (failedSubmissions > 0) {

                status = "FAILED";

        } else {

                status = "IN_PROGRESS";
        }

        return new EvaluationStatusResponse(
                status,
                totalSubmissions,
                evaluatedSubmissions,
                failedSubmissions
        );
        }

        @Transactional
        public void reEvaluateSubmission(Long submissionId) {

        Submission submission =
                submissionRepository
                        .findById(submissionId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Submission not found with id: "
                                                + submissionId
                                )
                        );
                        
        submission.setStatus(SubmissionStatus.PENDING);
        submissionRepository.save(submission);

        evaluationProducer.sendSubmissionForEvaluation(
                submissionId
        );
        }

        @Transactional
        public void reEvaluateAttempt(Long attemptId) {

        List<Submission> submissions =
                submissionRepository
                        .findAllByTestAttemptId(attemptId);

        if (submissions.isEmpty()) {
                throw new RuntimeException(
                        "No submissions found for this attempt"
                );
        }

        for (Submission submission : submissions) {

                submission.setStatus(SubmissionStatus.PENDING);
                submissionRepository.save(submission);

                evaluationProducer.sendSubmissionForEvaluation(
                        submission.getId()
                );
        }
        }

        @Transactional
        public void reEvaluateTest(Long testId) {

        List<Submission> submissions =
                submissionRepository.findAllByTestAttempt_Test_Id(testId);

        if (submissions.isEmpty()) {
                throw new RuntimeException(
                        "No submissions found for this test"
                );
        }

        for (Submission submission : submissions) {

                submission.setStatus(SubmissionStatus.PENDING);
                submissionRepository.save(submission);

                evaluationProducer.sendSubmissionForEvaluation(
                        submission.getId()
                );
        }
        }
}