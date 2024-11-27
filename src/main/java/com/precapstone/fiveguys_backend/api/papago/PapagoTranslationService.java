package com.precapstone.fiveguys_backend.api.papago;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PapagoTranslationService {
    private static final String API_URL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";
    @Value("${papago.client-id}")
    private String CLIENT_ID;
    @Value("${papago.client-secret}")
    private String CLIENT_SECRET;

    public CommonResponse translate(String text, String source, String target) {
        // HTTP 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", CLIENT_ID);
        headers.set("X-NCP-APIGW-API-KEY", CLIENT_SECRET);
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        // 요청 데이터 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("source", source);
        params.add("target", target);
        params.add("text", text);

        // HTTP 요청
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, Map.class);

        // 응답 처리
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("message")) {
            Map<String, Object> message = (Map<String, Object>) responseBody.get("message");
            Map<String, Object> result = (Map<String, Object>) message.get("result");
            return CommonResponse.builder()
                    .code(200)
                    .data((String) result.get("translatedText"))
                    .build();
        }
        return CommonResponse.builder()
                .code(200)
                .data("오류가 발생했습니다.")
                .build();

    }
}
