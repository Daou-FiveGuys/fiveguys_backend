package com.precapstone.fiveguys_backend.message.send;

import lombok.Getter;

/**
 * 뿌리오 메세지 전송 서비스에서 전달하는 결과 클래스
 */
@Getter
public class PpurioSendResponse {
    // 응답 코드
    private final String code;

    // 응답 메시지
    private final String description;

    // 요청자가 부여한 키 (현재 메세지 키는 랜덤으로 발급)
    private final String refKey;

    // 메시지 고유 키
    private final String messageKey;

    /**
     *
     * @param code 응답 코드
     * @param description 응답 메시지
     * @param refKey 요청자가 부여한 키 (현재 메세지 키는 랜덤으로 발급)
     * @param messageKey 메시지 고유 키
     */
    public PpurioSendResponse(String code, String description, String refKey, String messageKey) {
        this.code = code;
        this.description = description;
        this.refKey = refKey;
        this.messageKey = messageKey;
    }
}
