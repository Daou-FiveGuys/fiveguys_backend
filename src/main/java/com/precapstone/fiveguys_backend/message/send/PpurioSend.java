package com.precapstone.fiveguys_backend.message.send;

import com.precapstone.fiveguys_backend.message.auth.PpurioAuth;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PpurioSend {
    private final RestTemplate restTemplate;

    // 뿌리오 API 이용을 위한 고정 주소
    @Value("${spring.ppurio.base-url}")
    private String url;

    // 이용할 뿌리오 계정
    @Value("${spring.ppurio.account}")
    private String ppurioAccount;

    private final PpurioAuth ppurioAuth;

    public void sendMessage(String fromNumber, String toNumber, String filePath) throws IOException {
        String accessToken = ppurioAuth.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ accessToken);

        var requestBody = createSendTestParams(fromNumber, toNumber, filePath);

        HttpEntity<Map> request = new HttpEntity<>(requestBody, headers);

        restTemplate.postForObject(url+"/v1/message", request, MessageResponse.class);
    }

    private Map<String, Object> createSendTestParams(String fromNumber, String toNumber, String filePath) throws IOException {
        String name = "TestName";
        String var1 = "var1 항목 내용입니다.";

        HashMap<String, Object> params = new HashMap<>();
        params.put("account", ppurioAccount);
        params.put("messageType", "LMS");
        params.put("from", fromNumber);
        params.put("content", "[*이름*], hello this is [*1*]");
        params.put("duplicateFlag", "Y");
        params.put("rejectType", "AD"); // 광고성 문자 수신거부 설정, 비활성화할 경우 해당 파라미터 제외
        params.put("targetCount", 1);
        params.put("targets", List.of(
                Map.of("to", toNumber,
                        "name", name,
                        "changeWord", Map.of(
                                "var1", var1)))
        );
//        params.put("files", List.of(
//                createFileTestParams(filePath)
//        ));
        params.put("refKey", RandomStringUtils.random(32, true, true)); // refKey 생성, 32자 이내로 아무 값이든 상관 없음

        return params;
    }

    private Map<String, Object> createFileTestParams(String filePath) throws RuntimeException, IOException {
        FileInputStream fileInputStream = null;
        try {
            File file = new File(filePath);
            byte[] fileBytes = new byte[ (int) file.length()];
            fileInputStream = new FileInputStream(file);
            int readBytes = fileInputStream.read(fileBytes);

            if (readBytes != file.length()) {
                throw new IOException();
            }

            String encodedFileData = Base64.getEncoder().encodeToString(fileBytes);

            HashMap<String, Object> params = new HashMap<>();
            params.put("size", file.length());
            params.put("name", file.getName());
            params.put("data", encodedFileData);
            return params;
        } catch (IOException e) {
            throw new RuntimeException("파일을 가져오는데 실패했습니다.", e);
        } finally {
            if(fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }
}
