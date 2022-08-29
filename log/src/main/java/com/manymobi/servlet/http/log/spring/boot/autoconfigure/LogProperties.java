package com.manymobi.servlet.http.log.spring.boot.autoconfigure;

import com.manymobi.servlet.http.HttpMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

import java.util.Collections;
import java.util.Map;

/**
 * @author 梁建军
 * 创建日期： 2018/11/7
 * 创建时间： 17:12
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = LogProperties.LOG_PREFIX)
public class LogProperties {

    public static final String LOG_PREFIX = "servlet.http.log";

    /**
     * 默认body默认缓存长度
     */
    public static final int DEFAULT_BODY_INITIAL_SIZE = 8192;
    /**
     * 默认body最大缓存长度
     */
    public static final int DEFAULT_BODY_MAX_LENGTH = 8192;

    public static final String[] READABLE_CONTENT_TYPE = {
            "application/xml",
            "application/json",
            "text/markdown",
            "text/plain"
    };
    /**
     * 默认全部记录日志的请求方法
     */
    static final HttpMethod[] READABLE_HTTP_METHODS = {
            HttpMethod.GET,
            HttpMethod.HEAD,
            HttpMethod.POST,
            HttpMethod.PUT,
            HttpMethod.PATCH,
            HttpMethod.DELETE,
            HttpMethod.OPTIONS,
            HttpMethod.TRACE
    };

    static final Strategy DEFAULT_STRATEGY;

    static {
        DEFAULT_STRATEGY = new Strategy();
        DEFAULT_STRATEGY.setOutputLog(true);
        DEFAULT_STRATEGY.setHttpMethods(READABLE_HTTP_METHODS);
        DEFAULT_STRATEGY.setRequestBody(true);
        DEFAULT_STRATEGY.setRequestContentType(READABLE_CONTENT_TYPE);
        DEFAULT_STRATEGY.setRequestBodyInitialSize(0);
        DEFAULT_STRATEGY.setRequestBodyMaxSize(DEFAULT_BODY_INITIAL_SIZE);
        DEFAULT_STRATEGY.setResponseBody(true);
        DEFAULT_STRATEGY.setResponseContentType(READABLE_CONTENT_TYPE);
        DEFAULT_STRATEGY.setResponseBodyInitialSize(DEFAULT_BODY_MAX_LENGTH);
        DEFAULT_STRATEGY.setResponseBodyMaxSize(DEFAULT_BODY_MAX_LENGTH);
        DEFAULT_STRATEGY.setCustom(Collections.emptyMap());
    }

    /**
     * 记录日志的过滤器配置
     */
    private Filter filter = new Filter();

    /**
     * 启用
     */
    private boolean enabled = true;

    /**
     * 默认日志打印策略
     */
    private Strategy defaultStrategy = new Strategy();

    /**
     * 匹配
     */
    private Map<String, PathStrategy> pathStrategy;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class PathStrategy extends Strategy {
        /**
         * 路径匹配支持通配符, 路径会根据 "/" 切割
         * "*" 或 {id}: 匹配一个
         * "**" : 匹配多个
         * 根据这个匹配路径,匹配的路径,当为null或空数组的时候,
         * 将使用所在map的key作为匹配
         */
        private String[] path;
    }

    /**
     * 策略
     */
    @Data
    public static class Strategy {
        /**
         * 是否输出日志
         */
        private Boolean outputLog;
        /**
         * 请求方式 null将代表所有方法
         */
        private HttpMethod[] httpMethods;
        /**
         * 请求body是否输出日志
         */
        private Boolean requestBody;
        /**
         * 请求需要输出日志的内容格式
         * 只支持完全相同匹配. 只比较";"前面部分
         */
        private String[] requestContentType;
        /**
         * 请求内容默认缓存长度
         * 0:使用默认的
         * -1: 根据 contentLength 初始化变量. 当 contentLength 大于 requestBodyMaxSize时候,将使用 requestBodyMaxSize 进行初始化
         */
        private Integer requestBodyInitialSize;
        /**
         * 请求内容最大缓存长度
         * 0:使用默认的
         * -1: 不限制
         */
        private Integer requestBodyMaxSize;
        /**
         * 响应body是否输出日志
         */
        private Boolean responseBody;
        /**
         * 打印响应的格式
         * 只支持完全相同匹配. 只比较";"前面部分
         */
        private String[] responseContentType;
        /**
         * 响应内容默认缓存长度
         * 0:使用默认的
         */
        private Integer responseBodyInitialSize;
        /**
         * 响应内容最大默认缓存长度
         * 0:使用默认的
         * -1: 不限制
         */
        private Integer responseBodyMaxSize;

        @Deprecated
        public Integer getResponseBodyMaxLength() {
            log.warn("getResponseBodyMaxLength 已过时,建议换成 getResponseBodyMaxSize");
            return responseBodyMaxSize;
        }

        @Deprecated
        public void setResponseBodyMaxLength(Integer responseBodyMaxLength) {
            this.responseBodyMaxSize = responseBodyMaxLength;
            log.warn("responseBodyMaxLength 已过时,建议换成 responseBodyMaxSize");
        }

        /**
         * 自定义
         */
        private Map<String, Object> custom;
    }

    @Data
    public static class Filter {
        /**
         * servlet中定义的url模式的可变集合 过滤器将根据的规范进行注册
         */
        private String[] urlPatterns = {"/*"};
        /**
         * 过滤器名称
         */
        private String name = "logFilter";
        /**
         * 设置注册bean的顺序
         * 这个默认值比 spring.sleuth.web.filterOrder 大 1 来保证日志中有 traceId
         */
        private int order = Ordered.HIGHEST_PRECEDENCE + 5 + 1;
    }
}
