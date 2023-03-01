package com.gw.gwmall.rediscomm.config;


import com.gw.gwmall.rediscomm.util.RedisOpsExtUtil;
import com.gw.gwmall.rediscomm.util.TemplateUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisExtConifg {

    @Value("${spring.redis.cluster.nodes}")
    private String redisNodes;

    @Value("${spring.redis.password:nopwd}")
    private String redisPass;

    @Autowired
    private Environment environment;

    @Autowired
    @Qualifier("redisFactoryCluster")
    private RedisConnectionFactory connectionFactory;

    @Bean("redisCluster")
    @Primary
    public RedisTemplate<String,Object> redisTemplate(){
        return TemplateUtil.createRedisTemplate(connectionFactory);
    }

    @Bean("redisClusterConfig")
    @Primary
    public RedisClusterConfiguration redisClusterConfig() {

        Map<String, Object> source = new HashMap<>(8);
        source.put("spring.redis.cluster.nodes", environment.getProperty("spring.redis.cluster.nodes"));
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(
                new MapPropertySource("RedisClusterConfiguration", source));
        redisClusterConfiguration.setPassword(environment.getProperty("spring.redis.password"));
        return redisClusterConfiguration;
    }

    @Bean("redisClusterPool")
    @Primary
    @ConfigurationProperties(prefix = "spring.redis.cluster.lettuce.pool")
    public GenericObjectPoolConfig redisClusterPool() {
        return new GenericObjectPoolConfig();
    }

    @Bean("redisFactoryCluster")
    @Primary
    public LettuceConnectionFactory lettuceConnectionFactory(
            @Qualifier("redisClusterPool") GenericObjectPoolConfig redisPool,
            @Qualifier("redisClusterConfig") RedisClusterConfiguration redisClusterConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(redisPool).build();
        return new LettuceConnectionFactory(redisClusterConfig, clientConfiguration);
    }

    @Bean
    public RedisOpsExtUtil redisOpsUtil(){
        return new RedisOpsExtUtil();
    }

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        ClusterServersConfig clusterServersConfig = config.useClusterServers();
        for (String node: redisNodes.split(",")){
            clusterServersConfig.addNodeAddress("redis://"+node);
        }
        if(!"nopwd".equals(redisPass)){
            clusterServersConfig.setPassword(redisPass);
        }
        return Redisson.create(config);
    }
}
