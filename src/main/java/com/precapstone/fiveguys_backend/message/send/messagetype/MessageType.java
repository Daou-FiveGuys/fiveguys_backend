package com.precapstone.fiveguys_backend.message.send.messagetype;

import com.precapstone.fiveguys_backend.message.send.option.Target;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MessageType {
    protected Map<String, Object> params = new HashMap<>();

    public MessageType(String ppurioAccount, String fromNumber, String message, List<Target> targets) {
        params.put("account", ppurioAccount);
        params.put("messageType", "SMS");
        params.put("from", fromNumber);
        params.put("content", message);
        params.put("duplicateFlag", "Y");
        params.put("rejectType", "AD");
        params.put("targetCount", targets.size());
        params.put("targets", targets);
        params.put("refKey", RandomStringUtils.random(32, true, true));
    }
}
