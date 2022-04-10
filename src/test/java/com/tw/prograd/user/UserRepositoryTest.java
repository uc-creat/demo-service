package com.tw.prograd.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenSaved_thenFindByEmail() {
        UserEntity toBeSavedUser = UserEntity.builder().id(1).email("some@test.net").build();

        userRepository.save(toBeSavedUser);

        var savedUser = userRepository.findByEmail("some@test.net");

        assertTrue(savedUser.isPresent());
        assertEquals(toBeSavedUser, savedUser.get());
    }
}