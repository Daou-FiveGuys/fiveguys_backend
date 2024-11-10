package com.precapstone.fiveguys_backend.api.nlp;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "NLP", description = "이미지 프롬프팅, 문장 구체화, 주요 문장 추출")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai/gpt")
public class GptController {
    private final GptService gptService;
    @PostMapping("/generate-text")
    public ResponseEntity<CommonResponse> makeText(@RequestHeader("Authorization") String authorization, @RequestBody NlpParamDTO nlpParamDTO) {
        return ResponseEntity.ok(gptService.makeText(authorization, nlpParamDTO.getText()));
    }

    @PostMapping("/generate-image-prompt")
    public ResponseEntity<CommonResponse> generateImagePrompt(@RequestHeader("Authorization") String authorization, @RequestBody NlpParamDTO nlpParamDTO) {
        return ResponseEntity.ok(gptService.generateImagePrompt(authorization, nlpParamDTO.getText()));
    }

    @PostMapping("/extract-key-points")
    public ResponseEntity<CommonResponse> extractKeyPoints(@RequestHeader("Authorization") String authorization, @RequestBody NlpParamDTO nlpParamDTO) {
        return ResponseEntity.ok(gptService.extractKeyPoints(authorization, nlpParamDTO.getText()));
    }
}
