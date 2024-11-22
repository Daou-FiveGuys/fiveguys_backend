package com.precapstone.fiveguys_backend.api.messagehistory;

import com.precapstone.fiveguys_backend.api.user.UserService;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.precapstone.fiveguys_backend.exception.errorcode.MessageHistoryErrorCode.MESSAGE_HISTORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MessageHistoryService {
    private final MessageHistoryRepository messageHistoryRepository;
    private final UserService userService;

    public MessageHistory create(HessageHistoryCreateDTO messageHistoryDTO) {
        // TODO: 1. [예외처리] 전화번호 서식이 틀린 경우

        // TODO: 2. [예외처리] 본문 길이 틀린 경우

        var user = userService.findById(messageHistoryDTO.getUserId());

        // TODO: MMS인 경우에만 전달 받음

        var messageHistory = MessageHistory.builder()
                .messageType(messageHistoryDTO.getMessageType())
                .fromNumber(messageHistoryDTO.getFromNumber())
                .contact2s(messageHistoryDTO.getContact2s())
                .subject(messageHistoryDTO.getSubject())
                .content(messageHistoryDTO.getContent())
                .createdAt(LocalDateTime.now())
                .user(user)
                //.sendImage(sendImage)
                .build();

        return messageHistory;
    }

    public MessageHistory read(Long messageHistoryId) {
        var messageHistory = messageHistoryRepository.findByMessageHistoryId(messageHistoryId)
                .orElseThrow(() -> new ControlledException(MESSAGE_HISTORY_NOT_FOUND));

        return messageHistory;
    }

    public List<MessageHistory> readAll(Long userId) {
        var user = userService.findById(userId);

        var messageHistory = messageHistoryRepository.findByUser(user)
                .orElseThrow(()-> new ControlledException(MESSAGE_HISTORY_NOT_FOUND));

        return messageHistory;
    }

    /**
     * 월간 메세지 정보를 조회
     */
    public List<Boolean> readAllAboutMonth(Integer month, Long userId) {
        var messageHistories = readAll(userId);

        List<Boolean> list = new ArrayList<>(Collections.nCopies(32, false));

        for(var messageHistory : messageHistories) {
            if(messageHistory.getCreatedAt().getMonth().getValue()==month) {
                list.set(messageHistory.getCreatedAt().getDayOfMonth(), true);
            }
        }
        return list;
    }

    /**
     * 일간 메세지 정보를 조회
     */
    public List<MessageHistory> readAllAboutDate(LocalDate localDate, Long userId) {
        var user = userService.findById(userId);

        var startOfDay = localDate.atStartOfDay(); // 날짜의 시작
        var endOfDay = localDate.atTime(LocalTime.MAX); // 날짜의 끝
        var messageHistories = messageHistoryRepository.findByUserAndCreatedAtBetween(user, startOfDay, endOfDay);

        return messageHistories;
    }

    public MessageHistory delete(Long messageHistoryId) {
        var messageHistory = read(messageHistoryId);

        messageHistoryRepository.deleteByMessageHistoryId(messageHistoryId);
        return messageHistory;
    }
}
