package com.tw.prograd.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.prograd.dto.ErrorResponse;
import com.tw.prograd.user.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private final String userName = "admin@test.com";
    private final String password = "password";
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private ObjectMapper mapper;
    private String validAuthorizationToken;
    private String invalidAuthorizationToken;

    @BeforeEach
    void setUp() {
        UserEntity userEntity = UserEntity.builder()
                .id(1)
                .email(userName).build();
        UserEntity savedUser = userRepository.save(userEntity);

        UserAuthEntity userAuthEntity = UserAuthEntity.builder()
                .id(savedUser.getId())
                .user(savedUser)
                .password(password).build();
        userAuthRepository.save(userAuthEntity);

        validAuthorizationToken = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes());
        invalidAuthorizationToken = Base64.getEncoder().encodeToString(("user@unknown.com" + ":" + password).getBytes());
    }

    @Test
    void shouldBeAbleToLoginWithValidCredential() throws Exception {
        User user = User.builder()
                .id(1)
                .username(userName).build();

        mvc.perform(get("/users/login")
                        .header("Authorization", "Basic " + validAuthorizationToken))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(user)));
    }

    @Test
    void shouldNotBeAbleToLoginWithoutValidCredential() throws Exception {
        ErrorResponse error = ErrorResponse.builder()
                .errorCode(UNAUTHORIZED.value())
                .message(UNAUTHORIZED.getReasonPhrase()).build();

        mvc.perform(get("/users/login")
                        .header("Authorization", "Basic " + invalidAuthorizationToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(mapper.writeValueAsString(error)));
    }
}