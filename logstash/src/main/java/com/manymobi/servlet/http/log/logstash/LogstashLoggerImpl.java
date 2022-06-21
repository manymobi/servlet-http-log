package com.manymobi.servlet.http.log.logstash;

import com.manymobi.servlet.http.log.LogFilter;
import com.manymobi.servlet.http.log.LogStrategy;
import com.manymobi.servlet.http.log.Logger;
import net.logstash.logback.argument.StructuredArguments;
import net.logstash.logback.marker.Markers;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 梁建军
 * 创建日期： 2022/5/21
 * 创建时间： 下午7:49
 * @version 1.0
 * @since 1.0
 */
public class LogstashLoggerImpl extends Logger {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LogFilter.class);

    @Override
    protected void logRequest(HttpServletRequest httpRequest, LogStrategy logStrategy) {
        httpRequest.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());

        Map<String, Object> map = new HashMap<>();
        map.put("url", getURL(httpRequest));
        map.put("uri", getURI(httpRequest));
        map.put("method", httpRequest.getMethod());
        map.put("ip", httpRequest.getRemoteAddr());
        Map<String, Object> header = getHeader(httpRequest);
        map.put("header", header);
        String parameterString = getParameterString(httpRequest);
        if (parameterString != null) {
            map.put("parameter", parameterString);
        }
        logger.info("request {} {} {}", httpRequest.getMethod(), getURL(httpRequest), StructuredArguments.e(map));
    }

    @Override
    protected void logRequestBody(HttpServletRequest httpRequest, String bodyString, LogStrategy logStrategy) {

        Map<String, Object> map = new HashMap<>();
        map.put("url", getURL(httpRequest));
        map.put("uri", getURI(httpRequest));
        map.put("method", httpRequest.getMethod());
        logger.info(Markers.appendEntries(map), "request body={}", bodyString);
    }

    @Override
    protected void logResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String bodyString, LogStrategy logStrategy) {
        long startTime = (long) httpRequest.getAttribute(REQUEST_START_TIME);
        long time = System.currentTimeMillis() - startTime;

        Map<String, Object> map = new HashMap<>();
        map.put("status", httpResponse.getStatus());
        Map<String, Object> header = getHeader(httpResponse);
        map.put("header", header);
        map.put("time", time);
        logger.info(Markers.appendEntries(map), "response status={} time={}ms body={} header={}",
                httpResponse.getStatus(),
                time,
                bodyString,
                header);
    }

    @Override
    protected void log(String format, Object... args) {
        logger.info(format, args);
    }
}
