package com.manymobi.servlet.http.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 梁建军
 * 创建日期： 2022/5/21
 * 创建时间： 下午11:17
 * @version 1.0
 * @since 1.0
 */
class ContentTypeUtilTest {

    @Test
    void isCompatibleWith() {
        String[] strings = {"application/xml",
                "application/json",
                "text/markdown",
                "text/plain"};
        boolean compatibleWith = ContentTypeUtil.isCompatibleWith(new HashSet<>(Arrays.asList(strings)),
                "application/json;charset=UTF-8");
        assertTrue(compatibleWith);
    }
}