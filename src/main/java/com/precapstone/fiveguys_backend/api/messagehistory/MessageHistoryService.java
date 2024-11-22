package com.precapstone.fiveguys_backend.api.messagehistory;

import com.precapstone.fiveguys_backend.api.user.UserService;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.precapstone.fiveguys_backend.exception.errorcode.MessageHistoryErrorCode.MESSAGE_HISTORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MessageHistoryService {
    private final MessageHistoryRepository messageHistoryRepository;
    private final UserService userService;

    public MessageHistory createMessageHistory(HessageHistoryCreateDTO messageHistoryDTO) {
        // TODO: 1. [예외처리] 전화번호 서식이 틀린 경우

        // TODO: 2. [예외처리] 본문 길이 틀린 경우

        var user = userService.findById(messageHistoryDTO.getUserId());

        var messageHistory = MessageHistory.builder()
                .messageType(messageHistoryDTO.getMessageType())
                .fromNumber(messageHistoryDTO.getFromNumber())
                .contact2s(messageHistoryDTO.getContact2s())
                .subject(messageHistoryDTO.getSubject())
                .content(messageHistoryDTO.getContent())
                .createdAt(LocalDateTime.now())
                .user(user)
                // TODO: sendImage 추가할 것
                .build();

        return messageHistory;
    }

    public MessageHistory readMessageHistory(Long messageHistoryId) {
        var messageHistory = messageHistoryRepository.findByMessageHistoryId(messageHistoryId)
                .orElseThrow(() -> new ControlledException(MESSAGE_HISTORY_NOT_FOUND));

        return messageHistory;
    }

    public List<MessageHistory> readAllMessageHistory(Long userId) {
        var user = userService.findById(userId);

        var messageHistory = messageHistoryRepository.findByUser(user)
                .orElseThrow(()-> new ControlledException(MESSAGE_HISTORY_NOT_FOUND));

        return messageHistory;
    }

    public MessageHistory delete(Long messageHistoryId) {
        var messageHistory = readMessageHistory(messageHistoryId);

        messageHistoryRepository.deleteById(messageHistoryId);
        return messageHistory;
    }
}
