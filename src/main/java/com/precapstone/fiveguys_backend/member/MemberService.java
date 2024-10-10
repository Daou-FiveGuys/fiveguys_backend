package com.precapstone.fiveguys_backend.member;

import com.precapstone.fiveguys_backend.auth.OAuth2UserInfo;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.ResponseMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {
    @Getter
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public Member login(String userId, String password) {
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        optionalMember.ifPresent(member -> {
            if (passwordEncoder.matches(password, member.getPassword())) {
                if (!member.getEmailVerified()) {
                    //TODO 이메일 인증 필요
                } else {
                    //TODO 로그인
                }
            } else {
                throw new IllegalArgumentException("Invalid email or password");
            }
        });
        optionalMember.orElseThrow(() -> new EntityNotFoundException("Member not found"));
        return null;
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
