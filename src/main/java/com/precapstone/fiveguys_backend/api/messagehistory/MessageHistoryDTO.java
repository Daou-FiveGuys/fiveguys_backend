package com.precapstone.fiveguys_backend.api.messagehistory;

import com.precapstone.fiveguys_backend.api.messagehistory.messagehistory.MessageType;
import com.precapstone.fiveguys_backend.entity.Contact2;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@Builder
public class MessageHistoryDTO {
    private String userId;
    private String content;
    private String fromNumber;
    private MessageType messageType;
    private String subject;
    private List<Contact2> contact2s;
    private MultipartFile sendImage;
}
