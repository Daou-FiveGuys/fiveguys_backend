package com.precapstone.fiveguys_backend.api.messagehistory;

import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.entity.messagehistory.MessageHistory;
import com.precapstone.fiveguys_backend.entity.messagehistory.MessageType;
import com.precapstone.fiveguys_backend.entity.SendImage;
import com.precapstone.fiveguys_backend.api.sendimage.SendImageService;
import com.precapstone.fiveguys_backend.api.user.UserService;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.precapstone.fiveguys_backend.exception.errorcode.MessageHistoryErrorCode.MESSAGE_HISTORY_NOT_FOUND;
import static com.precapstone.fiveguys_backend.exception.errorcode.UserErrorCode.USER_AUTHORIZATION_FAILED;
import static com.precapstone.fiveguys_backend.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MessageHistoryService {
    private final MessageHistoryRepository messageHistoryRepository;
    private final UserService userService;
    private final SendImageService sendImageService;
    private final JwtTokenProvider jwtTokenProvider;

    public MessageHistory create(MessageHistoryDTO messageHistoryDTO) {
        // TODO: 1. [예외처리] 전화번호 서식이 틀린 경우

        // TODO: 2. [예외처리] 본문 길이 틀린 경우

        var user = userService.findByUserId(messageHistoryDTO.getUserId())
                .orElseThrow(()->new ControlledException(USER_NOT_FOUND));

        var messageHistory = MessageHistory.builder()
                .messageType(messageHistoryDTO.getMessageType())
                .fromNumber(messageHistoryDTO.getFromNumber())
                .contact2s(new ArrayList<>(messageHistoryDTO.getContact2s()))
                .subject(messageHistoryDTO.getSubject())
                .content(messageHistoryDTO.getContent())
                .user(user)
                .build();

        // MMS인 경우에만 전달 받음
        SendImage sendImage;
        if(messageHistoryDTO.getMessageType() == MessageType.MMS) {
            sendImage = sendImageService.create(messageHistory, messageHistoryDTO.getSendImage());
            messageHistory.setSendImage(sendImage);
        }

        messageHistoryRepository.save(messageHistory);
        return messageHistory;
    }

    public MessageHistory createLink(MessageHistoryDTO messageHistoryDTO, String url) {
        // TODO: 1. [예외처리] 전화번호 서식이 틀린 경우

        // TODO: 2. [예외처리] 본문 길이 틀린 경우

        var user = userService.findByUserId(messageHistoryDTO.getUserId())
                .orElseThrow(()->new ControlledException(USER_NOT_FOUND));

        var messageHistory = MessageHistory.builder()
                .messageType(messageHistoryDTO.getMessageType())
                .fromNumber(messageHistoryDTO.getFromNumber())
                .contact2s(new ArrayList<>(messageHistoryDTO.getContact2s()))
                .subject(messageHistoryDTO.getSubject())
                .content(messageHistoryDTO.getContent())
                .user(user)
                .build();

        sendImageService.createLink(messageHistory, url);

        messageHistoryRepository.save(messageHistory);
        return messageHistory;
    }

    public MessageHistory read(Long messageHistoryId, String accessToken) {
        var messageHistory = messageHistoryRepository.findByMessageHistoryId(messageHistoryId)
                .orElseThrow(() -> new ControlledException(MESSAGE_HISTORY_NOT_FOUND));

        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if(!messageHistory.getUser().getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        return messageHistory;
    }

    public List<MessageHistory> readAll(String accessToken) {
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        var user = userService.findByUserId(userId)
                .orElseThrow(() -> new ControlledException(USER_NOT_FOUND));

        var messageHistory = messageHistoryRepository.findByUser(user)
                .orElseThrow(()-> new ControlledException(MESSAGE_HISTORY_NOT_FOUND));

        return messageHistory;
    }

    /**
     * 월간 메세지 정보를 조회
     */
    public List<Boolean> readAllAboutMonth(LocalDate localDate, String accessToken) {
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        var user = userService.findByUserId(userId)
                .orElseThrow(() -> new ControlledException(USER_NOT_FOUND));

        var startOfDay = localDate.withDayOfMonth(1).atStartOfDay(); // 월의 시작
        var endOfDay = localDate.withDayOfMonth(localDate.lengthOfMonth()).atTime(LocalTime.MAX); // 월의 끝
        var messageHistories = messageHistoryRepository.findByUserAndCreatedAtBetween(user, startOfDay, endOfDay);

        List<Boolean> list = new ArrayList<>(Collections.nCopies(32, false));

        for(var messageHistory : messageHistories)
            list.set(messageHistory.getCreatedAt().getDayOfMonth(), true);

        return list;
    }

    /**
     * 일간 메세지 정보를 조회
     */
    public List<MessageHistory> readAllAboutDate(LocalDate localDate, String accessToken) {
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        var user = userService.findByUserId(userId)
                .orElseThrow(() -> new ControlledException(USER_NOT_FOUND));

        var startOfDay = localDate.atStartOfDay(); // 날짜의 시작
        var endOfDay = localDate.atTime(LocalTime.MAX); // 날짜의 끝
        var messageHistories = messageHistoryRepository.findByUserAndCreatedAtBetween(user, startOfDay, endOfDay);

        return messageHistories;
    }

    public MessageHistory delete(Long messageHistoryId, String accessToken) {
        var messageHistory = read(messageHistoryId, accessToken);

        messageHistoryRepository.deleteByMessageHistoryId(messageHistoryId);
        return messageHistory;
    }
}
