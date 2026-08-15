package com.example.demo.service;

import com.example.demo.config.Judge0Config;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class Judge0Service {

    private final Judge0Config judge0Config;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public Judge0Service(
            Judge0Config judge0Config,
            ObjectMapper objectMapper
    ) {
        this.judge0Config = judge0Config;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    public JsonNode executeCode(
            String sourceCode,
            String stdin,
            int languageId
    ) throws Exception {

        String url =
                judge0Config.getBaseUrl()
                        + "/submissions?base64_encoded=false&wait=false";

        // -----------------------------------------
        // Create Judge0 request body
        // -----------------------------------------

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("source_code", sourceCode);
        requestBody.put("language_id", languageId);
        requestBody.put(
                "stdin",
                stdin == null ? "" : stdin
        );

        // Convert explicitly to JSON
        String jsonBody =
                objectMapper.writeValueAsString(requestBody);

        System.out.println("Sending to Judge0:");
        System.out.println(jsonBody);

        // -----------------------------------------
        // HTTP headers
        // -----------------------------------------

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        // -----------------------------------------
        // Send request
        // -----------------------------------------

        HttpEntity<String> request =
                new HttpEntity<>(
                        jsonBody,
                        headers
                );

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        url,
                        request,
                        String.class
                );

        System.out.println("Judge0 response:");
        System.out.println(response.getBody());

        // -----------------------------------------
        // Get token
        // -----------------------------------------

        JsonNode submissionResponse =
                objectMapper.readTree(
                        response.getBody()
                );

        JsonNode tokenNode =
                submissionResponse.get("token");

        if (tokenNode == null) {

            throw new RuntimeException(
                    "Judge0 did not return token. Response: "
                            + response.getBody()
            );
        }

        String token = tokenNode.asText();

        // -----------------------------------------
        // Wait for execution
        // -----------------------------------------

        JsonNode result = waitForResult(token);

        System.out.println("FINAL JUDGE0 RESULT:");
        System.out.println(
                objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(result)
        );

        return result;
    }


    private JsonNode waitForResult(String token)
        throws Exception {

    String url =
            judge0Config.getBaseUrl()
                    + "/submissions/"
                    + token
                    + "?base64_encoded=true";

    for (int i = 0; i < 30; i++) {

        ResponseEntity<String> response =
                restTemplate.getForEntity(
                        url,
                        String.class
                );

        JsonNode result =
                objectMapper.readTree(
                        response.getBody()
                );

        JsonNode status =
                result.get("status");

        if (status != null) {

            int statusId =
                    status.get("id").asInt();

            /*
             * Judge0:
             *
             * 1 = In Queue
             * 2 = Processing
             * 3+ = Finished
             */

            if (statusId > 2) {

                decodeJudge0Field(
                        result,
                        "stdout"
                );

                decodeJudge0Field(
                        result,
                        "stderr"
                );

                decodeJudge0Field(
                        result,
                        "compile_output"
                );

                decodeJudge0Field(
                        result,
                        "message"
                );

                return result;
            }
        }

        Thread.sleep(500);
    }

    throw new RuntimeException(
            "Judge0 execution timed out"
    );
}
private void decodeJudge0Field(
        JsonNode result,
        String field
) {

    JsonNode node =
            result.get(field);

    if (node == null ||
            node.isNull() ||
            !node.isTextual()) {

        return;
    }

    String encoded =
            node.asText();

    if (encoded.isEmpty()) {
        return;
    }

    try {

        byte[] decoded =
                java.util.Base64
                        .getDecoder()
                        .decode(
                                encoded.replaceAll("\\s", "")
                        );

        String value =
                new String(
                        decoded,
                        java.nio.charset.StandardCharsets.UTF_8
                );

        ((com.fasterxml.jackson.databind.node.ObjectNode) result)
                .put(field, value);

    } catch (IllegalArgumentException e) {

        // Leave the field unchanged if it is not valid Base64.
    }
}
}