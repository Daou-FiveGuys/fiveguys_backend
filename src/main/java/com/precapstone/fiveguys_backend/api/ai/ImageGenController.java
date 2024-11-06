package com.precapstone.fiveguys_backend.api.ai;

import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.dto.ImageInpaintDTO;
import com.precapstone.fiveguys_backend.api.dto.ImagePromptDTO;
import com.precapstone.fiveguys_backend.api.dto.ImageUpscaleDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.auth.JwtFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO 이미지 업로드 기능
@Tag(name = "Image Generate", description = "이미지 생성, 수정")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai/image")
public class ImageGenController {
    private final ImageGenService imageGenService;

    @PostMapping("/generate")
    public ResponseEntity<CommonResponse> generateImage(@RequestHeader("Authorization") String authorization, @RequestBody ImagePromptDTO imagePromptDTO) {
        String accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");
        return ResponseEntity.ok(imageGenService.generate(accessToken, imagePromptDTO.getPrompt()));
    }

    @PostMapping("/inpaint")
    public ResponseEntity<CommonResponse> inpaint(@RequestHeader("Authorization") String authorization, @RequestBody ImageInpaintDTO imageInpaintDTO) {
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        return ResponseEntity.ok(imageGenService.inpaint(accessToken, imageInpaintDTO));
    }

    @PostMapping("/upscale")
    public ResponseEntity<CommonResponse> upscale(@RequestHeader("Authorization") String authorization, @RequestBody ImageUpscaleDTO imageUpscaleDTO) {
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        return ResponseEntity.ok(imageGenService.upscale(accessToken, imageUpscaleDTO));
    }
}
