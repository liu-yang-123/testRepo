package com.zcxd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import javax.annotation.Resource;

@Configuration
public class RedisConfig {
    @Value("${wx.mp.redisConfig.password}")
    private String password;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setPassword(password);
        return factory;
    }
}