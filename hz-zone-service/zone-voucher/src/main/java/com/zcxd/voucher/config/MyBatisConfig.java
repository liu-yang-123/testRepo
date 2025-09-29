package com.zcxd.voucher.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zccc
 */
@Configuration
public class MyBatisConfig {

    /**
     * 引入分页拦截器，不然total参数异常
     */

    @Bean
    public PaginationInterceptor mybatisPlusInterceptor() {
        //引入分页拦截器
        return new PaginationInterceptor();
    }

}