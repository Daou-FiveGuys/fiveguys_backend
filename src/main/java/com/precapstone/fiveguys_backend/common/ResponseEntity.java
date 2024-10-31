package com.precapstone.fiveguys_backend.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseEntity<T> {
    private int status;
    private String message;
    private T data;
}
