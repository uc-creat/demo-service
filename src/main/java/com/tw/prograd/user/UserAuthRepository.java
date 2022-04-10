package com.tw.prograd.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuthEntity, Integer> {
}
