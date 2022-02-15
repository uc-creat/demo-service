package com.tw.prograd.test;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Test {
    @Id
    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
