package com.precapstone.fiveguys_backend.api.image;

import com.precapstone.fiveguys_backend.api.dto.ImageInpaintDTO;
import com.precapstone.fiveguys_backend.api.dto.ImageRequestDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Image Generate", description = "이미지 조회, 생성, 수정, 업스케일링")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai/image")
public class ImageController {
    private final ImageService imageService;
    private final OctetStreamReadMsgConverter octetStreamReadMsgConverter;

    /**
     * 이미지 생성 컨트롤러
     * @param authorization 인증 헤더
     * @return ResponseEntity<CommonResponse> 이미지 정보
     */
    @PostMapping("/generate")
    public ResponseEntity<CommonResponse> generateImage(@RequestHeader("Authorization") String authorization, @RequestBody ImageGenerateDTO imageGenerateDTO) {
        return ResponseEntity.ok(imageService.generate(authorization, imageGenerateDTO));
    }
    /**
     * 이미지 생성 컨트롤러
     * @param authorization 인증 헤더
     * @return ResponseEntity<CommonResponse> 이미지 정보
     */
    @PostMapping("/generate-lora")
    public ResponseEntity<CommonResponse> generateImageWithLora(@RequestHeader("Authorization") String authorization, @RequestBody ImageGenerateLoraDTO imageGenerateLoraDTO) {
        return ResponseEntity.ok(imageService.generateWithLora(authorization, imageGenerateLoraDTO));
    }


    //TODO 되돌리기 기능
    /**
     * 이미지 인페인팅(수정) 컨트롤러
     * @param authorization 인증 헤더
     * @param imageInpaintDTO 이미지 아이디, 프롬프트 (KOR)
     * @return ResponseEntity<CommonResponse> 이미지 정보
     */

    @PostMapping(value = "/inpaint", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse> inpaint(@RequestHeader("Authorization") String authorization,
                                                  @RequestPart ImageInpaintDTO imageInpaintDTO,
                                                  @RequestPart MultipartFile multipartFile) {
        return ResponseEntity.ok(imageService.inpaint(authorization, imageInpaintDTO, multipartFile));
    }

    /**
     * 이미지 업스케일 컨트롤러
     * @param authorization 인증 헤더
     * @return ResponseEntity<CommonResponse> 이미지 정보
     */
    @PostMapping("/upscale")
    public ResponseEntity<CommonResponse> upscale(@RequestHeader("Authorization") String authorization, @RequestBody ImageRequestDTO imageRequestDTO) {
        return ResponseEntity.ok(imageService.upscale(authorization, imageRequestDTO.getRequestId()));
    }

    @PostMapping("/remove-text/imggen")
    public ResponseEntity<CommonResponse> removeTextImggen(@RequestHeader("Authorization") String authorization, @RequestBody ImageRequestDTO imageRequestDTO) {
        return ResponseEntity.ok(imageService.removeTextByImggen(authorization, imageRequestDTO.getRequestId()));
    }

    @PostMapping("/remove-text/photoroom")
    public ResponseEntity<CommonResponse> removeTextPhotoroom(@RequestHeader("Authorization") String authorization, @RequestBody ImageRequestDTO imageRequestDTO) {
        return ResponseEntity.ok(imageService.removeTextByPhotoRoom(authorization, imageRequestDTO.getRequestId()));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<CommonResponse> getImage(@RequestHeader("Authorization") String authorization, @PathVariable("requestId") String requestId) {
        return ResponseEntity.ok(imageService.getImage(authorization, requestId));
    }

    @GetMapping
    public ResponseEntity<CommonResponse> getAllImages(@RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(imageService.getAllImages(authorization));
    }
}
