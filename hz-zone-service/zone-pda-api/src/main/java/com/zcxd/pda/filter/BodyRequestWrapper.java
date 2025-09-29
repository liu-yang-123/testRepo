package com.zcxd.pda.filter;

import com.zcxd.common.util.AESUtils;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * request body包装器
 * @author
 * @date 2021-10-11
 */
@Slf4j
public class BodyRequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;

    /**
     *
     * @param request  请求对象
     * @param aesEncrypt 是否加密 1=加解密 0=不加解密
     * @param aesKey 密钥
     * @throws Exception
     */
    public BodyRequestWrapper(HttpServletRequest request,int aesEncrypt, String aesKey) throws Exception {
        super(request);
        String method = request.getMethod();
        String url = request.getRequestURI();
        //将request body中的流转为字符串
        BufferedReader reader = request.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        String bodyStr = stringBuilder.toString();
        if (bodyStr.length() > 0 && aesEncrypt == 1) {
            // 解密数据
            String decode = AESUtils.decryptResponse(bodyStr,aesKey);
            // 重新赋值
            body = decode.getBytes(StandardCharsets.UTF_8);
        } else {
            body = bodyStr.getBytes();
        }
        //记录body数据日志
        log.info("请求路径:{},请求方法:{},请求参数:{}",url,method,body);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    /**
     * 在使用@RequestBody注解的时候，框架是调用了getInputStream()方法，所以我们要重写这个方法
     * @return
     */
    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

}
