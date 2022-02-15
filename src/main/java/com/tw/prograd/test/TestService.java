package com.tw.prograd.test;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    private final TestRepository repository;

    public TestService(TestRepository repository) {
        this.repository = repository;
    }

    public Test test() {
        return repository.findAll().stream().findAny().orElse(null);
    }
}
