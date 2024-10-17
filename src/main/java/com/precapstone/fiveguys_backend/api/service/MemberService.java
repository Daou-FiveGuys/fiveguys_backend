package com.precapstone.fiveguys_backend.api.service;

import com.precapstone.fiveguys_backend.api.dto.MemberParam;
import com.precapstone.fiveguys_backend.api.repository.MemberRepository;
import com.precapstone.fiveguys_backend.common.PasswordValidator;
import com.precapstone.fiveguys_backend.common.auth.OAuth2UserInfo;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.ResponseMessage;
import com.precapstone.fiveguys_backend.common.enums.UserRole;
import com.precapstone.fiveguys_backend.entity.Member;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<Member> findByUserId(String userId){
        return memberRepository.findByUserId(userId);
    }

    @Transactional
    public CommonResponse verifiedEmail(String email) {
        Member member = memberRepository.findByUserId("fiveguys_"+email)
                        .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        member.setEmailVerified(true);
        member.setUserRole(UserRole.USER);
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);
        return CommonResponse.builder()
                .code(200)
                .data(member)
                .message("Email Verified Successfully")
                .build();
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
     * @param memberParam
     *
     * @return CommonResponse 회원가입 여부
     */
    @Transactional
    public CommonResponse register(MemberParam memberParam) {
        String result = checkPasswordValidation(memberParam.getPassword(), memberParam.getConfirmPassword());
        if(result != null){
            return CommonResponse.builder()
                        .code(400)
                        .message(result)
                        .build();
        }

        String encodedPassword = passwordEncoder.encode(memberParam.getPassword());
        String userId = "fiveguys_" + memberParam.getEmail();

        /**
         * 가입 여부 확인
         */
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        if (optionalMember.isPresent()) {
            return CommonResponse.builder()
                    .code(500)
                    .message("Registered Failed: Already Registered Email")
                    .data(optionalMember.get().getEmail())
                    .build();
        }

        LocalDateTime now = LocalDateTime.now();
        /**
         * 신규 회원
         */
        Member newMember = Member.builder()
                .email(memberParam.getEmail())
                .emailVerified(false)
                .password(encodedPassword)
                .provider("fiveguys")
                .name(memberParam.getName())
                .userRole(UserRole.VISITOR)
                .createdAt(now)
                .updatedAt(now)
                .userId(userId)
                .build();

        memberRepository.save(newMember);

        return CommonResponse.builder()
                .code(200)
                .message("Registered Successfully")
                .data(newMember)
                .build();
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


        LocalDateTime now = LocalDateTime.now();
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        return optionalMember.orElseGet(() -> Member.builder()
            .email(email)
            .emailVerified(true)
            .password(null)
            .provider(provider)
            .name(name)
            .createdAt(now)
            .updatedAt(now)
            .userRole(UserRole.USER)
            .userId(userId)
            .build());
    }

    private String checkPasswordValidation(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }

        if (!PasswordValidator.isValidPassword(password)) {
            return "Invalid password";
        }

        return null;
    }
}
