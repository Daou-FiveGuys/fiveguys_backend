package com.precapstone.fiveguys_backend.api.messagehistory;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/messageHistory/")
@RequiredArgsConstructor
public class MessageHistoryController {
    private final MessageHistoryService messageHistoryService;

    // 문자 기록 조회
    @GetMapping("/{messageHistoryId}")
    public ResponseEntity read(@PathVariable Long messageHistoryId) {
        var messageHistory = messageHistoryService.read(messageHistoryId);

        var response = CommonResponse.builder().code(200).message("문자 기록 조회 성공").data(messageHistory).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity readAll(@PathVariable Long userId) {
        var messageHistories = messageHistoryService.readAll(userId);

        var response = CommonResponse.builder().code(200).message("문자 기록 조회 성공").data(messageHistories).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/month/{month}")
    public ResponseEntity readMonth(@PathVariable Integer month, @PathVariable Long userId) {
        var messageHistoriesYN = messageHistoryService.readAllAboutMonth(month, userId);

        var response = CommonResponse.builder().code(200).message("문자 기록 조회 성공").data(messageHistoriesYN).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("date/{date}")
    public ResponseEntity readDate(@PathVariable LocalDate localDate, @PathVariable Long userId) {
        var messageHistories = messageHistoryService.readAllAboutDate(localDate, userId);

        var response = CommonResponse.builder().code(200).message("문자 기록 조회 성공").data(messageHistories).build();
        return ResponseEntity.ok(response);
    }

    // 문자 기록 삭제
    @DeleteMapping("/{messageHistoryId}")
    public ResponseEntity delete(@PathVariable Long messageHistoryId) {
        var messageHistory = messageHistoryService.delete(messageHistoryId);

        var response = CommonResponse.builder().code(200).message("문자 기록 삭제 성공").data(messageHistory).build();
        return ResponseEntity.ok(response);
    }
}
