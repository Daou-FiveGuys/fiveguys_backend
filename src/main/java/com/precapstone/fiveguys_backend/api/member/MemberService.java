package com.precapstone.fiveguys_backend.api.member;

import com.precapstone.fiveguys_backend.api.auth.AuthService;
import com.precapstone.fiveguys_backend.api.dto.AuthResponseDTO;
import com.precapstone.fiveguys_backend.api.dto.MemberDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.PasswordValidator;
import com.precapstone.fiveguys_backend.common.enums.UserRole;
import com.precapstone.fiveguys_backend.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<Member> findByUserId(String userId){
        return memberRepository.findByUserId(userId);
    }

    /**
     * FiveGuys 일반 회원가입
     * @param memberDTO
     *
     * @return CommonResponse 회원가입 여부
     */
    @Transactional
    public CommonResponse register(MemberDTO memberDTO) {
        String result = checkPasswordValidation(memberDTO.getPassword(), memberDTO.getConfirmPassword());
        if(result != null){
            return CommonResponse.builder()
                        .code(400)
                        .message(result)
                        .build();
        }

        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        String userId = "fiveguys_" + memberDTO.getEmail();

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
                .email(memberDTO.getEmail())
                .password(encodedPassword)
                .provider("fiveguys")
                .name(memberDTO.getName())
                .userRole(UserRole.VISITOR)
                .createdAt(now)
                .updatedAt(now)
                .userId(userId)
                .build();

        memberRepository.save(newMember);
        Map<String, String> tokens = authService.usersAuthorization(newMember);
        return CommonResponse.builder()
                .code(200)
                .message("Registered Success")
                .data(AuthResponseDTO.builder()
                        .accessToken(tokens.get("access_token"))
                        .build()
                )
                .build();
    }

    @Transactional
    public CommonResponse deleteByUserId(String userId, String password) {
        //TODO 검증로직 추가
        Member member = memberRepository.findByUserId(userId).orElseThrow();
        return CommonResponse.builder()
                .code(200)
                .message("Member deleted successfully")
                .build();
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
