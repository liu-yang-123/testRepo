package com.zcxd.base.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class RedissonSpringDataConfig {

    @Value("${redis.singleServerConfig.address}")
    private String address;

    @Value("${redis.singleServerConfig.password}")
    private String password;

    @Bean
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson(){
        Config config = new Config();
        SingleServerConfig serverConfig =  config.useSingleServer().setAddress(address);
        if(!StringUtils.isEmpty(password)){
            serverConfig.setPassword(password);
        }
        return Redisson.create(config);
    }

}