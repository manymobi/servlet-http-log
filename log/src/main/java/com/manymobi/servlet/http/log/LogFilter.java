package com.manymobi.servlet.http.log;

import com.manymobi.servlet.http.util.ContentTypeUtil;
import com.manymobi.servlet.http.util.URLPathRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author 梁建军
 * 创建日期： 2018/10/22
 * 创建时间： 14:08
 * @version 1.0
 * @since 1.0
 */
public class LogFilter implements Filter {

    private final URLPathRepository<LogStrategy> urlPathRepository;

    private final Logger logger;

    /**
     * 初始化
     * @param logger 日志
     * @param urlPathRepository url路径存储库
     */
    public LogFilter(Logger logger, URLPathRepository<LogStrategy> urlPathRepository) {
        this.logger = logger;
        this.urlPathRepository = urlPathRepository;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    protected String decodeURL(String content) {
        return URLDecoder.decode(content, StandardCharsets.UTF_8);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!logger.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String method = httpRequest.getMethod();
        Optional<LogStrategy> logStrategyOptional = urlPathRepository.find(
                method, decodeURL(httpRequest.getRequestURI())
        );
        LogStrategy logStrategy = logStrategyOptional.orElse(null);
        if (logStrategy == null || !logStrategy.isOutputLog()) {
            chain.doFilter(request, response);
            return;
        }

        logger.logRequest(httpRequest, logStrategy);
        ServletRequest requestWrapper = request;
        if (logStrategy.isRequestBody() && ContentTypeUtil.isCompatibleWith(logStrategy.getRequestContentType(), request.getContentType())) {
            requestWrapper = new LogHttpServletRequestWrapper(httpRequest, logStrategy,
                    bodyString -> logger.logRequestBody(httpRequest, bodyString, logStrategy));
        }

        LogHttpServletResponseWrapper responseWrapper = new LogHttpServletResponseWrapper(httpResponse, logStrategy,
                (String bodyString) -> logger.logResponse(httpRequest, httpResponse, bodyString, logStrategy));

        chain.doFilter(requestWrapper, responseWrapper);
        //读取没有没有达到需要记录的内容. 强制打印防止丢失日志
        if (requestWrapper instanceof LogHttpServletRequestWrapper) {
            ((LogHttpServletRequestWrapper) requestWrapper).print();
        }
        responseWrapper.print();
    }

    @Override
    public void destroy() {

    }
}
