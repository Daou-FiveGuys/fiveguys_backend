package com.precapstone.fiveguys_backend.message;

import com.precapstone.fiveguys_backend.message.send.PpurioSendParam;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.message.auth.PpurioAuth;
import com.precapstone.fiveguys_backend.message.send.PpurioSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/ppurio/")
@RequiredArgsConstructor
public class PpurioSendController {
    private final PpurioAuth ppurioAuth;
    private final PpurioSendService ppurioSendService;

    @PostMapping("auth")
    public CommonResponse test() {
        String result = ppurioAuth.createPost();
        return CommonResponse.builder().code(200).message("테스트 성공").data(result).build();
    }

    @PostMapping("send")
    public CommonResponse send(@RequestBody PpurioSendParam ppurioSendParam) throws IOException {
        System.out.println(ppurioSendParam.getTargets().get(0).get());
        ppurioSendService.sendMessage(ppurioSendParam);
        return CommonResponse.builder().code(200).message("테스트 성공").build();
    }
}
