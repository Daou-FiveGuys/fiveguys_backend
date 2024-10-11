package com.precapstone.fiveguys_backend.api.service;

import com.precapstone.fiveguys_backend.api.repository.MemberRepository;
import com.precapstone.fiveguys_backend.common.auth.OAuth2UserInfo;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.ResponseMessage;
import com.precapstone.fiveguys_backend.entity.Member;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void verifiedEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByUserId("fiveguys_"+email);
        optionalMember.ifPresent(member -> {
            member.setEmailVerified(true);
            memberRepository.save(member);
        });
    }

    @Transactional
    public CommonResponse login(String userId, String password) {
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        if(optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (passwordEncoder.matches(password, member.getPassword())) {
                String responseMessage = null;
                if (member.getEmailVerified())
                    responseMessage = ResponseMessage.SUCCESS;
                else
                    responseMessage = ResponseMessage.EMAIL_VERIFICAITION_REQUIRED;
                return CommonResponse.builder()
                        .code(200)
                        .data(optionalMember.get())
                        .message(responseMessage)
                        .build();
            } else {
                return CommonResponse.builder()
                        .code(401)
                        .message("Invalid email or password")
                        .build();
            }
        }
        return CommonResponse.builder()
                .code(401)
                .message("Member not found")
                .build();
    }

    /**
     * FiveGuys 일반 회원가입
     *
     * @param email 이메일
     * @param name 가입자 이름
     * @param password 비밀번호
     * @return Member
     */
    @Transactional
    public Member register(String email, String name, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        String userId = "fiveguys_" + email;

        //TODO 이메일이 이미 존재하면 가입 방지 처리
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        return optionalMember.orElseGet(() -> {
            Member newMember = Member.builder()
                    .email(email)
                    .emailVerified(false)
                    .password(encodedPassword)
                    .provider("fiveguys")
                    .name(name)
                    .userId(userId)
                    .build();
            return memberRepository.save(newMember);
        });
    }

    /**
     * OAuth2 회원가입
     *
     * @param oAuth2UserInfo 사용자 정보 -> NaverUserInfo || GoogleUserInfo
     * @param provider oAuth2 제공 기업 -> naver || google
     * @return Member
     */
    @Transactional
    public Member register(OAuth2UserInfo oAuth2UserInfo, String provider) {
        String providerId = oAuth2UserInfo.getProviderId();
        String userId = provider + "_" + providerId;
        String name = oAuth2UserInfo.getName();
        String email = oAuth2UserInfo.getProviderEmail();

        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        return optionalMember.orElseGet(() -> {
                    Member newMember = Member.builder()
                            .email(email)
                            .emailVerified(true)
                            .password(null) // 비밀번호는 null로 설정
                            .provider(provider)
                            .name(name)
                            .userId(userId)
                            .build();
                    return memberRepository.save(newMember);
                });
    }
}
