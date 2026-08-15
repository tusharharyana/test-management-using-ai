package com.example.demo.service;

import com.example.demo.dto.request.CreateQuestionRequest;
import com.example.demo.dto.request.CreateTestRequest;
import com.example.demo.dto.response.QuestionResponse;
import com.example.demo.dto.response.TestResponse;
import com.example.demo.entity.Question;
import com.example.demo.entity.Test;
import com.example.demo.enums.TestStatus;
import com.example.demo.repository.TestRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.dto.request.UpdateTestRequest;

@Service
public class TestService {

    private final TestRepository testRepository;

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int ACCESS_CODE_LENGTH = 6;

    private final SecureRandom secureRandom = new SecureRandom();


    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }


    // =========================
    // Create Test
    // =========================

    @Transactional
    public TestResponse createTest(CreateTestRequest request) {

        Test test = new Test();

        test.setTitle(request.getTitle());
        test.setDescription(request.getDescription());
        test.setDurationMinutes(request.getDurationMinutes());

        test.setAccessCode(generateUniqueAccessCode());

        test.setStatus(TestStatus.DRAFT);


        if (request.getQuestions() != null) {

            for (CreateQuestionRequest questionRequest
                    : request.getQuestions()) {

                Question question = new Question();

                question.setTitle(questionRequest.getTitle());

                question.setProblemStatement(
                        questionRequest.getProblemStatement()
                );

                question.setExamples(
                        questionRequest.getExamples()
                );

                question.setMaxMarks(
                        questionRequest.getMaxMarks()
                );

                question.setQuestionOrder(
                        questionRequest.getQuestionOrder()
                );

                test.addQuestion(question);
            }
        }


        Test savedTest = testRepository.save(test);

        return mapToResponse(savedTest);
    }


    // =========================
    // Get Test By ID
    // =========================

    @Transactional(readOnly = true)
    public TestResponse getTestById(Long id) {

        Test test = testRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Test not found with id: " + id
                        )
                );

        return mapToResponse(test);
    }


    // =========================
    // Get All Tests
    // =========================

    @Transactional(readOnly = true)
    public List<TestResponse> getAllTests() {

        return testRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================
    // Get Test By Access Code
    // =========================

    @Transactional(readOnly = true)
    public TestResponse getTestByAccessCode(String accessCode) {

        Test test = testRepository
                .findByAccessCode(accessCode)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Invalid test access code"
                        )
                );

        return mapToResponse(test);
    }


    // =========================
    // Generate Access Code
    // =========================

    private String generateUniqueAccessCode() {

        String accessCode;

        do {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < ACCESS_CODE_LENGTH; i++) {

                int randomIndex =
                        secureRandom.nextInt(CHARACTERS.length());

                builder.append(
                        CHARACTERS.charAt(randomIndex)
                );
            }

            accessCode = builder.toString();

        } while (testRepository.existsByAccessCode(accessCode));

        return accessCode;
    }


    // =========================
    // Entity → Response DTO
    // =========================

    private TestResponse mapToResponse(Test test) {

        List<QuestionResponse> questionResponses =
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


        return new TestResponse(

                test.getId(),

                test.getTitle(),

                test.getDescription(),

                test.getAccessCode(),

                test.getDurationMinutes(),

                test.getStatus(),

                test.getCreatedAt(),

                questionResponses
        );
    }
    @Transactional
public TestResponse activateTest(Long testId) {

    Test test = testRepository
            .findById(testId)
            .orElseThrow(
                    () -> new ResourceNotFoundException(
                            "Test not found with id: "
                                    + testId
                    )
            );


    if (test.getStatus() == TestStatus.COMPLETED) {

        throw new BadRequestException(
                "A completed test cannot be activated"
        );
    }


    test.setStatus(TestStatus.ACTIVE);


    Test savedTest =
            testRepository.save(test);


    return mapToResponse(savedTest);
}

@Transactional
public TestResponse completeTest(Long testId) {

    Test test = testRepository
            .findById(testId)
            .orElseThrow(
                    () -> new ResourceNotFoundException(
                            "Test not found with id: "
                                    + testId
                    )
            );


    if (test.getStatus() == TestStatus.COMPLETED) {

        throw new BadRequestException(
                "Test is already completed"
        );
    }


    test.setStatus(TestStatus.COMPLETED);


    Test savedTest =
            testRepository.save(test);


    return mapToResponse(savedTest);
}
@Transactional
public TestResponse updateTestTitle(
        Long testId,
        UpdateTestRequest request
) {

    Test test = testRepository
            .findById(testId)
            .orElseThrow(
                    () -> new ResourceNotFoundException(
                            "Test not found with id: " + testId
                    )
            );

    if (request.getTitle() == null ||
            request.getTitle().trim().isEmpty()) {

        throw new BadRequestException(
                "Test title cannot be empty"
        );
    }

    test.setTitle(request.getTitle().trim());

    Test savedTest = testRepository.save(test);

    return mapToResponse(savedTest);
}
}