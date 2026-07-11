package com.example.demo.service;

import com.example.demo.dto.request.StartTestRequest;
import com.example.demo.dto.response.QuestionResponse;
import com.example.demo.dto.response.TestAttemptResponse;
import com.example.demo.entity.Test;
import com.example.demo.entity.TestAttempt;
import com.example.demo.enums.AttemptStatus;
import com.example.demo.enums.TestStatus;
import com.example.demo.repository.TestAttemptRepository;
import com.example.demo.repository.TestRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.BadRequestException;

@Service
public class TestAttemptService {

    private final TestRepository testRepository;

    private final TestAttemptRepository testAttemptRepository;


    public TestAttemptService(
            TestRepository testRepository,
            TestAttemptRepository testAttemptRepository
    ) {
        this.testRepository = testRepository;
        this.testAttemptRepository = testAttemptRepository;
    }


    @Transactional
    public TestAttemptResponse startTest(StartTestRequest request) {

        // 1. Find test using access code
        Test test = testRepository
                .findByAccessCode(request.getAccessCode())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Invalid test access code"
                        )
                );


        // 2. Check whether test is active
        if (test.getStatus() != TestStatus.ACTIVE) {

            throw new RuntimeException(
                    "This test is not currently active"
            );
        }


        // 3. Check if this student has already attempted the test
        boolean alreadyAttempted =
                testAttemptRepository
                        .existsByTestIdAndStudentUid(
                                test.getId(),
                                request.getStudentUid()
                        );

        if (alreadyAttempted) {

            throw new RuntimeException(
                    "You have already started or attempted this test"
            );
        }


        // 4. Create attempt
        LocalDateTime startedAt = LocalDateTime.now();

        LocalDateTime expiresAt =
                startedAt.plusMinutes(test.getDurationMinutes());


        TestAttempt attempt = new TestAttempt();

        attempt.setStudentName(request.getStudentName());

        attempt.setStudentUid(request.getStudentUid());

        attempt.setTest(test);

        attempt.setStatus(AttemptStatus.IN_PROGRESS);

        attempt.setStartedAt(startedAt);

        attempt.setExpiresAt(expiresAt);


        // 5. Save attempt
        TestAttempt savedAttempt =
                testAttemptRepository.save(attempt);


        // 6. Convert questions to response DTO
        List<QuestionResponse> questions =
                test.getQuestions()
                        .stream()
                        .map(question -> new QuestionResponse(

                                question.getId(),

                                question.getTitle(),

                                question.getProblemStatement(),

                                question.getExamples(),

                                question.getMaxMarks(),

                                question.getQuestionOrder()
                        ))
                        .toList();


        // 7. Return response
        return new TestAttemptResponse(

                savedAttempt.getId(),

                savedAttempt.getStudentName(),

                savedAttempt.getStudentUid(),

                test.getId(),

                test.getTitle(),

                test.getDurationMinutes(),

                savedAttempt.getStatus(),

                savedAttempt.getStartedAt(),

                savedAttempt.getExpiresAt(),

                questions
        );
    }

    @Transactional
public TestAttemptResponse submitTest(
        Long attemptId
) {

    TestAttempt attempt = testAttemptRepository
            .findById(attemptId)
            .orElseThrow(
                    () -> new ResourceNotFoundException(
                            "Test attempt not found with id: "
                                    + attemptId
                    )
            );


    if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {

        throw new BadRequestException(
                "This test attempt is no longer in progress"
        );
    }


    LocalDateTime now = LocalDateTime.now();


    if (now.isAfter(attempt.getExpiresAt())) {

        attempt.setStatus(AttemptStatus.EXPIRED);

        testAttemptRepository.save(attempt);

        throw new BadRequestException(
                "Test time has expired"
        );
    }


    attempt.setStatus(AttemptStatus.SUBMITTED);

    attempt.setSubmittedAt(now);


    TestAttempt savedAttempt =
            testAttemptRepository.save(attempt);


    return mapToResponse(savedAttempt);
}
private TestAttemptResponse mapToResponse(
        TestAttempt attempt
) {

    Test test = attempt.getTest();


    List<QuestionResponse> questions =
            test.getQuestions()
                    .stream()
                    .map(question -> new QuestionResponse(

                            question.getId(),

                            question.getTitle(),

                            question.getProblemStatement(),

                            question.getExamples(),

                            question.getMaxMarks(),

                            question.getQuestionOrder()
                    ))
                    .toList();


    return new TestAttemptResponse(

            attempt.getId(),

            attempt.getStudentName(),

            attempt.getStudentUid(),

            test.getId(),

            test.getTitle(),

            test.getDurationMinutes(),

            attempt.getStatus(),

            attempt.getStartedAt(),

            attempt.getExpiresAt(),

            questions
    );
}
}