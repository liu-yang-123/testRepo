package com.zcxd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@MapperScan("com.zcxd.db.mapper")
@SpringBootApplication
@EnableScheduling
public class WxMpDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxMpDemoApplication.class, args);
    }
}
