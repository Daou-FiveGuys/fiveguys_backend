package com.precapstone.fiveguys_backend.config;

import com.precapstone.fiveguys_backend.auth.PrincipalOauth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final PrincipalOauth2UserService userService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorizeRequest ->
                authorizeRequest.requestMatchers("/shopping").authenticated()
                                .anyRequest().permitAll())
            .headers(c -> c.frameOptions(
                    HeadersConfigurer.FrameOptionsConfig::disable).disable())
            .sessionManagement(c ->
                    c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(handler -> handler.authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException, IOException {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UnAuthorized");
                    }
                }))
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                .userService(userService)));
        return http.build();
    }
}
