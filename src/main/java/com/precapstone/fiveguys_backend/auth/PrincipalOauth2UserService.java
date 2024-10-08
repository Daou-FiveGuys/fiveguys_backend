package com.precapstone.fiveguys_backend.auth;

import com.precapstone.fiveguys_backend.member.Member;
import com.precapstone.fiveguys_backend.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${app.oauth2.password}")
    private String oauth2Password;

    @Autowired
    private MemberRepository memberRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = switch(provider.toLowerCase()){
            case "naver" -> new NaverUserInfo(oAuth2User.getAttributes());
//            case "google" -> new
            default -> null;
        };

        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId; // "ex) google_xxxxxxxx"
        String password = bCryptPasswordEncoder.encode(oauth2Password);
        String email = oAuth2UserInfo.getProviderEmail();

        Member member = memberRepository.findByUsername(username).orElse(null);
        if (member == null) {
            member = Member.builder()
                    .providerId(providerId)
                    .email(email)
                    .password(password)
                    .provider(provider)
                    .username(username)
                    .build();
            memberRepository.save(member);
        } else {
            System.out.println("Member already exists");
        }

        return new PrincipalDetails(member,oAuth2User.getAttributes());
    }
}
