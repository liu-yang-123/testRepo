package com.zcxd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @desc 网关服务引导入口类
 * @author songanwei
 * @date 2021/4/15 15:57
 */
@EnableFeignClients(basePackages = "com.zcxd.gateway.provider")
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class,args);
    }

}
