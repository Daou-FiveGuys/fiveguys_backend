package com.precapstone.fiveguys_backend.api.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.precapstone.fiveguys_backend.api.dto.LoginInfoDTO;
import com.precapstone.fiveguys_backend.api.dto.AuthResponseDTO;
import com.precapstone.fiveguys_backend.api.member.MemberRepository;
import com.precapstone.fiveguys_backend.api.redis.RedisService;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.ResponseMessage;
import com.precapstone.fiveguys_backend.common.auth.CustomUserDetails;
import com.precapstone.fiveguys_backend.common.enums.LoginType;
import com.precapstone.fiveguys_backend.common.enums.UserRole;
import com.precapstone.fiveguys_backend.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final CustomUserDetailService customUserDetailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;
    @Value("${spring.security.oauth2.client.registration.naver.redirect_uri}")
    private String naverRedirectUri;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    private LoginType type;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String tokenUri;
    private String userInfoRequestUri;

    /**
     * 일반 로그인
     * @param loginInfoDTO 이메일, 비밀번호
     * @return CommonResponse
     */
    public CommonResponse signIn(LoginInfoDTO loginInfoDTO){
        Optional<Member> optionalMember = memberRepository.findByUserId("fiveguys_" + loginInfoDTO.getEmail());
        if(optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (passwordEncoder.matches(loginInfoDTO.getPassword(), member.getPassword())) {
                String responseMessage = null;
                if (member.getUserRole() == UserRole.USER)
                    responseMessage = ResponseMessage.SUCCESS;
                else
                    responseMessage = ResponseMessage.EMAIL_VERIFICAITION_REQUIRED;
                Map<String, String> tokens = usersAuthorization(member);
                return CommonResponse.builder()
                        .code(200)
                        .message(responseMessage)
                        .data(AuthResponseDTO.builder()
                                .accessToken(tokens.get("access_token"))
                                .build()
                        )
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
     * 소셜 연동 로그인
     * @param code 소셜 로그인 코드
     * @param type 네이버, 구글...
     * @return CommonResponse
     */
    public CommonResponse signIn(String code, LoginType type){
        this.type = type;
        switch (type){
            case NAVER -> {
                clientId = naverClientId;
                clientSecret = naverClientSecret;
                redirectUri = naverRedirectUri;
                tokenUri = "https://nid.naver.com/oauth2.0/token";
                userInfoRequestUri = "https://openapi.naver.com/v1/nid/me";
            }
            case GOOGLE -> {
                clientId = googleClientId;
                clientSecret = googleClientSecret;
                redirectUri = googleRedirectUri;
                tokenUri = "https://oauth2.googleapis.com/token";
                userInfoRequestUri = "https://www.googleapis.com/oauth2/v2/userinfo";
            }
        }

        String accessToken = getAccessToken(code);
        Member member = getUserInfo(accessToken);
        member = register(member);
        Map<String,String> tokens = usersAuthorization(member);

        return CommonResponse.builder()
                .code(200)
                .data(AuthResponseDTO.builder()
                        .accessToken(tokens.get("access_token"))
                        .build())
                .build();
    }

    public CommonResponse logout(String accessToken) {
        try{
            String userId = (String) jwtTokenProvider.getClaimsFromToken(accessToken).get("sub");
            redisService.delete(userId + ":refreshToken");
            return CommonResponse.builder()
                    .code(200)
                    .message("logged out successfully")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponse.builder()
                    .code(401)
                    .message("logged out failed")
                    .build();
        }
    }

    /**
     * access, refresh 토큰 발급
     * @param member
     * @return {"access_token", "refresh_token"}
     */
    public Map<String, String> usersAuthorization(Member member) {
        UserDetails userDetails = customUserDetailService.loadUserByUserId(member.getUserId());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
        redisService.setDataExpire(member.getUserId() + "_refreshToken", refreshToken, 604800);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        return tokens;
    }

    /**
     * 소셜 회원가입 처리
     * @param member 소셜에서 받은 회원 정보
     * @return 회원가입 된 회원 정보
     */
    private Member register(Member member){
        return memberRepository.findByUserId(member.getUserId())
                .orElseGet(() -> memberRepository.save(member));
    }

    /**
     * 액세스 토큰으로 회원 정보 조회
     * @param accessToken 액세스 토큰
     * @return 회원 정보
     */
    private Member getUserInfo(String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;

        response = restTemplate.exchange(userInfoRequestUri, HttpMethod.GET, request, String.class);

        String responseBody = response.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> attributes = objectMapper.readValue(responseBody, Map.class);
            if(type == LoginType.NAVER)
                attributes = (Map<String,Object>) attributes.get("response");
            return Member.builder()
                    .userId(type.getType().toLowerCase()+"_"+attributes.get("id").toString())
                    .userRole(UserRole.USER)
                    .name(attributes.get("name").toString())
                    .email(attributes.get("email").toString())
                    .provider(type.getType().toLowerCase())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Invalid access token");
    }

    /**
     * 소셜 로그인 시 액세스 토큰 발급
     * @param code 소셜 로그인 코드
     * @return 소셜 액세스 토큰
     */
    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("client_secret", clientSecret);
        body.add("code", code);

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST,request, JsonNode.class);
            String accessToken = responseNode.getBody().get("access_token").asText();
            return accessToken;
        } catch (RuntimeException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 액세스 토큰으로 이메일 인증 여부 조회
     * @param accessToken 액세스 토큰
     * @return 이메일 인증 여부
     */
    public CommonResponse isVerified(String accessToken) {
        if(!jwtTokenProvider.validateToken(accessToken)){
            return CommonResponse.builder()
                    .data(401)
                    .message("Invalid access token")
                    .build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) jwtTokenProvider.getAuthentication(accessToken).getPrincipal();
        if(userDetails.getMember().getUserRole() != UserRole.USER){
            return CommonResponse.builder()
                    .data(400)
                    .message("Not verified")
                    .build();
        } else {
            return CommonResponse.builder()
                    .data(200)
                    .message("Verified")
                    .build();
        }
    }

    /**
     * 액세스 토큰 재발급
     * @param accessToken 유저 아이디
     * @return 액세스 토큰
     */
    public CommonResponse refreshAccessToken(String accessToken){
        try {
            String userId = (String) jwtTokenProvider.getClaimsFromToken(accessToken).get("sub");
            UserDetails userDetails = customUserDetailService.loadUserByUserId(userId);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    "",
                    userDetails.getAuthorities()
            );
            // 레디스에 refresh_token 존재하는지
            String storedRefreshToken = redisService.get(userId + "_refreshToken");
            if (storedRefreshToken == null || !jwtTokenProvider.validateToken(storedRefreshToken)) {
                // 없거나 만료되었으면 리프레시 토큰 발급, 저장 -> 액세스 토큰 발급 후 리턴
                String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
                redisService.setDataExpire(userId + "_refreshToken", refreshToken, 604800000);
            }
            String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
            // 새로운 액세스 토큰 반환
            return CommonResponse.builder()
                    .code(200)
                    .data(AuthResponseDTO.builder()
                            .accessToken(newAccessToken)
                            .build())
                    .build();
        } catch (RuntimeException e) {
            return CommonResponse.builder()
                    .code(403)
                    .message("Bad Request")
                    .build();
        }
    }
}
