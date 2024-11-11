package com.precapstone.fiveguys_backend.api.user;

import com.precapstone.fiveguys_backend.api.auth.AuthService;
import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.dto.AuthResponseDTO;
import com.precapstone.fiveguys_backend.api.dto.UserDTO;
import com.precapstone.fiveguys_backend.api.dto.UserInfoResponseDTO;
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
    private final JwtTokenProvider jwtTokenProvider;

    public Optional<User> findByUserId(String userId){
        return userRepository.findByUserId(userId);
    }
    String [] emails = {"6ukeem@gmail.com", "2071335@hansung.ac.kr", "2071112@hansung.ac.kr" , "2071013@hansung.ac.kr" , "imbanana15@gmail.com"};
    /**
     * FiveGuys 일반 회원가입
     * @param userDTO
     *
     * @return CommonResponse 회원가입 여부
     */
    @Transactional
    public CommonResponse register(UserDTO userDTO) {
        boolean flag = false;
        for(String email : emails){
            if (email.equals(userDTO.getEmail())) {
                flag = true;
                break;
            }
        }
        String result = checkPasswordValidation(userDTO.getPassword(), userDTO.getConfirmPassword());
        if(result != null || !flag){
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
        try {
            mailService.sendWelcomeEmail(newUser.getEmail());
        }catch (Exception e){
            e.printStackTrace();
        }
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

    public CommonResponse getUser(String authorization) {
        String token = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            return CommonResponse.builder()
                    .code(400)
                    .message("User not found")
                    .build();
        }
        User user = optionalUser.get();
        return CommonResponse.builder()
                .code(200)
                .data(UserInfoResponseDTO.builder()
                        .userRole(user.getUserRole())
                        .name(user.getName())
                        .email(user.getEmail())
                        .userId(user.getUserId()))
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
    public CommonResponse delete(String authorization, String email, String password) {
        String token = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if(userId == null || optionalUser.isEmpty()){
            return CommonResponse.builder()
                    .code(400)
                    .message("Bad request")
                    .build();
        }

        if(!userId.contains("fiveguys_")){
            if(userId.contains(email)){
                userRepository.delete(optionalUser.get());
                return CommonResponse.builder()
                        .code(200)
                        .message("Successfully Deleted User")
                        .build();
            }
            else {
                return CommonResponse.builder()
                        .code(400)
                        .message("Bad request")
                        .build();
            }
        }

        if(passwordEncoder.matches(password, optionalUser.get().getPassword())){
            return CommonResponse.builder()
                    .code(400)
                    .message("Bad request")
                    .build();
        }

        userRepository.delete(optionalUser.get());
        return CommonResponse.builder()
                .code(200)
                .message("User deleted successfully")
                .build();
    }

    @Transactional
    public CommonResponse edit(String authorization, UserDTO userDTO) {
        String token = jwtTokenProvider.getUserIdFromToken(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        if(!userId.contains("fiveguys_") || !userDTO.getPassword().equals(userDTO.getConfirmPassword())){
            return CommonResponse.builder()
                    .code(400)
                    .message("Bad request")
                    .build();
        }


        User user = userRepository.findByUserId(userId).orElseThrow();
        user.setUpdatedAt(LocalDateTime.now());
        user.setPassword(userDTO.getPassword());
        userRepository.save(user);
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

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow();
    }
}
