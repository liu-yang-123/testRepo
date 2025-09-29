package com.zcxd.pda.filter;

import com.zcxd.pda.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 数据AES解密过滤器
 * @author songanwei
 * @date 2021-10-11
 */
@Slf4j
@Order(1)
@Component
public class ReqAesFilter implements Filter {

    @Value("${encrypt.aes}")
    private int aesEncrypt;

    @Value("${encrypt.key}")
    private String aesKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        //解析request post body数据并重写request body流数据，注意 request body流只能读取一次，无法重复读取
        String method = servletRequest.getMethod();
        if (HttpMethod.POST.toString().equalsIgnoreCase(method)){
            try {
                request = new BodyRequestWrapper(servletRequest,aesEncrypt,aesKey);
            } catch (Exception e) {
                throw new BusinessException("数据解密异常");
            }
        }
        chain.doFilter(request,response);
    }


}
