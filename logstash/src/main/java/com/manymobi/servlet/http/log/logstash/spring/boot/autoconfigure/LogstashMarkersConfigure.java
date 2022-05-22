package com.manymobi.servlet.http.log.logstash.spring.boot.autoconfigure;

import com.manymobi.servlet.http.log.Logger;
import com.manymobi.servlet.http.log.logstash.LogstashLoggerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "servlet.http.log.logstash.enabled", matchIfMissing = true)
@ConditionalOnClass(net.logstash.logback.marker.Markers.class)
public class LogstashMarkersConfigure {

    @Bean
    public Logger logger() {
        return new LogstashLoggerImpl();
    }
}
