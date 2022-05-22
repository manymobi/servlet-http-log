package com.manymobi.servlet.http.log.slf4j;

import com.manymobi.servlet.http.log.LogFilter;
import com.manymobi.servlet.http.log.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author 梁建军
 * 创建日期： 2022/5/21
 * 创建时间： 下午7:49
 * @version 1.0
 * @since 1.0
 */
public class Slf4jLoggerImpl extends Logger {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LogFilter.class);

    @Override
    protected boolean isEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    protected void log(String format, Object... args) {
        logger.info(format, args);
    }
}
