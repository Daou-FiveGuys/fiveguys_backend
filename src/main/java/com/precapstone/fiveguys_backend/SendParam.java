package com.precapstone.fiveguys_backend;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SendParam {
    String fromNumber;
    String toNumber;
    String filePath;
}
