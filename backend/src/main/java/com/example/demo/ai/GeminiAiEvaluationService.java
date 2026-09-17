package com.example.demo.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.entity.Question;
import com.example.demo.entity.Submission;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GeminiAiEvaluationService
        implements AiEvaluationService {

    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    private final String apiKey;

    private final String model;


    public GeminiAiEvaluationService(
            ObjectMapper objectMapper,
            @Value("${ai.gemini.api-key}") String apiKey,
            @Value("${ai.gemini.model}") String model
    ) {

        this.restClient = RestClient.create();

        this.objectMapper = objectMapper;

        this.apiKey = apiKey;

        this.model = model;
    }


    @Override
    public AiEvaluationResult evaluate(
            Submission submission
    ) {

        try {

            String prompt = buildPrompt(submission);

            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/"
                            + model
                            + ":generateContent?key="
                            + apiKey;


            String requestBody = objectMapper.writeValueAsString(
                    new GeminiRequest(prompt)
            );


            String response = restClient
                    .post()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);


            return parseResponse(response);

        } catch (Exception exception) {

            throw new RuntimeException(
                    "AI evaluation failed: "
                            + exception.getMessage(),
                    exception
            );
        }
    }


    private String buildPrompt(Submission submission) {

    Question question = submission.getQuestion();

    return """
            You are an educational coding evaluator.

            Evaluate the student's solution for the given programming problem.

            MAIN GOAL:
            Check whether the student's solution correctly solves the problem.
            Be fair, educational, and moderately lenient. Do not evaluate it like
            a strict competitive programming judge.

            IMPORTANT SCORING SYSTEM:

            You MUST evaluate the student's code using a FIXED INTERNAL RUBRIC
            of exactly 30 marks.

            The question's actual maximum marks may be 10, 20, 30, 50, 100,
            or any other value.

            IGNORE the question's actual maximum marks while calculating the AI
            evaluation.

            ALWAYS calculate the AI evaluation out of 30.

            The backend will later convert the AI score from /30 to the
            question's actual maximum marks.

            FIXED 30-MARK RUBRIC:

            - Correctness: maximum 15 marks
            - Edge Case Handling: maximum 6 marks
            - Efficiency: maximum 4 marks
            - Code Quality: maximum 3 marks
            - Syntax & Completeness: maximum 2 marks

            TOTAL: 30 marks

            IMPORTANT RULES:

            1. CORRECT SOLUTIONS
            - If the algorithm correctly solves the problem, award FULL MARKS.
            - Do not deduct marks simply because the solution is not optimal.
            - A correct brute-force solution is a valid solution.
            - A different algorithm from the expected/reference solution is valid
              if it correctly solves the problem.
            - Do not require the student's solution to match a particular approach.

            2. EFFICIENCY
            - Do NOT deduct marks merely because a faster or more optimal approach exists.
            - Consider efficiency only when the chosen complexity is genuinely
              unreasonable for the stated problem constraints.
            - If no constraints are provided, do not assume extremely strict limits.
            - Do not punish a correct solution simply for being O(n^2), O(n log n),
              or otherwise non-optimal.

            3. EDGE CASES
            - Consider whether the algorithm reasonably handles important edge cases.
            - Do not invent unrealistic hidden requirements.
            - Minor missing edge cases should result in only a small deduction.
            - If the main algorithm is correct and edge-case handling is mostly
              correct, award generous partial credit.

            4. SYNTAX AND FORMAT
            - Evaluate the intended algorithm even if the code has minor syntax
              or formatting mistakes.
            - Do not deduct marks for missing main(), driver code, class Solution,
              input/output boilerplate, imports, namespace/package declarations,
              or similar boilerplate when the intended solution is clear.
            - LeetCode-style, function-only, and complete-program solutions are
              all acceptable.
            - Minor syntax mistakes should cause only a small deduction unless
              they make the solution impossible to understand.

            5. PARTIAL CREDIT
            - Give generous partial credit when the core approach is correct but
              the implementation contains small mistakes.
            - If the intended solution is clearly correct but contains minor
              implementation errors, normally award 70%%-95%% of the available
              30 marks.
            - Reserve very low scores for solutions that are fundamentally
              incorrect, largely incomplete, unrelated, or impossible to
              understand.

            6. NO EXECUTION
            - The code is NOT compiled or executed.
            - No hidden test-case results are available.
            - Evaluate the source code statically.
            - Do not claim that code definitely passes or fails a test case unless
              this can be determined from the code itself.

            SCORING:

            Correctness: 0 to 15
            Edge Case Handling: 0 to 6
            Efficiency: 0 to 4
            Code Quality: 0 to 3
            Syntax & Completeness: 0 to 2

            The total AI score MUST be:

            correctnessScore
            + edgeCaseScore
            + efficiencyScore
            + codeQualityScore
            + syntaxScore

            Therefore:

            score MUST always be between 0 and 30.

            IMPORTANT SCORING PRINCIPLE:

            Correctness is the most important factor.

            If the student's algorithm is correct and its complexity is reasonable
            for the stated constraints, award the maximum available marks,
            including full efficiency marks.

            Do NOT reduce marks because:
            - a more optimal solution exists
            - the reference solution is different
            - the solution uses brute force
            - the solution uses a different valid algorithm
            - main() is missing
            - class Solution is missing
            - driver/input/output code is missing
            - minor syntax or boilerplate issues exist

            Only deduct marks when there is an actual issue with the solution.

            PROBLEM:

            Title:
            %s

            Problem Statement:
            %s

            Examples:
            %s

            Language:
            %s

            STUDENT CODE:

            ```%s
            %s
            ```

            Return ONLY valid JSON.

            Use exactly:

            {
              "score": 0,
              "correctnessScore": 0,
              "edgeCaseScore": 0,
              "efficiencyScore": 0,
              "codeQualityScore": 0,
              "syntaxScore": 0,
              "confidence": 0,
              "feedback": "Brief constructive feedback explaining what was done correctly and what could be improved."
            }

            JSON RULES:
            - correctnessScore must be between 0 and 15.
            - edgeCaseScore must be between 0 and 6.
            - efficiencyScore must be between 0 and 4.
            - codeQualityScore must be between 0 and 3.
            - syntaxScore must be between 0 and 2.
            - score must equal the sum of all five category scores.
            - score must be between 0 and 30.
            - confidence must be between 0 and 100.
            - Return JSON only.
            - Do not include markdown.
            - Do not include ```json.
            """
            .formatted(
                    question.getTitle(),
                    question.getProblemStatement(),
                    question.getExamples(),
                    submission.getLanguage(),
                    submission.getLanguage(),
                    submission.getSourceCode()
            );
}


    private AiEvaluationResult parseResponse(
            String rawResponse
    ) throws Exception {

        JsonNode root =
                objectMapper.readTree(rawResponse);


        String aiText = root
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();


        aiText = cleanJson(aiText);


        return objectMapper.readValue(
                aiText,
                AiEvaluationResult.class
        );
    }


    private String cleanJson(String text) {

        return text
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }


    // Gemini API request structure

    private record GeminiRequest(
            Content[] contents
    ) {

        GeminiRequest(String prompt) {

            this(
                    new Content[]{
                            new Content(
                                    new Part[]{
                                            new Part(prompt)
                                    }
                            )
                    }
            );
        }
    }


    private record Content(
            Part[] parts
    ) {
    }


    private record Part(
            String text
    ) {
    }
}