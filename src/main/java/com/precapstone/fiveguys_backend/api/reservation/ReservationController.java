package com.precapstone.fiveguys_backend.api.reservation;

import com.precapstone.fiveguys_backend.api.messagehistory.MessageHistoryService;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.auth.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservation/")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final MessageHistoryService messageHistoryService;

    // 전체 예약 조회
    @GetMapping
    public ResponseEntity read(@RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var messageHistories = messageHistoryService.read(accessToken);
        var reservations = reservationService.readAll(messageHistories);

        var response = CommonResponse.builder().code(200).message("전체 예약 조회 성공").data(reservations).build();
        return ResponseEntity.ok(response);
    }
}
