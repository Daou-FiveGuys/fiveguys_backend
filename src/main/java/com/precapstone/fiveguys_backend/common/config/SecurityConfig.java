package com.precapstone.fiveguys_backend.common.config;

import com.precapstone.fiveguys_backend.api.service.CustomUserDetailService;
import com.precapstone.fiveguys_backend.api.service.PrincipalOauth2UserService;
import com.precapstone.fiveguys_backend.common.auth.CustomUserDetails;
import com.precapstone.fiveguys_backend.common.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final PrincipalOauth2UserService oauth2UserService;
    private final CustomUserDetailService customUserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        //TODO 페이지 권한 수정
                        .requestMatchers("/verification").hasRole(UserRole.VISITOR.getRole())
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login-process")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            // 로그인 후 추가 로직
                            // 비즈니스 로직 예: 이메일 인증 필요 여부 체크
                            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                            if (isEmailVerificationRequired(userDetails)) {
                                request.getSession().setAttribute("email", userDetails.getMember().getEmail());
                                response.sendRedirect("/verification");
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)  // OAuth2 로그인 성공 시 리다이렉트
                        .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService))
                )
                .sessionManagement(session -> session
                        .sessionFixation().newSession()  // 새 세션을 생성
                );
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // 세션 생성 정책 설정
//                );
        return http.build();
    }

    private boolean isEmailVerificationRequired(CustomUserDetails userDetails) {
        // 예시: 사용자의 역할이 "UNVERIFIED"인 경우 이메일 인증이 필요하다고 가정
        return userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_"+UserRole.VISITOR.getRole()));
    }

}
