package com.manymobi.servlet.http;

import lombok.Getter;

/**
 * @author 梁建军
 * 创建日期： 2022/5/9
 * 创建时间： 下午8:16
 * @version 1.0
 * @since 1.0
 */
@Getter
public enum HttpMethod {
    ALL("*"),
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }


}
