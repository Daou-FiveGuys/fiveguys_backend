package com.precapstone.fiveguys_backend.api.message;

import com.precapstone.fiveguys_backend.api.dto.PpurioSendDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.api.message.auth.PpurioAuth;
import com.precapstone.fiveguys_backend.api.message.send.PpurioSendService;
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

    /**
     * 이미지를 전송하는 서비스
     * 
     * @param ppurioSendDTO 서비스를 이용하기 위한 정보가 담겨있는 객체
     *
     * @return 응답 정보 전달 200(성공)
     *
     * @throws IOException
     */
    @PostMapping("send")
    public CommonResponse send(@RequestBody PpurioSendDTO ppurioSendDTO) throws IOException {
        System.out.println(ppurioSendDTO.getTargets().get(0).get());
        ppurioSendService.sendMessage(ppurioSendDTO);
        return CommonResponse.builder().code(200).message("테스트 성공").build();
    }
}
