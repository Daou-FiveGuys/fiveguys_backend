package com.precapstone.fiveguys_backend.member;

import com.precapstone.fiveguys_backend.auth.OAuth2UserInfo;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.ResponseMessage;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Getter
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public CommonResponse login(String userId, String password) {
        Optional<User> optionalMember = userRepository.findByUserId(userId);
        if(optionalMember.isPresent()) {
            User user = optionalMember.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return CommonResponse.builder()
                        .code(200)
                        .message(ResponseMessage.SUCCESS)
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
    public User register(String email, String name, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        String userId = "fiveguys_" + email;

        //TODO 이메일이 이미 존재하면 가입 방지 처리
        Optional<User> optionalMember = userRepository.findByUserId(userId);
        return optionalMember.orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .emailVerified(false)
                    .password(encodedPassword)
                    .provider("fiveguys")
                    .name(name)
                    .userId(userId)
                    .build();
            return userRepository.save(newUser);
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
    public User register(OAuth2UserInfo oAuth2UserInfo, String provider) {
        String providerId = oAuth2UserInfo.getProviderId();
        String userId = provider + "_" + providerId;
        String name = oAuth2UserInfo.getName();
        String email = oAuth2UserInfo.getProviderEmail();

        Optional<User> optionalMember = userRepository.findByUserId(userId);
        return optionalMember.orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .emailVerified(true)
                            .password(null) // 비밀번호는 null로 설정
                            .provider(provider)
                            .name(name)
                            .userId(userId)
                            .build();
                    return userRepository.save(newUser);
                });
    }
}
