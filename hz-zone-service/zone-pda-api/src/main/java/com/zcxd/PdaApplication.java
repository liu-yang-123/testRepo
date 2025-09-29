package com.zcxd;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author songanwei
 * @date 2021/4/21 9:24
 */
@MapperScan("com.zcxd.db.mapper")
@ComponentScan( {"com.zcxd.pda","com.zcxd.db","com.zcxd.common"})
@Slf4j
@SpringBootApplication
public class PdaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdaApplication.class,args);
    }
}
