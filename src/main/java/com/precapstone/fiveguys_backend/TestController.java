package com.precapstone.fiveguys_backend;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.message.auth.PpurioAuth;
import com.precapstone.fiveguys_backend.message.send.PpurioSend;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/test/")
@RequiredArgsConstructor
public class TestController {
    private final PpurioAuth ppurioAuth;
    private final PpurioSend ppurioSend;

    @PostMapping("test/auth")
    public CommonResponse test() {
        String result = ppurioAuth.createPost();
        return CommonResponse.builder().code(200).message("테스트 성공").data(result).build();
    }

    @PostMapping("test/mms")
    public CommonResponse send(@RequestBody SendParam sendParam) throws IOException {
        ppurioSend.sendMessage(sendParam.fromNumber, sendParam.getToNumber() , "./test.jpg");
        return CommonResponse.builder().code(200).message("테스트 성공").build();
    }
}
