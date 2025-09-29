package com.zcxd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zccc
 */
@EnableScheduling
@EnableFeignClients(basePackages = "com.zcxd.gun.feign")
@MapperScan("com.zcxd.gun.db.mapper")
@SpringBootApplication
public class GunApplication {
    public static void main(String[] args) {
        SpringApplication.run(GunApplication.class,args);
    }
}
