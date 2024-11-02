package com.precapstone.fiveguys_backend.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {
    GOOGLE("GOOGLE"),
    NAVER("NAVER"),
    FIVEGUYS("FIVEGUYS");
    private final String type;
}
