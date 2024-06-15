package com.manymobi.servlet.http.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiFunction;

/**
 * @author 梁建军
 * 创建日期： 2022/5/21
 * 创建时间： 下午7:42
 * @version 1.0
 * @since 1.0
 * 日志
 */
public abstract class Logger {

    protected final static String REQUEST_START_TIME = "REQUEST_START_TIME";

    protected static final BiFunction<Object, Object, Object> biFunction = (s1, o) -> {
        if (s1 instanceof List) {
            ((List) s1).add(o);
            return s1;
        }
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(s1);
        objects.add(o);
        return objects;
    };

    protected abstract void log(String format, Object... args);

    /**
     * 判断是否开启日志
     *
     * @return true 日志启动
     */
    protected boolean isEnabled() {
        return true;
    }

    protected void logRequest(HttpServletRequest httpRequest, LogStrategy logStrategy) {
        httpRequest.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());
        Map<String, Object> map = new HashMap<>();
        map.put("url", getURL(httpRequest));
        map.put("uri", getURI(httpRequest));
        map.put("method", httpRequest.getMethod());
        map.put("ip", httpRequest.getRemoteAddr());
        map.put("header", getHeader(httpRequest));
        String parameterString = getParameterString(httpRequest);
        if (parameterString != null) {
            map.put("parameter", parameterString);
        }
        log("request {} {} {}", httpRequest.getMethod(), getURL(httpRequest), map);
    }

    protected String getURI(HttpServletRequest httpRequest) {
        return decodeURL(httpRequest.getRequestURI());
    }

    protected String getURL(HttpServletRequest httpRequest) {
        return decodeURL(httpRequest.getRequestURL().toString());
    }

    protected String decodeURL(String content) {
        try {
            return URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return content;
        }
    }

    protected Map<String, Object> getHeader(HttpServletRequest httpRequest) {
        Enumeration<String> stringEnumeration = httpRequest.getHeaderNames();
        Map<String, Object> headerMap = new HashMap<>();
        while (stringEnumeration.hasMoreElements()) {
            String name = stringEnumeration.nextElement();
            Enumeration<String> headers = httpRequest.getHeaders(name);
            while (headers.hasMoreElements()) {
                String s = headers.nextElement();
                headerMap.merge(name, s, biFunction);
            }
        }
        return headerMap;
    }

    protected String getParameterString(HttpServletRequest httpRequest) {
        Map<String, String[]> objectMap = httpRequest.getParameterMap();
        if (objectMap == null || objectMap.isEmpty()) {
            return null;
        }
        StringBuilder parameterBuilder = new StringBuilder();
        Set<Map.Entry<String, String[]>> entries = objectMap.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            parameterBuilder
                    .append("&")
                    .append(entry.getKey())
                    .append("=");
            StringJoiner stringJoiner1 = new StringJoiner(",");
            for (String s : entry.getValue()) {
                stringJoiner1.add(s);
            }
            parameterBuilder.append(stringJoiner1);
        }
        return parameterBuilder.substring(1);
    }

    /**
     * 当请求流被读取的时候,才会触发
     *
     * @param httpRequest 请求
     * @param bodyString  请求内容
     * @param logStrategy 日志策略记录
     */
    protected void logRequestBody(HttpServletRequest httpRequest, String bodyString, LogStrategy logStrategy) {
        log("request body={}", bodyString);
    }

    protected void logResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String bodyString,
                               LogStrategy logStrategy) {
        long startTime = (long) httpRequest.getAttribute(REQUEST_START_TIME);
        long time = System.currentTimeMillis() - startTime;

        Map<String, Object> header = getHeader(httpResponse);
        log("response status={} time={}ms body={} header={}",
                httpResponse.getStatus(),
                time,
                bodyString,
                header);
    }

    protected Map<String, Object> getHeader(HttpServletResponse httpResponse) {
        Collection<String> headerNames = httpResponse.getHeaderNames();
        if (headerNames == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> headerMap = new HashMap<>();

        for (String headerName : headerNames) {
            Collection<String> collection = httpResponse.getHeaders(headerName);
            if (headerMap.containsKey(headerName)) {
                continue;
            }
            for (String s : collection) {
                headerMap.merge(headerName, s, biFunction);
            }
        }
        return headerMap;
    }

}
