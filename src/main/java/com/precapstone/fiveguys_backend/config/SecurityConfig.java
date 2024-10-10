package com.precapstone.fiveguys_backend.config;

import com.precapstone.fiveguys_backend.auth.PrincipalOauth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final PrincipalOauth2UserService userService;

    public SecurityConfig(PrincipalOauth2UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .formLogin(AbstractHttpConfigurer::disable)
//                .httpBasic(AbstractHttpConfigurer::disable)
//                .oauth2Login(oauth2 ->
//                        oauth2
//                                .userInfoEndpoint(userInfoEndpointConfig ->
//                                        userInfoEndpointConfig.userService(userService)))
//                .authorizeHttpRequests(auth ->
//                        auth.requestMatchers("/", "/index.html", "/oauth2/**", "/login/**") // 허용 경로 설정
//                                .permitAll()
//                                .anyRequest()
//                                .authenticated());
//        return http.build();

        http
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화 (필요 시 활성화)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/signup", "/oauth2/**", "/css/**", "/js/**").permitAll()  // 로그인, 회원가입 페이지 접근 허용
                        .anyRequest().authenticated()  // 나머지 요청은 인증 필요
                )
                .formLogin(form -> form
                        .loginPage("/login")  // 로그인 페이지 지정
                        .defaultSuccessUrl("/", true)  // 로그인 성공 시 리다이렉트
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")  // OAuth2 로그인 페이지
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(userService)  // OAuth2 사용자 서비스 설정
                        )
                );

        return http.build();
    }
}
