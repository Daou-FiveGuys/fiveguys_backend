package com.precapstone.fiveguys_backend.message.send;

import lombok.Getter;

@Getter
public class PpurioSendResponse {
    private final String code;
    private final String description;
    private final String refKey;
    private final String messageKey;

    public PpurioSendResponse(String code, String description, String refKey, String messageKey) {
        this.code = code;
        this.description = description;
        this.refKey = refKey;
        this.messageKey = messageKey;
    }
}
