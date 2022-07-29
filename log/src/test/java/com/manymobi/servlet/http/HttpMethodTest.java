package com.manymobi.servlet.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 梁建军
 * 创建日期： 2022/7/9
 * 创建时间： 下午9:55
 * @version 1.0
 * @since 1.0
 */
class HttpMethodTest {

    @Test
    void getValue() {
        assertEquals(HttpMethod.PUT.getValue(), "PUT");
    }
}