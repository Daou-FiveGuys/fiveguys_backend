package com.precapstone.fiveguys_backend.api.chatbot;

import com.precapstone.fiveguys_backend.api.dto.ChatbotDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatbot")
public class ChatbotController {
    private final ChatbotService chatbotService;

    @PostMapping
    public ResponseEntity<CommonResponse> getChatbotAnswer(@RequestBody ChatbotDTO chatbotDTO) throws IOException {

        return ResponseEntity.ok(chatbotService.getChatbotAnswer(chatbotDTO.getMessage()));
    }

}
