package com.precapstone.fiveguys_backend.api.member;

import com.precapstone.fiveguys_backend.api.dto.MemberDTO;
import com.precapstone.fiveguys_backend.common.PasswordValidator;
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
    public CommonResponse login(String email, String password) {
        Optional<Member> optionalMember = memberRepository.findByUserId(email);
        if(optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (passwordEncoder.matches(password, member.getPassword())) {
                String responseMessage = null;
                if (member.getUserRole() == UserRole.USER)
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

        return CommonResponse.builder()
                .code(200)
                .message("Registered Successfully")
                .data(newMember)
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
