package com.manymobi.servlet.http.log.spring.boot.autoconfigure;

import com.manymobi.servlet.http.HttpMethod;
import com.manymobi.servlet.http.log.LogFilter;
import com.manymobi.servlet.http.log.LogStrategy;
import com.manymobi.servlet.http.log.Logger;
import com.manymobi.servlet.http.log.slf4j.Slf4jLoggerImpl;
import com.manymobi.servlet.http.util.URLPathRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author 梁建军
 * 创建日期： 2022/7/9
 * 创建时间： 下午9:16
 * @version 1.0
 * @since 1.0
 */
class ServletHttpLogAutoConfigureTest {

    @Test
    void urlPathRepository() {
        ServletHttpLogAutoConfigure servletHttpLogAutoConfigure = new ServletHttpLogAutoConfigure();
        LogProperties logProperties = new LogProperties();
        URLPathRepository<LogStrategy> repository = servletHttpLogAutoConfigure.urlPathRepository(logProperties);
        Optional<LogStrategy> logStrategy = repository.find(HttpMethod.GET, "/api/a");

        assertTrue(logStrategy.isPresent());
    }

    @Test
    void slf4jLogger() {
        ServletHttpLogAutoConfigure servletHttpLogAutoConfigure = new ServletHttpLogAutoConfigure();
        Logger logger = servletHttpLogAutoConfigure.slf4jLogger();
        assertNotNull(logger);
    }

    @Test
    void logFilterRegistration() {
        ServletHttpLogAutoConfigure autoConfigure = new ServletHttpLogAutoConfigure();
        LogProperties logProperties = new LogProperties();
        URLPathRepository<LogStrategy> repository = autoConfigure.urlPathRepository(logProperties);

        FilterRegistrationBean<LogFilter> logFilterFilterRegistrationBean =
                autoConfigure.logFilterRegistration(logProperties, new Slf4jLoggerImpl(), repository);

        assertEquals(logFilterFilterRegistrationBean.getOrder(), Ordered.HIGHEST_PRECEDENCE + 5 + 1);

        for (String urlPattern : logFilterFilterRegistrationBean.getUrlPatterns()) {
            assertEquals(urlPattern, "/*");
        }

    }

    @Test
    void get() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method get = ServletHttpLogAutoConfigure.class.getDeclaredMethod("get",
                Integer.class, Integer.class, Integer.class);
        get.setAccessible(true);
        assertEquals(get.invoke(null, 1, 2, 3), 1);
        assertEquals(get.invoke(null, 0, 2, 3), 2);
        assertEquals(get.invoke(null, null, 2, 3), 2);
        assertEquals(get.invoke(null, null, 0, 3), 3);
        assertEquals(get.invoke(null, null, null, 3), 3);
        assertNull(get.invoke(null, null, null, null));
        assertNull(get.invoke(null, 0, null, null));
        assertNull(get.invoke(null, 0, 0, null));
        assertEquals(get.invoke(null, 3, null, null), 3);
    }

    @Test
    void getObject() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method get = ServletHttpLogAutoConfigure.class.getDeclaredMethod("get",
                Object.class, Object.class, Object.class);
        get.setAccessible(true);
        assertEquals(get.invoke(null, "1", "2", "3"), "1");
        assertEquals(get.invoke(null, null, "2", "3"), "2");
        assertEquals(get.invoke(null, null, null, "3"), "3");
        assertEquals(get.invoke(null, "1", null, null), "1");
        assertEquals(get.invoke(null, "1", "null", null), "1");
    }

    @Test
    void getLogStrategy() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method get = ServletHttpLogAutoConfigure.class.getDeclaredMethod("getLogStrategy",
                String.class, LogProperties.PathStrategy.class, LogProperties.Strategy.class);
        get.setAccessible(true);

        LogProperties.PathStrategy strategy = new LogProperties.PathStrategy();
        strategy.setOutputLog(true);
        strategy.setHttpMethods(new HttpMethod[]{});
        strategy.setRequestBody(true);
        strategy.setRequestContentType(new String[]{});
        strategy.setRequestBodyInitialSize(1000);
        strategy.setRequestBodyMaxSize(10);
        strategy.setResponseBody(true);
        strategy.setResponseContentType(new String[]{});
        strategy.setResponseBodyInitialSize(10000);
        strategy.setResponseBodyMaxSize(1000);
        strategy.setCustom(Collections.emptyMap());

        LogStrategy key = (LogStrategy) get.invoke(null, "key", strategy, strategy);
        assertEquals(strategy.getOutputLog(), key.isOutputLog());
        Assertions.assertEquals(strategy.getHttpMethods(), key.getHttpMethods());
        Assertions.assertEquals(strategy.getRequestBody(), key.isRequestBody());
        Assertions.assertEquals(new HashSet<>(Arrays.asList(strategy.getRequestContentType())),
                key.getRequestContentType());
        Assertions.assertEquals(10, key.getRequestBodyInitialSize());
        Assertions.assertEquals(strategy.getRequestBodyMaxSize(), key.getRequestBodyMaxSize());
        Assertions.assertEquals(strategy.getResponseBody(), key.isResponseBody());
        Assertions.assertEquals(new HashSet<>(Arrays.asList(strategy.getResponseContentType())), key.getResponseContentType());
        Assertions.assertEquals(1000, key.getResponseBodyInitialSize());
        Assertions.assertEquals(strategy.getResponseBodyMaxSize(), key.getResponseBodyMaxSize());
        Assertions.assertEquals(strategy.getCustom(), key.getCustom());
        System.out.println(key);
    }
}