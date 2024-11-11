package com.precapstone.fiveguys_backend.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class ChatbotDTO {
    private String message;
    private int ai_model_id;
}
