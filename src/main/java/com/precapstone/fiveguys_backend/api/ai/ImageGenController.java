package com.precapstone.fiveguys_backend.api.ai;

import com.precapstone.fiveguys_backend.api.dto.ImagePromptDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.auth.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai")
public class ImageGenController {
    private final ImageGenService imageGenService;
    @PostMapping
    public ResponseEntity<CommonResponse> generateImage(@RequestHeader("Authorization") String authorization, @RequestBody ImagePromptDTO imagePromptDTO) {
        String accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");
        return ResponseEntity.ok(imageGenService.generate(accessToken, imagePromptDTO.getPrompt()));
    }
}
