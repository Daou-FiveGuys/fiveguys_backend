package com.precapstone.fiveguys_backend.api.nlp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GptService {
    @Value("${openai.url}")
    private String openaiUrl;
    @Value("${openai.api_key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String TEXT_INSTRUCTION = "단체 문자 발송을 위해 다음 내용을 문자 형식으로 구체적으로 작성해 주세요. 응답은 한국어로 출력되어야 합니다.";

    private static final String IMG_PROMPT_INSTRUCTION = "Please extract a prompt for image generation based on the following content, and respond in English.";

    private static final String EXTRACT_KEY_POINTS_INSTRUCTION = "다음 문자 내용에서 중요한 내용을 문장 단위로 요약해 주세요. 각 문장은 세미콜론(;)으로 구분하고, 응답은 한국어로 해 주세요.";

    public CommonResponse makeText(String authorization, String prompt){
        if(getPromptIsNull(prompt))
            return CommonResponse.builder()
                    .code(400)
                    .message("prompt is empty")
                    .build();
        String result = request(prompt, TEXT_INSTRUCTION, 512);
        if(result == null || result.isEmpty())
            return CommonResponse.builder()
                    .code(400)
                    .message("response is empty")
                    .build();

        return CommonResponse.builder()
                .code(200)
                .data(
                        trimDoubleQuotes(trimEscapeSequences(trimRole(result))))
                .build();
    }

    public CommonResponse generateImagePrompt(String authorization, String prompt){
        if(getPromptIsNull(prompt))
            return CommonResponse.builder()
                    .code(400)
                    .message("prompt is empty")
                    .build();

        String result = request(prompt, IMG_PROMPT_INSTRUCTION, 512);
        if(result == null || result.isEmpty())
            return CommonResponse.builder()
                    .code(400)
                    .message("response is empty")
                    .build();

        return CommonResponse.builder()
                .code(200)
                .data(
                        trimDoubleQuotes(trimEscapeSequences(trimRole(result))))
                .build();
    }

    public CommonResponse extractKeyPoints(String authorization, String prompt){
        if(getPromptIsNull(prompt))
            return CommonResponse.builder()
                    .code(400)
                    .message("prompt is empty")
                    .build();

        String result = request(prompt, EXTRACT_KEY_POINTS_INSTRUCTION, 512);
        if(result == null || result.isEmpty())
            return CommonResponse.builder()
                    .code(400)
                    .message("response is empty")
                    .build();

        String[] tokens = trimDoubleQuotes(trimEscapeSequences(trimRole(result))).split(";");
        ArrayList<String> response = new ArrayList<>();
        for(String token : tokens){
            response.add(token.trim());
        }

        return CommonResponse.builder()
                .code(200)
                .data(response)
                .build();
    }

    private String request(String prompt, String instruction, int maxTokens){
        HttpEntity<Map<String, Object>> request = getMapHttpEntity(prompt, instruction, maxTokens);
        ResponseEntity<String> response = restTemplate.exchange(openaiUrl, HttpMethod.POST, request, String.class);
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean getPromptIsNull(String prompt){
        return prompt == null || prompt.isEmpty();
    }

    private @NotNull HttpEntity<Map<String, Object>> getMapHttpEntity(String prompt, String instruction, int maxTokens) {
        String fullPrompt = instruction + "\"" + prompt + "\"";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", "gpt-4-turbo");
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("max_tokens", maxTokens);

        Map<String, Object> userMessageMap = new HashMap<>();
        userMessageMap.put("role", "user");  // 사용자 메시지 역할
        userMessageMap.put("content", fullPrompt);

        requestBody.put("messages", new Object[] {userMessageMap});

        return new HttpEntity<>(requestBody, headers);
    }

    private String trimRole(String input){
        return input.replaceAll("^\\*\\*.*?\\*\\*", "");
    }

    private String trimEscapeSequences(String input) {
        return input.replaceAll("[\\n\\t]", " ").trim();
    }

    private String trimDoubleQuotes(String input){
        return input.replace("\"", "");
    }
}
