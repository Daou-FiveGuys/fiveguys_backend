package com.precapstone.fiveguys_backend.api.amountused;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.auth.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/amountController/")
public class AmountUsedController {
    private final AmountUsedService amountUsedService;

    // 사용량 조회
    @GetMapping
    public ResponseEntity read(@RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var amountUsed = amountUsedService.read(accessToken);

        var response = CommonResponse.builder().code(200).message("사용량 조회 성공").data(amountUsed).build();
        return ResponseEntity.ok(response);
    }

    // 일일 사용량 조회
    @GetMapping("day/{localDate}")
    public ResponseEntity readByDay(@RequestHeader("Authorization") String authorization, @PathVariable LocalDate localDate) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var dailyAmount = amountUsedService.readByDay(accessToken, localDate);

        var response = CommonResponse.builder().code(200).message("일일 사용량 조회 성공").data(dailyAmount).build();
        return ResponseEntity.ok(response);
    }
}
