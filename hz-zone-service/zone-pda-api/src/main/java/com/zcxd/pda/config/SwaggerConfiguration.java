package com.zcxd.pda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger2配置类
 * @author songanwei
 * @date 2021/4/28 10:51
 */
@Configuration
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration implements WebMvcConfigurer {

    @Bean(value = "userApi")
    @Order(value = 1)
    public Docket groupRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(false)
                .apiInfo(groupApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zcxd.pda.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo groupApiInfo(){
        return new ApiInfoBuilder()
                .title("swagger-bootstrap-ui很棒~~~！！！")
                .description("<div style='font-size:14px;color:red;'>swagger-bootstrap-ui-demo RESTful APIs</div>")
                .termsOfServiceUrl("http://www.zcxd.com/")
//                .contact("group@qq.com")
                .version("1.0")
                .build();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(new SwaggerInterceptor()).addPathPatterns("/**");
    }
}
