package com.precapstone.fiveguys_backend.api.amountused;

import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.dailyamount.DailyAmountService;
import com.precapstone.fiveguys_backend.api.user.UserService;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.auth.JwtFilter;
import com.precapstone.fiveguys_backend.entity.AmountUsed;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.precapstone.fiveguys_backend.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/amountUsed/")
public class AmountUsedController {
    private final AmountUsedService amountUsedService;
    private final DailyAmountService dailyAmountService;

    // 사용량 조회
    @GetMapping
    public ResponseEntity read(@RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var amountUsed = amountUsedService.read(accessToken);
        var dailyAmounts = dailyAmountService.readAddByAmountUsed(amountUsed);
        amountUsed.setDailyAmounts(dailyAmounts); // TODO: 임시코드!! 조회수 많아서 정상 로직으로 수정해야함

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
