package com.precapstone.fiveguys_backend.api.user;

import com.precapstone.fiveguys_backend.api.auth.AuthService;
import com.precapstone.fiveguys_backend.api.dto.AuthResponseDTO;
import com.precapstone.fiveguys_backend.api.dto.UserDTO;
import com.precapstone.fiveguys_backend.api.email.MailService;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.PasswordValidator;
import com.precapstone.fiveguys_backend.common.enums.UserRole;
import com.precapstone.fiveguys_backend.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MailService mailService;

    public Optional<User> findByUserId(String userId){
        return userRepository.findByUserId(userId);
    }

    /**
     * FiveGuys 일반 회원가입
     * @param userDTO
     *
     * @return CommonResponse 회원가입 여부
     */
    @Transactional
    public CommonResponse register(UserDTO userDTO) {
        String result = checkPasswordValidation(userDTO.getPassword(), userDTO.getConfirmPassword());
        if(result != null){
            return CommonResponse.builder()
                        .code(400)
                        .message(result)
                        .build();
        }

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        String userId = "fiveguys_" + userDTO.getEmail();

        /**
         * 가입 여부 확인
         */
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isPresent()) {
            return CommonResponse.builder()
                    .code(500)
                    .message("Registered Failed: Already Registered Email")
                    .data(optionalUser.get().getEmail())
                    .build();
        }

        LocalDateTime now = LocalDateTime.now();
        /**
         * 신규 회원
         */
        User newUser = User.builder()
                .email(userDTO.getEmail())
                .password(encodedPassword)
                .provider("fiveguys")
                .name(userDTO.getName())
                .userRole(UserRole.VISITOR)
                .createdAt(now)
                .updatedAt(now)
                .userId(userId)
                .build();

        userRepository.save(newUser);
        mailService.sendWelcomeEmail(newUser.getEmail());
        Map<String, String> tokens = authService.usersAuthorization(newUser);
        return CommonResponse.builder()
                .code(200)
                .message("Registered Success")
                .data(AuthResponseDTO.builder()
                        .accessToken(tokens.get("access_token"))
                        .build()
                )
                .build();
    }

    public CommonResponse emailExists(String email) {
        //TODO 소셜 계정 + 일반 계정 이메일 중복 허용
        Optional<User> optionalUser = userRepository.findByUserId("fiveguys_" + email);
        if(optionalUser.isPresent()){
            return CommonResponse.builder()
                    .code(400)
                    .message("Email Already Exists")
                    .build();
        }
        return CommonResponse.builder()
                .code(200)
                .build();
    }

    @Transactional
    public CommonResponse deleteByUserId(String userId, String password) {
        //TODO 검증로직 추가
        User user = userRepository.findByUserId(userId).orElseThrow();
        return CommonResponse.builder()
                .code(200)
                .message("User deleted successfully")
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