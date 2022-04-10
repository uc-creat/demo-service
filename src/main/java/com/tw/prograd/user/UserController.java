package com.tw.prograd.user;

import com.tw.prograd.user.dto.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/login")
    public User login(Authentication authentication) {
        UserAuthEntity user = (UserAuthEntity) authentication.getPrincipal();
        return User.builder()
                .id(user.getId())
                .username(user.getUsername()).build();

    }
}
