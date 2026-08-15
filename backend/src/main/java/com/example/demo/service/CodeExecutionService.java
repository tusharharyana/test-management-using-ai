package com.example.demo.service;

import com.example.demo.dto.request.RunCodeRequest;
import com.example.demo.dto.response.RunCodeResponse;
import com.example.demo.dto.response.TestCaseResultResponse;
import com.example.demo.entity.Question;
import com.example.demo.entity.TestCase;
import com.example.demo.repository.QuestionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeExecutionService {

    private final QuestionRepository questionRepository;
    private final Judge0Service judge0Service;
    private final Judge0LanguageService languageService;

    public CodeExecutionService(
            QuestionRepository questionRepository,
            Judge0Service judge0Service,
            Judge0LanguageService languageService
    ) {
        this.questionRepository = questionRepository;
        this.judge0Service = judge0Service;
        this.languageService = languageService;
    }

    @Transactional(readOnly = true)
    public RunCodeResponse runCode(
            RunCodeRequest request
    ) throws Exception {

        // =========================================
        // 1. Validate request
        // =========================================

        if (request.getQuestionId() == null) {
            throw new RuntimeException(
                    "Question ID is required"
            );
        }

        if (request.getLanguage() == null) {
            throw new RuntimeException(
                    "Programming language is required"
            );
        }

        if (request.getSourceCode() == null ||
                request.getSourceCode().trim().isEmpty()) {

            throw new RuntimeException(
                    "Source code cannot be empty"
            );
        }


        // =========================================
        // 2. Find question
        // =========================================

        Question question =
                questionRepository.findById(
                        request.getQuestionId()
                ).orElseThrow(
                        () -> new RuntimeException(
                                "Question not found with id: "
                                        + request.getQuestionId()
                        )
                );


        // =========================================
        // 3. Get test cases
        // =========================================

        List<TestCase> testCases =
                question.getTestCases();

        if (testCases == null ||
                testCases.isEmpty()) {

            throw new RuntimeException(
                    "No test cases configured for this question"
            );
        }


        // =========================================
        // 4. Judge0 language ID
        // =========================================

        int languageId =
                languageService.getLanguageId(
                        request.getLanguage()
                );


        List<TestCaseResultResponse> results =
                new ArrayList<>();

        int passed = 0;
        int failed = 0;


        // =========================================
        // 5. Execute test cases
        // =========================================

        for (TestCase testCase : testCases) {

            JsonNode result =
                    judge0Service.executeCode(
                            request.getSourceCode(),
                            testCase.getInput(),
                            languageId
                    );


            // =====================================
            // 6. Read Judge0 status
            // =====================================

            JsonNode statusNode =
                    result.get("status");

            if (statusNode == null ||
                    statusNode.get("id") == null) {

                return new RunCodeResponse(
                        "EXECUTION_ERROR",
                        testCases.size(),
                        passed,
                        failed,
                        null,
                        null,
                        "Judge0 returned an invalid response.",
                        results
                );
            }

            int statusId =
                    statusNode.get("id").asInt();

            String judge0Status =
                    statusNode.has("description")
                            ? statusNode
                                .get("description")
                                .asText()
                            : "Unknown";


            // =====================================
            // 7. Compilation Error
            // Judge0 status ID 6
            // =====================================

            if (statusId == 6) {

                return new RunCodeResponse(
                        "COMPILATION_ERROR",
                        testCases.size(),
                        passed,
                        failed,
                        getText(result, "compile_output"),
                        null,
                        "Code could not be compiled.",
                        results
                );
            }


            // =====================================
            // 8. Time Limit Exceeded
            // Judge0 status ID 5
            // =====================================

            if (statusId == 5) {

                failed++;

                results.add(
                        new TestCaseResultResponse(
                                testCase.getTestCaseOrder(),
                                "TIME_LIMIT_EXCEEDED",
                                getActualOutput(result)
                        )
                );

                continue;
            }


            // =====================================
            // 9. Runtime errors
            // Judge0 IDs 7 - 12
            // =====================================

            if (statusId >= 7 &&
                    statusId <= 12) {

                failed++;

                results.add(
                        new TestCaseResultResponse(
                                testCase.getTestCaseOrder(),
                                "RUNTIME_ERROR",
                                getActualOutput(result)
                        )
                );

                continue;
            }


            // =====================================
            // 10. Other Judge0 errors
            // =====================================

            if (statusId != 3) {

                failed++;

                results.add(
                        new TestCaseResultResponse(
                                testCase.getTestCaseOrder(),
                                judge0Status
                                        .toUpperCase()
                                        .replace(" ", "_"),
                                getActualOutput(result)
                        )
                );

                continue;
            }


            // =====================================
            // 11. Program executed successfully
            // =====================================

            String actualOutput =
                    getActualOutput(result);

            String expectedOutput =
                    testCase.getExpectedOutput();


            // =====================================
            // 12. Compare output
            // =====================================

            boolean passedTestCase =
                    normalizeOutput(actualOutput)
                            .equals(
                                    normalizeOutput(
                                            expectedOutput
                                    )
                            );


            if (passedTestCase) {

                passed++;

                results.add(
                        new TestCaseResultResponse(
                                testCase.getTestCaseOrder(),
                                "PASSED",
                                actualOutput
                        )
                );

            } else {

                failed++;

                results.add(
                        new TestCaseResultResponse(
                                testCase.getTestCaseOrder(),
                                "FAILED",
                                actualOutput
                        )
                );
            }
        }


        // =========================================
        // 13. Final result
        // =========================================

        String finalStatus =
                failed == 0
                        ? "ALL_TEST_CASES_PASSED"
                        : "TEST_CASES_FAILED";


        return new RunCodeResponse(
                finalStatus,
                testCases.size(),
                passed,
                failed,
                null,
                null,
                null,
                results
        );
    }


    // =============================================
    // Get stdout
    // =============================================

    private String getActualOutput(
            JsonNode result
    ) {

        return getText(
                result,
                "stdout"
        );
    }


    // =============================================
    // Safely read text field
    // =============================================

    private String getText(
            JsonNode result,
            String field
    ) {

        JsonNode node =
                result.get(field);

        if (node == null ||
                node.isNull()) {

            return "";
        }

        return node.asText();
    }


    // =============================================
    // Normalize output before comparison
    // =============================================

    private String normalizeOutput(
            String output
    ) {

        if (output == null) {
            return "";
        }

        return output
                .replace("\r\n", "\n")
                .trim();
    }
}