package com.manymobi.servlet.http.log.spring.boot.autoconfigure;

import com.manymobi.servlet.http.HttpMethod;
import com.manymobi.servlet.http.log.LogFilter;
import com.manymobi.servlet.http.log.LogStrategy;
import com.manymobi.servlet.http.log.Logger;
import com.manymobi.servlet.http.log.slf4j.Slf4jLoggerImpl;
import com.manymobi.servlet.http.util.URLPathRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author 梁建军
 * 创建日期： 2018/10/22
 * 创建时间： 15:10
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@ConditionalOnClass(javax.servlet.Filter.class)
@ConditionalOnProperty(name = "servlet.http.log.enabled", matchIfMissing = true)
@EnableConfigurationProperties(LogProperties.class)
public class ServletHttpLogAutoConfigure {


    @Bean
    public URLPathRepository<LogStrategy> urlPathRepository(LogProperties logProperties) {
        LogProperties.Strategy defaultStrategy = logProperties.getDefaultStrategy();
        Map<String, LogProperties.PathStrategy> pathStrategyMap = logProperties.getPathStrategy();
        if (pathStrategyMap == null) {
            pathStrategyMap = new HashMap<>();
        }
        if (pathStrategyMap.isEmpty()) {
            LogProperties.PathStrategy pathStrategy = new LogProperties.PathStrategy();
            pathStrategyMap.put("/**", pathStrategy);
            pathStrategyMap.put("/", pathStrategy);
        }
        URLPathRepository.Builder<LogStrategy> builder = new URLPathRepository.Builder<>();
        pathStrategyMap.forEach((key, pathStrategy) -> {

            LogStrategy logStrategy = getLogStrategy(key, pathStrategy, defaultStrategy);
            for (HttpMethod httpMethod : logStrategy.getHttpMethods()) {
                if (pathStrategy.getPath() != null && pathStrategy.getPath().length > 0) {
                    for (String path : pathStrategy.getPath()) {
                        builder.addPath(httpMethod, path, logStrategy);
                    }
                } else {
                    builder.addPath(httpMethod, key, logStrategy);
                }
            }
        });

        return builder.build();
    }

    private static LogStrategy getLogStrategy(String key, LogProperties.PathStrategy pathStrategy,
                                              LogProperties.Strategy defaultStrategy) {


        LogStrategy.LogStrategyBuilder builder = LogStrategy.builder();

        builder.outputLog(get(pathStrategy.getOutputLog(),
                defaultStrategy.getOutputLog(),
                LogProperties.DEFAULT_STRATEGY.getOutputLog()));

        HttpMethod[] httpMethods = get(pathStrategy.getHttpMethods(),
                defaultStrategy.getHttpMethods(),
                LogProperties.DEFAULT_STRATEGY.getHttpMethods());
        if (Arrays.stream(httpMethods).anyMatch(httpMethod -> httpMethod == HttpMethod.ALL)) {
            builder.httpMethods(LogProperties.READABLE_HTTP_METHODS);
        } else {
            builder.httpMethods(httpMethods);
        }

        builder.requestBody(get(pathStrategy.getRequestBody(),
                defaultStrategy.getRequestBody(),
                LogProperties.DEFAULT_STRATEGY.getRequestBody()));

        builder.requestContentType(
                Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                        get(pathStrategy.getRequestContentType(),
                                defaultStrategy.getRequestContentType(),
                                LogProperties.DEFAULT_STRATEGY.getRequestContentType()))
                )));

        Integer requestBodyMaxSize = get(pathStrategy.getRequestBodyMaxSize(),
                defaultStrategy.getRequestBodyMaxSize(),
                LogProperties.DEFAULT_STRATEGY.getRequestBodyMaxSize());
        builder.requestBodyMaxSize(requestBodyMaxSize);
        Integer requestBodyInitialSize = get(pathStrategy.getRequestBodyInitialSize(),
                defaultStrategy.getRequestBodyInitialSize(),
                LogProperties.DEFAULT_STRATEGY.getRequestBodyInitialSize());
        builder.requestBodyInitialSize(requestBodyInitialSize);
        //当初始化的body大小超过 最大时候,初始化的body转变成 最大的容量
        if (requestBodyInitialSize > requestBodyMaxSize) {
            builder.responseBodyInitialSize(requestBodyMaxSize);
            log.warn("servlet-http-log 配置中 {} 的 requestBodyInitialSize({}) 已超过 requestBodyMaxSize({}) 初始化容量将使用 " +
                    "requestBodyMaxSize({})", key, requestBodyInitialSize, requestBodyMaxSize, requestBodyMaxSize);
        } else {
            builder.responseBodyInitialSize(requestBodyInitialSize);
        }


        builder.responseBody(get(pathStrategy.getResponseBody(),
                defaultStrategy.getRequestBody(),
                LogProperties.DEFAULT_STRATEGY.getRequestBody()));

        builder.responseContentType(
                Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                        get(pathStrategy.getResponseContentType(),
                                defaultStrategy.getResponseContentType(),
                                LogProperties.DEFAULT_STRATEGY.getResponseContentType()))
                )));


        Integer responseBodyMaxLength = get(pathStrategy.getResponseBodyMaxLength(),
                defaultStrategy.getResponseBodyMaxLength(),
                LogProperties.DEFAULT_STRATEGY.getResponseBodyMaxLength());
        builder.responseBodyMaxLength(responseBodyMaxLength);
        Integer responseBodyInitialSize = get(pathStrategy.getResponseBodyInitialSize(),
                defaultStrategy.getResponseBodyInitialSize(),
                LogProperties.DEFAULT_STRATEGY.getResponseBodyInitialSize());
        //当初始化的body大小超过 最大时候,初始化的body转变成 最大的容量
        if (responseBodyInitialSize > responseBodyMaxLength) {
            builder.responseBodyInitialSize(responseBodyMaxLength);
            log.warn("servlet-http-log 配置中 {} 的 responseBodyInitialSize({}) 已超过responseBodyMaxLength({}) 初始化容量将使用 " +
                    "responseBodyMaxLength({})", key, responseBodyInitialSize, responseBodyMaxLength, responseBodyMaxLength);
        } else {
            builder.responseBodyInitialSize(responseBodyInitialSize);
        }

        builder.custom(Collections.unmodifiableMap(get(pathStrategy.getCustom(),
                defaultStrategy.getCustom(),
                LogProperties.DEFAULT_STRATEGY.getCustom())));

        return builder.build();
    }

    /**
     * 返回第一个不为null的对象
     *
     * @param t1  变量1
     * @param t2  变量2
     * @param t3  变量3
     * @param <T> 类型
     * @return 返回的变量
     */
    private static <T> T get(T t1, T t2, T t3) {
        if (t1 != null) {
            return t1;
        }
        if (t2 != null) {
            return t2;
        }
        return t3;
    }

    /**
     * 返回第一个不为null 且不为 0的值
     *
     * @param t1 变量1
     * @param t2 变量2
     * @param t3 变量3
     * @return 返回的变量
     */
    private static Integer get(Integer t1, Integer t2, Integer t3) {
        if (t1 != null) {
            if (t1 != 0) {
                return t1;
            }
        }
        if (t2 != null) {
            if (t2 != 0) {
                return t2;
            }
        }
        return t3;
    }

    @Bean
    @ConditionalOnMissingBean
    public Logger slf4jLogger() {
        return new Slf4jLoggerImpl();
    }

    @Bean
    public FilterRegistrationBean<LogFilter> logFilterRegistration(LogProperties logProperties,
                                                                   Logger logger,
                                                                   URLPathRepository<LogStrategy> urlPathRepository) {
        FilterRegistrationBean<LogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LogFilter(logger, urlPathRepository));
        LogProperties.Filter filter = logProperties.getFilter();
        registration.addUrlPatterns(filter.getUrlPatterns());
        registration.setName(filter.getName());
        registration.setOrder(filter.getOrder());
        return registration;
    }

}
