package com.precapstone.fiveguys_backend.api.papago;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/translation")
public class PapagoTranslationController {
    private final PapagoTranslationService papagoTranslationService;

    public PapagoTranslationController(PapagoTranslationService papagoTranslationService) {
        this.papagoTranslationService = papagoTranslationService;
    }

    @PostMapping
    public ResponseEntity translateText(@RequestBody TranslationRequest request) {
        return ResponseEntity.ok(papagoTranslationService.translate(request.getText(), request.getSource(), request.getTarget()));
    }
}

class TranslationRequest {
    private String text;
    private String source; // 원본 언어 코드
    private String target; // 대상 언어 코드

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}

class TranslationResponse {
    private String translatedText;

    public TranslationResponse(String translatedText) {
        this.translatedText = translatedText;
    }

    // Getter
    public String getTranslatedText() {
        return translatedText;
    }
}
