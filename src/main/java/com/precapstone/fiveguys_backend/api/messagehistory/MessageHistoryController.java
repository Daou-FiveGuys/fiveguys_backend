package com.precapstone.fiveguys_backend.api.messagehistory;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.auth.JwtFilter;
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
    public ResponseEntity read(@PathVariable Long messageHistoryId, @RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var messageHistory = messageHistoryService.read(messageHistoryId, accessToken);
        var response = CommonResponse.builder().code(200).message("문자 기록 조회 성공").data(messageHistory).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity readAll(@RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var messageHistories = messageHistoryService.readAll(accessToken);
        var response = CommonResponse.builder().code(200).message("문자 기록 조회 성공").data(messageHistories).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/month/{localDate}")
    public ResponseEntity readMonth(@PathVariable LocalDate localDate, @RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var messageHistoriesYN = messageHistoryService.readAllAboutMonth(localDate, accessToken);
        var response = CommonResponse.builder().code(200).message("문자 기록 조회 성공").data(messageHistoriesYN).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("date/{localDate}")
    public ResponseEntity readDate(@PathVariable LocalDate localDate, @RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var messageHistories = messageHistoryService.readAllAboutDate(localDate, accessToken);
        var response = CommonResponse.builder().code(200).message("문자 기록 조회 성공").data(messageHistories).build();
        return ResponseEntity.ok(response);
    }

    // 문자 기록 삭제
    @DeleteMapping("/{messageHistoryId}")
    public ResponseEntity delete(@PathVariable Long messageHistoryId, @RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var messageHistory = messageHistoryService.delete(messageHistoryId, accessToken);
        var response = CommonResponse.builder().code(200).message("문자 기록 삭제 성공").data(messageHistory).build();
        return ResponseEntity.ok(response);
    }
}
