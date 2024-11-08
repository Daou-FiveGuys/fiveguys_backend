package com.precapstone.fiveguys_backend.api.ai;

import com.precapstone.fiveguys_backend.api.dto.ImageInpaintDTO;
import com.precapstone.fiveguys_backend.api.dto.ImagePromptDTO;
import com.precapstone.fiveguys_backend.api.dto.ImageUpscaleDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Image Generate", description = "이미지 생성, 수정")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai/image")
public class ImageGenController {
    private final ImageGenService imageGenService;

    /**
     * 이미지 생성 컨트롤러
     * @param authorization 인증 헤더
     * @param imagePromptDTO 이미지 생성 프롬프트 (KOR)
     * @return ResponseEntity<CommonResponse> 이미지 정보
     */
    @PostMapping("/generate")
    public ResponseEntity<CommonResponse> generateImage(@RequestHeader("Authorization") String authorization, @RequestBody ImagePromptDTO imagePromptDTO) {
        return ResponseEntity.ok(imageGenService.generate(authorization, imagePromptDTO.getPrompt()));
    }

    //TODO 되돌리기 기능
    /**
     * 이미지 인페인팅(수정) 컨트롤러
     * @param authorization 인증 헤더
     * @param imageInpaintDTO 이미지 아이디, 프롬프트 (KOR)
     * @return ResponseEntity<CommonResponse> 이미지 정보
     */
    @PostMapping("/inpaint")
    public ResponseEntity<CommonResponse> inpaint(@RequestHeader("Authorization") String authorization, @ModelAttribute ImageInpaintDTO imageInpaintDTO) {
        return ResponseEntity.ok(imageGenService.inpaint(authorization, imageInpaintDTO));
    }

    /**
     * 이미지 업스케일 컨트롤러
     * @param authorization 인증 헤더
     * @param imageUpscaleDTO 이미지 아이디
     * @return ResponseEntity<CommonResponse> 이미지 정보
     */
    @PostMapping("/upscale")
    public ResponseEntity<CommonResponse> upscale(@RequestHeader("Authorization") String authorization, @RequestBody ImageUpscaleDTO imageUpscaleDTO) {
        return ResponseEntity.ok(imageGenService.upscale(authorization, imageUpscaleDTO));
    }
}
