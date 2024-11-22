package com.precapstone.fiveguys_backend.api.amountused;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.auth.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/amountController/")
public class AmountUsedController {
    private final AmountUsedService amountUsedService;

    // 사용량 조회
    @GetMapping("/{amountUsedId}")
    public ResponseEntity read(@PathVariable Long amountUsedId, @RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var amountUsed = amountUsedService.read(amountUsedId, accessToken);

        var response = CommonResponse.builder().code(200).message("사용량 조회 성공").data(amountUsed).build();
        return ResponseEntity.ok(response);
    }
}
