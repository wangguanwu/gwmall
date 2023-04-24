package com.gw.gwmall.rediscomm.config;

import cn.hutool.core.convert.Convert;
import com.gw.gwmall.rediscomm.util.RedisSingleUtil;
import com.gw.gwmall.rediscomm.util.RedisCommonUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author guanwu
 * @created on 2023-03-01 13:11:17
 **/

@Configuration
@ConditionalOnProperty(prefix = "spring.redis.single", value = "enable", havingValue = "true")
@EnableConfigurationProperties(RedisSingleProperties.class)
public class RedisSingleConfig {

    @Autowired
    private RedisSingleProperties redisSingleProperties;

    @Bean("redisSinglePool")
    @ConditionalOnProperty(prefix = "spring.redis.single", value = "enable", havingValue = "true")
    public GenericObjectPoolConfig redisSinglePool() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(Convert.toInt(redisSingleProperties.getMin_idle()));
        config.setMaxIdle(Convert.toInt(redisSingleProperties.getMax_idle()));
        config.setMaxTotal(Convert.toInt(redisSingleProperties.getMax_active()));
        config.setMaxWaitMillis(Convert.toInt(redisSingleProperties.getMax_wait()));
        return config;
    }

    @Bean("redisStandaloneConfiguration")
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        return new RedisStandaloneConfiguration(redisSingleProperties.getHost(),
                Convert.toInt(redisSingleProperties.getPort()));
    }

    @Bean("redisFactorySingle")
    public LettuceConnectionFactory redisFactorySingle(@Qualifier("redisSinglePool") GenericObjectPoolConfig config,
                                                       @Qualifier("redisStandaloneConfiguration") RedisStandaloneConfiguration redisStandaloneConfiguration) {//注意传入的对象名和类型RedisStandaloneConfiguration
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration);
    }

    /**
     * 单实例redis数据源
     *
     * @param connectionFactory
     * @return
     */
    @Bean("redisSingleTemplate")
    public RedisTemplate<String, Object> redisSingleTemplate(
            @Qualifier("redisFactorySingle")LettuceConnectionFactory connectionFactory) {
        return RedisCommonUtil.createRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisSingleUtil redisSingleUtil(){
        return new RedisSingleUtil();
    }

}
