package com.gw.gwmall.rediscomm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author guanwu
 * @created on 2023-03-01 11:38:20
 **/


@ConfigurationProperties(prefix = "spring.redis.single")
@Component
@Data
public class RedisSingleProperties {
    private String host;
    private String port;
    private String max_active;
    private String max_idle;
    private String max_wait;
    private String min_idle;
    private Duration timeout;
}
