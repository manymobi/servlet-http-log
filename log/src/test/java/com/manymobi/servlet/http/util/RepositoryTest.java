package com.manymobi.servlet.http.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 梁建军
 * 创建日期： 2022/4/1
 * 创建时间： 下午5:47
 * @version 1.0
 * @since 1.0
 */
public class RepositoryTest {

    Repository<String> repository;

    @BeforeEach
    public void init() throws IOException {
        Repository.Builder<String> stringBuilder = new Repository.Builder<>();
        stringBuilder.add("com/a/b/c/d/**", "1")
                .add("com/b/c", "2")
                .add("/api/order/*", "/api/order/{id}")
                .add("/api/order/*/cancel", "/api/order/{id}/cancel")
                .add("/api/order/*/item/add", "/api/order/{id}/item/add")
        ;
        repository = stringBuilder.build();
    }


    @Test
    public void test() {
        Optional<String> s = repository.find("com/a/b/c/d/c.d.d");
        Assertions.assertEquals(s.orElse(null), "1");
    }

    @Test
    public void test1() {
        Optional<String> s = repository.find("com/b/c");
        Assertions.assertEquals(s.orElse(null), "2");
    }

    @Test
    public void test3() {
        Optional<String> s = repository.find("com/b/c/b");
        Assertions.assertNull(s.orElse(null));
    }

    @Test
    public void test4() {
        Optional<String> s = repository.find("/api/order/2342242");
        Assertions.assertEquals(s.orElse(null), "/api/order/{id}");
    }

    @Test
    public void test5() {
        Optional<String> s = repository.find("/api/order/2342242/cancel");
        Assertions.assertEquals(s.orElse(null), "/api/order/{id}/cancel");
    }

    @Test
    public void test6() {
        Optional<String> s = repository.find("/api/order/2342242/item/add");
        Assertions.assertEquals(s.orElse(null), "/api/order/{id}/item/add");
    }
}
