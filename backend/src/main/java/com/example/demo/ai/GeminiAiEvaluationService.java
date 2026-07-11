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
                - Analyze the source code statically.
                - Identify logical bugs and edge cases.
                - Do not assume code is correct just because the general approach looks correct.
                - Check syntax and completeness.
                - Check time and space complexity.
                - Be consistent and strict but fair.

                MARKING RUBRIC:

                1. Logic and correctness: 0 to 15 marks
                2. Edge-case handling: 0 to 6 marks
                3. Efficiency: 0 to 4 marks
                4. Code quality: 0 to 3 marks
                5. Syntax and completeness: 0 to 2 marks

                Total maximum score: 30.

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
                  "feedback": "Short explanation of the evaluation"
                }

                Rules:
                - score must equal the sum of all five category scores.
                - score must be between 0 and 30.
                - confidence must be between 0 and 100.
                - Do not include markdown.
                - Do not include ```json.
                - Return JSON only.
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