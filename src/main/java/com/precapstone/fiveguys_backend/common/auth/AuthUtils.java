package com.precapstone.fiveguys_backend.common.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    // 현재 로그인한 사용자 정보 가져오기
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // 로그인 상태인지 확인
    public static boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal().equals("anonymousUser"));
    }
}