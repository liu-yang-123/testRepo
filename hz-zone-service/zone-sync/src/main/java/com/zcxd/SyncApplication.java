package com.zcxd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 同步引导类
 * @author songanwei
 * @date 2021/4/15 17:58
 */
@EnableScheduling
@SpringBootApplication
@MapperScan(basePackages = "com.zcxd.sync.mapper")
public class SyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyncApplication.class,args);
    }
}
