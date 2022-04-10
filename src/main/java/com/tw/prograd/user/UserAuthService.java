package com.tw.prograd.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserAuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not present"));

        return userAuthRepository.findById(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not present"));

    }
}
