package com.manymobi.servlet.http.util;

import com.manymobi.servlet.http.HttpMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;


/**
 * @author 梁建军
 * 创建日期： 2022/5/21
 * 创建时间： 下午11:23
 * @version 1.0
 * @since 1.0
 */
class URLPathRepositoryTest {

    URLPathRepository<String> repository;

    @BeforeEach
    public void init() throws IOException {
        URLPathRepository.Builder<String> stringBuilder = new URLPathRepository.Builder<>();
        stringBuilder.addPath(HttpMethod.GET, "com/a/b/c/d/**", "1")
                .addPath(HttpMethod.GET, "com/b/c", "2")
                .addPath(HttpMethod.GET, "/api/order/{id}", "/api/order/{id}")
                .addPath(HttpMethod.GET, "/api/order/{id}/cancel", "/api/order/{id}/cancel")
                .addPath(HttpMethod.GET, "/api/order/{id}/item/add", "/api/order/{id}/item/add")
                .addPath(HttpMethod.GET,"/api/a/**","/api/a/**")
                .addPath(HttpMethod.GET,"/api/a/*/a","/api/a/*/a")
        ;
        repository = stringBuilder.build();
    }


    @Test
    public void test() {
        Optional<String> s = repository.find(HttpMethod.GET, "com/a/b/c/d/c.d.d");
        Assertions.assertEquals(s.orElse(null), "1");
    }

    @Test
    public void test1() {
        Optional<String> s = repository.find(HttpMethod.GET, "com/b/c");
        Assertions.assertEquals(s.orElse(null), "2");
    }

    @Test
    public void test3() {
        Optional<String> s = repository.find(HttpMethod.GET, "com/b/c/b");
        Assertions.assertNull(s.orElse(null));
    }

    @Test
    public void test4() {
        Optional<String> s = repository.find(HttpMethod.GET, "/api/order/2342242");
        Assertions.assertEquals(s.orElse(null), "/api/order/{id}");
    }

    @Test
    public void test5() {
        Optional<String> s = repository.find(HttpMethod.GET, "/api/order/2342242/cancel");
        Assertions.assertEquals(s.orElse(null), "/api/order/{id}/cancel");
    }

    @Test
    public void test6() {
        Optional<String> s = repository.find(HttpMethod.GET, "/api/order/2342242/item/add");
        Assertions.assertEquals(s.orElse(null), "/api/order/{id}/item/add");
    }

    @Test
    public void test61() {
        Optional<String> s = repository.find("GET", "/api/order/2342242/item/add");
        Assertions.assertEquals(s.orElse(null), "/api/order/{id}/item/add");
    }

    @Test
    public void test7() {
        Optional<String> s = repository.find(HttpMethod.POST, "/api/order/2342242/item/add");
        Assertions.assertNull(s.orElse(null));
    }
    @Test
    public void test8() {
        Optional<String> s = repository.find(HttpMethod.GET, "/api/a/a");
        Assertions.assertEquals(s.orElse(null),"/api/a/**");
    }
}