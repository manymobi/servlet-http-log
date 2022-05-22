package com.manymobi.servlet.http.log;

import com.manymobi.servlet.http.HttpMethod;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * @author 梁建军
 * 创建日期： 2022/5/14
 * 创建时间： 下午9:07
 * @version 1.0
 * @since 1.0
 */
@Getter
@Builder
public class LogStrategy {
    /**
     * 输出日志
     */
    private final boolean outputLog;
    /**
     * 请求方式 null将代表所有方法
     */
    private final HttpMethod[] httpMethods;
    /**
     * 请求body是否输出日志
     */
    private final boolean requestBody;
    /**
     * 请求需要输出日志的内容格式
     */
    private final Set<String> requestContentType;
    /**
     * 请求内容默认缓存长度
     * 0:使用默认的
     */
    private final int requestBodyInitialSize;
    /**
     * 请求内容最大缓存长度
     * 0:使用默认的
     * -1: 不限制
     */
    private final int requestBodyMaxSize;
    /**
     * 响应body是否输出日志
     */
    private final boolean responseBody;
    /**
     * 打印响应的格式
     */
    private final Set<String> responseContentType;
    /**
     * 响应内容默认缓存长度
     */
    private final int responseBodyInitialSize;
    /**
     * 响应内容最大默认缓存长度
     */
    private final int responseBodyMaxLength;

    /**
     * 自定义
     */
    private final Map<String, Object> custom;
}
