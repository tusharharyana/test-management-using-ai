package com.example.demo.service;

import com.example.demo.dto.response.EvaluationSummaryResponse;
import com.example.demo.dto.response.StudentTestResultResponse;
import com.example.demo.dto.response.SubmissionResultResponse;
import com.example.demo.entity.Evaluation;
import com.example.demo.entity.Submission;
import com.example.demo.entity.Test;
import com.example.demo.entity.TestAttempt;
import com.example.demo.repository.EvaluationRepository;
import com.example.demo.repository.SubmissionRepository;
import com.example.demo.repository.TestAttemptRepository;
import com.example.demo.repository.TestRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.request.OverrideMarksRequest;

import java.util.List;

@Service
public class AdminResultService {

    private final TestRepository testRepository;

    private final TestAttemptRepository testAttemptRepository;

    private final SubmissionRepository submissionRepository;

    private final EvaluationRepository evaluationRepository;


    public AdminResultService(
            TestRepository testRepository,
            TestAttemptRepository testAttemptRepository,
            SubmissionRepository submissionRepository,
            EvaluationRepository evaluationRepository
    ) {
        this.testRepository = testRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.submissionRepository = submissionRepository;
        this.evaluationRepository = evaluationRepository;
    }


    @Transactional(readOnly = true)
    public List<StudentTestResultResponse> getTestResults(
            Long testId
    ) {

        Test test = testRepository
                .findById(testId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Test not found with id: "
                                        + testId
                        )
                );


        int maximumPossibleScore =
                test.getQuestions()
                        .stream()
                        .mapToInt(question -> question.getMaxMarks())
                        .sum();


        List<TestAttempt> attempts =
                testAttemptRepository.findAllByTestId(testId);


        return attempts
                .stream()
                .map(attempt -> mapAttemptToResponse(
                        attempt,
                        maximumPossibleScore
                ))
                .toList();
    }


    @Transactional(readOnly = true)
    public StudentTestResultResponse getStudentResult(
            Long testId,
            Long attemptId
    ) {

        Test test = testRepository
                .findById(testId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Test not found with id: "
                                        + testId
                        )
                );


        TestAttempt attempt = testAttemptRepository
                .findById(attemptId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Attempt not found with id: "
                                        + attemptId
                        )
                );


        if (!attempt.getTest().getId().equals(testId)) {

            throw new RuntimeException(
                    "This attempt does not belong to this test"
            );
        }


        int maximumPossibleScore =
                test.getQuestions()
                        .stream()
                        .mapToInt(question -> question.getMaxMarks())
                        .sum();


        return mapAttemptToResponse(
                attempt,
                maximumPossibleScore
        );
    }


    private StudentTestResultResponse mapAttemptToResponse(
            TestAttempt attempt,
            int maximumPossibleScore
    ) {

        List<Submission> submissions =
                submissionRepository.findAllByTestAttemptId(
                        attempt.getId()
                );


        List<SubmissionResultResponse> submissionResponses =
                submissions
                        .stream()
                        .map(this::mapSubmissionToResponse)
                        .toList();


        int aiTotalScore = submissionResponses
                .stream()
                .filter(response ->
                        response.getEvaluation() != null
                )
                .mapToInt(response ->
                        response.getEvaluation().getAiScore()
                )
                .sum();


        int finalTotalScore = submissionResponses
                .stream()
                .filter(response ->
                        response.getEvaluation() != null
                )
                .mapToInt(response ->
                        response.getEvaluation().getFinalScore()
                )
                .sum();


        return new StudentTestResultResponse(

                attempt.getId(),

                attempt.getStudentName(),

                attempt.getStudentUid(),

                attempt.getStatus(),

                attempt.getStartedAt(),

                attempt.getSubmittedAt(),

                aiTotalScore,

                finalTotalScore,

                maximumPossibleScore,

                submissionResponses
        );
    }


    private SubmissionResultResponse mapSubmissionToResponse(
            Submission submission
    ) {

        Evaluation evaluation = evaluationRepository
                .findBySubmissionId(submission.getId())
                .orElse(null);


        EvaluationSummaryResponse evaluationResponse = null;


        if (evaluation != null) {

            int finalScore =
                    evaluation.getTeacherScore() != null
                            ? evaluation.getTeacherScore()
                            : evaluation.getTotalScore();


            evaluationResponse =
                    new EvaluationSummaryResponse(

                            evaluation.getId(),

                            evaluation.getTotalScore(),

                            evaluation.getTeacherScore(),

                            finalScore,

                            evaluation.getCorrectnessScore(),

                            evaluation.getEdgeCaseScore(),

                            evaluation.getEfficiencyScore(),

                            evaluation.getCodeQualityScore(),

                            evaluation.getSyntaxScore(),

                            evaluation.getConfidence(),

                            evaluation.getFeedback(),

                            evaluation.getTeacherComment()
                    );
        }


        return new SubmissionResultResponse(

                submission.getId(),

                submission.getTestAttempt().getId(),

                submission.getQuestion().getId(),

                submission.getQuestion().getTitle(),

                submission.getLanguage(),

                submission.getStatus(),

                submission.getSubmittedAt(),

                evaluationResponse
        );
    }

    @Transactional
public EvaluationSummaryResponse overrideMarks(
        Long evaluationId,
        OverrideMarksRequest request
) {

    Evaluation evaluation = evaluationRepository
            .findById(evaluationId)
            .orElseThrow(
                    () -> new RuntimeException(
                            "Evaluation not found with id: "
                                    + evaluationId
                    )
            );


    int maximumMarks =
            evaluation
                    .getSubmission()
                    .getQuestion()
                    .getMaxMarks();


    if (request.getTeacherScore() == null
            || request.getTeacherScore() < 0
            || request.getTeacherScore() > maximumMarks) {

        throw new RuntimeException(
                "Teacher score must be between 0 and "
                        + maximumMarks
        );
    }


    evaluation.setTeacherScore(
            request.getTeacherScore()
    );

    evaluation.setTeacherComment(
            request.getTeacherComment()
    );


    Evaluation savedEvaluation =
            evaluationRepository.save(evaluation);


    return new EvaluationSummaryResponse(

            savedEvaluation.getId(),

            savedEvaluation.getTotalScore(),

            savedEvaluation.getTeacherScore(),

            savedEvaluation.getTeacherScore(),

            savedEvaluation.getCorrectnessScore(),

            savedEvaluation.getEdgeCaseScore(),

            savedEvaluation.getEfficiencyScore(),

            savedEvaluation.getCodeQualityScore(),

            savedEvaluation.getSyntaxScore(),

            savedEvaluation.getConfidence(),

            savedEvaluation.getFeedback(),

            savedEvaluation.getTeacherComment()
    );
}
}