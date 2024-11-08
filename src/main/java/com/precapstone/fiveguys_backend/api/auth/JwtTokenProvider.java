package com.precapstone.fiveguys_backend.api.auth;

import com.precapstone.fiveguys_backend.api.member.MemberRepository;
import com.precapstone.fiveguys_backend.common.auth.CustomUserDetails;
import com.precapstone.fiveguys_backend.entity.Member;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;

@RequiredArgsConstructor
@Service
public class JwtTokenProvider {
    private final MemberRepository memberRepository;
    @Value("${jwt.secret.key}")
    private String secretKey;
//    @Value("${jwt.secret.access_token_validity}") // 30분
    private String accessTokenValidity = "60000";
    @Value("${jwt.secret.refresh_token_validity}") // 7일
    private String refreshTokenValidity;

    private final CustomUserDetailService customUserDetailService;

    private Key key = null;

    @PostConstruct
    protected void init() {
        key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA512");
    }

    public String createAccessToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getMember().getUserId();
        claims.put("auth", authentication.getAuthorities());
        //TODO 토큰 유효기간 설정 오류
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(accessTokenValidity)))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getMember().getUserId();

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(refreshTokenValidity)))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
                return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT token");
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token");
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid token");
        }
        return false;
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserPk(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = customUserDetailService.loadUserByUserId(getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Authentication getAuthenticationByAccesstoken(String accessToken) {
        String userId = (String) getClaimsFromToken(accessToken).get("sub");
        UserDetails userDetails = customUserDetailService.loadUserByUserId(userId);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }

    public String getEmailFromToken(String token) {
        String userId = getClaimsFromToken(token).getSubject();
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        return optionalMember.map(Member::getEmail).orElse(null);
    }
    public String getUserIdFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }
}