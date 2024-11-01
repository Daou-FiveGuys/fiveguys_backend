package com.precapstone.fiveguys_backend;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.message.auth.PpurioAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test/")
@RequiredArgsConstructor
public class TestController {
    private final PpurioAuth ppurioAuth;

    @GetMapping("test")
    public CommonResponse test() {
        String result = ppurioAuth.createPost();
        return CommonResponse.builder().code(200).message("테스트 성공").data(result).build();
    }
}
