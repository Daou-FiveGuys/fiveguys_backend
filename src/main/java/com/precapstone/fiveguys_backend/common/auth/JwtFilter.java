package com.precapstone.fiveguys_backend.common.auth;

import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final List<String> EXCLUDE_URL = List.of(
            "/login", "/signup",
            "/swagger-ui/**", "/api-docs/**", "/swagger-resources/**",
            "/api/v1/oauth/refresh-token", "/api/v1/user/signup",
            "/api/v1/oauth/naver", "/api/v1/oauth/google", "/api/v1/oauth", "/**"
    );

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (pathMatchesExcludePattern(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token not found\"}");
            return;
        }

        token = token.replace(TOKEN_PREFIX, "");
        if (jwtTokenProvider.validateToken(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid Basic Authentication\"}");
            return;
        }


        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    private boolean pathMatchesExcludePattern(String requestURI) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String excludeUrl : EXCLUDE_URL) {
            if (pathMatcher.match(excludeUrl, requestURI)) {
                return true;
            }
        }
        return false;
    }
}

