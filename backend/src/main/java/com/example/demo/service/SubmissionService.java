package com.example.demo.service;

import com.example.demo.dto.request.CreateSubmissionRequest;
import com.example.demo.dto.response.SubmissionResponse;
import com.example.demo.entity.Question;
import com.example.demo.entity.Submission;
import com.example.demo.entity.TestAttempt;
import com.example.demo.enums.AttemptStatus;
import com.example.demo.enums.SubmissionStatus;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.SubmissionRepository;
import com.example.demo.repository.TestAttemptRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.messaging.producer.SubmissionEventPublisher;
import com.example.demo.dto.response.EvaluationSummaryResponse;
import com.example.demo.dto.response.SubmissionResultResponse;
import com.example.demo.entity.Evaluation;
import com.example.demo.repository.EvaluationRepository;

import java.time.LocalDateTime;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    private final TestAttemptRepository testAttemptRepository;

    private final QuestionRepository questionRepository;

    private final SubmissionEventPublisher submissionEventPublisher;
    private final EvaluationRepository evaluationRepository;


    public SubmissionService(
        SubmissionRepository submissionRepository,
        TestAttemptRepository testAttemptRepository,
        QuestionRepository questionRepository,
        SubmissionEventPublisher submissionEventPublisher,
        EvaluationRepository evaluationRepository
) {
    this.submissionRepository = submissionRepository;
    this.testAttemptRepository = testAttemptRepository;
    this.questionRepository = questionRepository;
    this.submissionEventPublisher = submissionEventPublisher;
    this.evaluationRepository = evaluationRepository;
}


    @Transactional
    public SubmissionResponse createSubmission(
            CreateSubmissionRequest request
    ) {

        // 1. Find the student's test attempt
        TestAttempt attempt = testAttemptRepository
                .findById(request.getAttemptId())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Test attempt not found with id: "
                                        + request.getAttemptId()
                        )
                );


        // 2. Check whether attempt is still in progress
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {

            throw new RuntimeException(
                    "This test attempt is no longer active"
            );
        }


        // 3. Check whether test time has expired
        if (LocalDateTime.now().isAfter(attempt.getExpiresAt()) && !request.isAutoSubmit()) {

            attempt.setStatus(AttemptStatus.EXPIRED);

            testAttemptRepository.save(attempt);

            throw new RuntimeException(
                    "Test time has expired"
            );
        }


        // 4. Find question
        Question question = questionRepository
                .findById(request.getQuestionId())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Question not found with id: "
                                        + request.getQuestionId()
                        )
                );


        // 5. Verify question actually belongs to this test
        if (!question.getTest().getId()
                .equals(attempt.getTest().getId())) {

            throw new RuntimeException(
                    "This question does not belong to the current test"
            );
        }


        // 6. Prevent duplicate final submission
        boolean alreadySubmitted =
                submissionRepository
                        .existsByTestAttemptIdAndQuestionId(
                                attempt.getId(),
                                question.getId()
                        );

        if (alreadySubmitted) {

            throw new RuntimeException(
                    "You have already submitted this question"
            );
        }


        // 7. Basic validation
        if (request.getSourceCode() == null
                || request.getSourceCode().isBlank()) {

            throw new RuntimeException(
                    "Source code cannot be empty"
            );
        }


        if (request.getLanguage() == null) {

            throw new RuntimeException(
                    "Programming language is required"
            );
        }


        // 8. Create submission
        Submission submission = new Submission();

        submission.setTestAttempt(attempt);

        submission.setQuestion(question);

        submission.setLanguage(request.getLanguage());

        submission.setSourceCode(request.getSourceCode());

        submission.setStatus(SubmissionStatus.PENDING);

        submission.setSubmittedAt(LocalDateTime.now());


        // 9. Save submission
        Submission savedSubmission =
                submissionRepository.save(submission);

        submissionEventPublisher.publish(
        savedSubmission.getId()
        );

        // 10. Return response
        return mapToResponse(savedSubmission);
    }


    private SubmissionResponse mapToResponse(
            Submission submission
    ) {

        return new SubmissionResponse(

                submission.getId(),

                submission.getTestAttempt().getId(),

                submission.getQuestion().getId(),

                submission.getLanguage(),

                submission.getStatus(),

                submission.getSubmittedAt()
        );
    }

    @Transactional(readOnly = true)
public SubmissionResultResponse getSubmissionResult(
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


    EvaluationSummaryResponse evaluationResponse = null;


    Evaluation evaluation = evaluationRepository
            .findBySubmissionId(submissionId)
            .orElse(null);


    if (evaluation != null) {

        Integer finalScore =
                evaluation.getTeacherScore() != null
                        ? evaluation.getTeacherScore()
                        : evaluation.getTotalScore();


        evaluationResponse = new EvaluationSummaryResponse(

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

        submission.getSourceCode(),

        evaluationResponse
);
}
}