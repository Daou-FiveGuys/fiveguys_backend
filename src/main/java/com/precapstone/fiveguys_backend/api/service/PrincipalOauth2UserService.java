package com.precapstone.fiveguys_backend.api.service;

import com.precapstone.fiveguys_backend.common.auth.GoogleUserInfo;
import com.precapstone.fiveguys_backend.common.auth.NaverUserInfo;
import com.precapstone.fiveguys_backend.common.auth.OAuth2UserInfo;
import com.precapstone.fiveguys_backend.common.auth.PrincipalDetails;
import com.precapstone.fiveguys_backend.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberService memberService;

    @Value("${app.oauth2.password}")
    private String oauth2Password;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = switch (provider.toLowerCase()) {
            case "naver" -> new NaverUserInfo(oAuth2User.getAttributes());
            case "google" -> new GoogleUserInfo(oAuth2User.getAttributes());
            default -> throw new OAuth2AuthenticationException("Unsupported provider");
        };

        Member member = memberService.register(oAuth2UserInfo, provider);
        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }
}
