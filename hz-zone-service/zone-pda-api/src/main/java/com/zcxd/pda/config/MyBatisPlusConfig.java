package com.zcxd.pda.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @ClassName MyBatisPlusConfig
 * @Description MyBatis属性配置
 * @author 秦江南
 * @Date 2021年5月10日下午2:12:45
 */
@Configuration
public class MyBatisPlusConfig {
 

    /**
     * 配置自动填充
     *
     * @return
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                // 数据类型要与fileName一致
                Long time = System.currentTimeMillis();
                this.setFieldValByName("createTime",time,metaObject);
//                this.setFieldValByName("createUser",UserContextHolder.getUserId(),metaObject);
                this.setFieldValByName("updateTime", System.currentTimeMillis(),metaObject);
//                this.setFieldValByName("updateUser",UserContextHolder.getUserId(),metaObject);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                // 数据类型要与fileName一致
                this.setFieldValByName("updateTime", System.currentTimeMillis(),metaObject);
//                this.setFieldValByName("updateUser",UserContextHolder.getUserId(),metaObject);
            }
        };
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
