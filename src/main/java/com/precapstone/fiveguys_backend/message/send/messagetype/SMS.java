package com.precapstone.fiveguys_backend.message.send.messagetype;

import java.util.List;

public class SMS extends MessageType {
    public SMS(String ppurioAccount, String fromNumber, String message, List targets) {
        super(ppurioAccount, fromNumber, message, targets);
        // TODO: 문자 메세지 길이 파악하기

        params.put("messageType", "SMS");
    }
}
