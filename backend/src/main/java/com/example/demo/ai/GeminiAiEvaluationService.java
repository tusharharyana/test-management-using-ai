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
                You are an expert competitive programming evaluator.

                Evaluate the student's code against the given problem.

                IMPORTANT:
                - No hidden test cases are available.
                - The code is NOT compiled or executed.
                - Analyze the source code statically.
                - Focus primarily on the student's algorithm, logic, and problem-solving approach.
                - This is an educational coding platform, not a strict online judge.
                - Students may submit LeetCode-style solutions (class Solution), function-only implementations, or complete programs with main().
                - Do NOT deduct marks for missing main(), class Solution, driver code, input/output handling, or other boilerplate code.
                - Do NOT heavily penalize minor syntax mistakes (missing semicolons, brackets, imports, namespace declarations, etc.) if the intended algorithm is clear.
                - Reward partial correctness and good problem-solving.
                - Deduct significant marks when the algorithm is incorrect, incomplete, or fails to handle important edge cases.
                - Do NOT automatically deduct marks for a brute-force or non-optimal approach.
                - Only consider efficiency when the approach is genuinely unacceptable for the stated constraints.                
                - Be fair, constructive, and moderately lenient while maintaining consistency.

                IMPORTANT EVALUATION RULES:
                - This is a competitive programming solution, not a complete software application.
                - Students may submit solutions in different valid formats such as:
                * LeetCode style (class Solution with only the required function)
                * Function-only implementation
                * Complete program with main() and input/output handling
                - DO NOT deduct marks because the solution does not contain:
                * main() function
                * class Solution wrapper
                * Driver code
                * Input/output handling
                * Scanner/cin/cout code
                * Boilerplate code
                * Package declarations or include statements, unless they are essential to understanding the solution.
                - Assume the online judge provides the driver code and invokes the required function correctly.
                - Evaluate the algorithm and implementation only.
                - Deduct significant marks only when the core algorithm is fundamentally incorrect, largely incomplete, or clearly incapable of solving the problem.
                
                               Additional Rules:
                - Never reduce marks only because main(), driver code, or class Solution is missing.
                - If the algorithm is correct but written as a LeetCode-style function, evaluate it as a valid competitive programming solution.
                - If the algorithm cannot be understood because essential implementation is missing, deduct marks accordingly.
                - If the algorithm is correct but written as a function, evaluate it as a valid competitive programming solution.


                ABSOLUTE RULE FOR BRUTE-FORCE SOLUTIONS:

                - A correct brute-force solution MUST receive FULL MARKS.
                - If the student's brute-force approach correctly solves the problem,
                  produces the correct result, and handles the relevant edge cases,
                  award the FULL maximum marks for the question.
                - DO NOT deduct marks simply because the solution is brute force.
                - DO NOT deduct marks simply because a more optimal algorithm exists.
                - DO NOT deduct Efficiency marks merely because another solution
                  has better time or space complexity.
                - A brute-force solution is a VALID solution if it correctly solves
                  the stated problem.
                - The expected or reference optimal solution is NOT the only valid
                  solution.
                - A different algorithm must be accepted if it correctly solves
                  the problem.
                - If the brute-force solution is correct and its complexity is
                  acceptable for the stated input constraints, award FULL marks
                  in every applicable category.
                - Only reduce marks if there is an actual problem in correctness,
                  edge-case handling, code quality, syntax, completeness, or if
                  the complexity is genuinely unacceptable for the stated constraints.
                - NEVER give a correct brute-force solution a low or zero score
                  merely because a faster algorithm exists.

                IMPORTANT:
                "Brute force" does NOT mean "incorrect".
                "Non-optimal" does NOT mean "incorrect".
                "Slower than the optimal solution" does NOT mean "wrong".

                MARKING RUBRIC:

                Maximum Marks for this question: %d

                Distribute marks proportionally according to the maximum marks.

                Suggested weightage:

                - Logic & Correctness: 50%%
                - Edge Case Handling: 20%%
                - Efficiency: 13%%
                - Code Quality: 10%%
                - Syntax & Completeness: 7%%

                Scoring Philosophy:
                - Reward understanding and the correct approach over perfect syntax.
                - Award generous partial credit if the algorithm is mostly correct.
                - Do not be overly strict because the code is not executed.
                - Reserve very low marks only for completely incorrect or unrelated solutions.
                - This evaluation is for educational assessment, not competitive ranking.
                - Give the benefit of the doubt when the student's intended algorithm is clear.
                - Minor syntax mistakes should result in only small deductions.
                - If the overall approach is correct but implementation has small mistakes, award between 70%% and 90%% of the available marks.
                - Use very low scores only when the solution is largely unrelated, missing, or fundamentally incorrect.
                Here is the problem statement:

                PROBLEM TITLE:
                %s

                PROBLEM STATEMENT:
                %s

                EXAMPLES:
                %s

                PROGRAMMING LANGUAGE:
                %s

                STUDENT SOURCE CODE:
                ```%s
                %s
                ```

                Return ONLY valid JSON.

                Use exactly this structure:

                {
                  "score": 0,
                  "correctnessScore": 0,
                  "edgeCaseScore": 0,
                  "efficiencyScore": 0,
                  "codeQualityScore": 0,
                  "syntaxScore": 0,
                  "confidence": 0,
                  "feedback": "A constructive explanation highlighting what the student did correctly, what mistakes were found, and how the solution can be improved."
                }

                Rules:
                - score must equal the sum of all five category scores.
                - score must be between 0 and the Maximum Marks for this question.
                - confidence must be between 0 and 100.
                - Do not include markdown.
                - Do not include ```json.
                - Return JSON only.
                """
                .formatted(
                        question.getMaxMarks(), 
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