package com.precapstone.fiveguys_backend.message.send;

import com.precapstone.fiveguys_backend.message.send.option.Target;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PpurioSendParam {
    String messageType;
    String content;
    String fromNumber;
    List<Target> targets;
    String subject;
    List<String> filePaths;
}
