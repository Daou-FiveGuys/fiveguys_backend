package com.precapstone.fiveguys_backend.api.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.ResponseMessage;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class ChatbotService {

    private final RestTemplate restTemplate;
    @Value("${chatbot.chatbot_id}")
    private String CHATBOT_ID;

    @Value("${chatbot.chatbot_api_key}")
    private String CHATBOT_API_KEY;

    // Chatling으로 API보내고 요청 받기
    public CommonResponse getChatbotAnswer(String question){

        String url = "https://api.chatling.ai/v2/chatbots/" + CHATBOT_ID + "/ai/kb/chat";

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + CHATBOT_API_KEY);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");

        // 요청 바디 설정
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", question);
        jsonObject.put("ai_model_id", 8);

        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, entity, JsonNode.class);

        // 응답 처리
        String res = response.getBody().get("data").get("response").asText();

        if (res.equals("안녕하세요! 무엇을 도와드릴까요?") || res.equals("Hello! How can I assist you today?")) {
            return CommonResponse.builder()
                    .code(404)
                    .message(ResponseMessage.CHATBOT_ANS_FAIL)
                    .data("저도 잘 모르겠네요... 뿌리오 고객센터 연락처(1588-5412)")
                    .build();
        }

        return CommonResponse.builder()
                .code(200)
                .message(ResponseMessage.SUCCESS)
                .data(res)
                .build();
    }
}
