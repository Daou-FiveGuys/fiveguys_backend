package com.precapstone.fiveguys_backend.message.auth;

import lombok.Getter;

/**
 * 뿌리오 메세징 서비스를 이용하기 위한 인증 토큰
 * 각 서비스의 헤더에 추가해야한다.
 *
 */
@Getter
public class AccessTocken {
    // 인증 토큰
    private final String tocken;

    // Bearer 고정
    private final String type;

    // 토큰 만료 시간 [yyyyMMddHHmmss]
    private final String expired;

    /**
     *
     * @param token 인증 토큰
     * @param type Bearer 고정
     * @param expired 토큰 만료 시간 [yyyyMMddHHmmss]
     */
    public AccessTocken(String token, String type, String expired) {
        this.tocken = token;
        this.type = type;
        this.expired = expired;
    }
}