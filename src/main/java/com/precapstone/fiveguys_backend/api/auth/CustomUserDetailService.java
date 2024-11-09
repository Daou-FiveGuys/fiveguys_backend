package com.precapstone.fiveguys_backend.api.auth;

import com.precapstone.fiveguys_backend.api.user.UserRepository;
import com.precapstone.fiveguys_backend.common.auth.CustomUserDetails;
import com.precapstone.fiveguys_backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserId("fiveguys_"+username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return new CustomUserDetails(user);
    }

    public UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with user id: " + userId));
        return new CustomUserDetails(user);
    }
}
