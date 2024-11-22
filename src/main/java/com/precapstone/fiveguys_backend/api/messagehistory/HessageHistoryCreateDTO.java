package com.precapstone.fiveguys_backend.api.messagehistory;

import com.precapstone.fiveguys_backend.api.sendImage.SendImage;
import com.precapstone.fiveguys_backend.entity.Contact2;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class HessageHistoryCreateDTO {
    private Long userId;
    private String content;
    private String fromNumber;
    private MessageType messageType;
    private String subject;
    private List<Contact2> contact2s;
    private SendImage sendImage;
}
