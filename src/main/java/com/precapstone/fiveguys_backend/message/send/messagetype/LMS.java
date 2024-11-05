package com.precapstone.fiveguys_backend.message.send.messagetype;

import com.precapstone.fiveguys_backend.message.send.option.Target;

import java.util.List;

public class LMS extends MessageType {
    public LMS(String ppurioAccount, String fromNumber, String message, List<Target> targets) {
        super(ppurioAccount, fromNumber, message, targets);
        // TODO: 문자 메세지 길이 파악하기

        params.put("messageType", "LMS");
    }
}
