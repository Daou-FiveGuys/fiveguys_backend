package com.precapstone.fiveguys_backend.api.message;

import com.precapstone.fiveguys_backend.api.message.send.option.Target;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Setter
@Getter
public class PpurioMessageDTO {
    String messageType;
    String content;
    String fromNumber;
    List<Target> targets;
    String subject;
    LocalDateTime sendTime;
}