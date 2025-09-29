package com.zcxd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
	
/**
 * @author songanwei
 * @date 2021/4/21 9:24
 */
@EnableScheduling
@MapperScan("com.zcxd.db.mapper")
@SpringBootApplication
public class BaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseApplication.class,args);
    }
	
}
