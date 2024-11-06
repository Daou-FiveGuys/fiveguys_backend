package com.precapstone.fiveguys_backend.message.send;

import com.precapstone.fiveguys_backend.message.send.messagetype.MMS;
import com.precapstone.fiveguys_backend.message.send.messagetype.LMS;
import com.precapstone.fiveguys_backend.message.auth.PpurioAuth;
import com.precapstone.fiveguys_backend.message.send.messagetype.MessageType;
import com.precapstone.fiveguys_backend.message.send.messagetype.SMS;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PpurioSendService {
    private final RestTemplate restTemplate;

    // 뿌리오 계정
    @Value("${spring.ppurio.account}")
    private String ppurioAccount;

    // 뿌리오 API 이용을 위한 고정 주소
    @Value("${spring.ppurio.base-url}")
    private String url;

    private final PpurioAuth ppurioAuth;

    public void sendMessage(PpurioSendParam ppurioSendParam) throws IOException {
        // 토큰 발급
        String accessToken = ppurioAuth.getAccessToken();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ accessToken);

        // 바디 설정
        var requestBody = createSendParams(ppurioSendParam);

        // 전송 데이터 생성
        HttpEntity<Map> request = new HttpEntity<>(requestBody, headers);

        // 전송
        restTemplate.postForObject(url+"/v1/message", request, PpurioSendResponse.class);
    }

    /**
     * PpurioSendParam을 통해 전달받은 MessageType을 통해 메세지 정보를 분기 처리
     * 
     * @param ppurioSendParam 사용자가 요청한 속성 정보
     *
     * @return 전송할 메세지 생성 (Map)
     * 
     * @throws IOException
     */
    private Map createSendParams(PpurioSendParam ppurioSendParam) throws IOException {
        // 각 메세지 타입에 맞게 requestBody 분리
        MessageType messageType = switch (ppurioSendParam.messageType) {
            case "MMS" -> new MMS(ppurioAccount, ppurioSendParam.fromNumber, ppurioSendParam.content, ppurioSendParam.targets, ppurioSendParam.filePaths);
            case "LMS" -> new LMS(ppurioAccount, ppurioSendParam.fromNumber, ppurioSendParam.content, ppurioSendParam.targets);
            case "SMS" -> new SMS(ppurioAccount, ppurioSendParam.fromNumber, ppurioSendParam.content, ppurioSendParam.targets);
            default -> null;
        };

        return messageType.getParams();
    }
}
